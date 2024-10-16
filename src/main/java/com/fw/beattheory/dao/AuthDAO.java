package com.fw.beattheory.dao;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fw.beattheory.models.auth.EmailVerificationDetail;
import com.fw.beattheory.models.auth.PasswordVerificationDetail;
import com.fw.beattheory.models.auth.UserAuthenticationDetail;
import com.fw.beattheory.util.CommonUtil;
import com.fw.beattheory.util.JwtUtil;

import ch.qos.logback.classic.Logger;

//DAO class that handles db transactions related to authentication
@Repository
public class AuthDAO extends DAOBase{
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthDAO.class);

    //Tests the connection to the db and return true if query is successful
    public boolean testConnection(){
        boolean connectionIsAvailable = false;
        try(Connection con = getConnection()){
            logger.info("Testing db connection");
            PreparedStatement stmt = con.prepareStatement("select 1");
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                String val = rs.getString(1);
                if(!CommonUtil.isEmpty(val)){
                    return true;
                }
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return connectionIsAvailable;
    }
    //Returns the username and password of a user based on email 
    public UserAuthenticationDetail getUserAuthenticationDetail(String email){
        UserAuthenticationDetail uad = null;
        try(Connection con = getConnection()){
            logger.info("Calling db for user authentication details");
            PreparedStatement stmt = con.prepareStatement("select id, username, email, password, salt from user where email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            //Should only return one value since email is a unique field
            if(rs.next()){
                int userId = rs.getInt("id");
                email = rs.getString("email");
                String username = rs.getString("username");
                String pw = rs.getString("password");
                String salt = rs.getString("salt");

                uad = new UserAuthenticationDetail(userId, email, username, pw, salt);
            } else{
                logger.error("No credential match found, invalid auth attempt");
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return uad;
    }

    //Returns the username and email from the user table
    public Map<String, String> getExistingEmailAndUsername(String email, String username){
        Map<String, String> credMap = new HashMap<String, String>();
        String existingEmail = "";
        String existingUsername = "";
        try(Connection con = getConnection()){
            logger.info("Calling db for credential lookup");
            PreparedStatement emailStmt = con.prepareStatement("select email from user where email = ?");
            emailStmt.setString(1, email);
            
            ResultSet rs = emailStmt.executeQuery();
            if(rs.next()){
                existingEmail = rs.getString("email");
            }

            PreparedStatement usernameStmt = con.prepareStatement("select username from user where username = ?");
            usernameStmt.setString(1, username);
            
            ResultSet rs2 = usernameStmt.executeQuery();
            if(rs2.next()){
                existingUsername = rs2.getString("username");
            }

            logger.info("Existing email and username: " + existingEmail + " : " + existingUsername);
        } catch(SQLException e){
            logConnectionError(e);
        }

        credMap.put("email", existingEmail);
        credMap.put("username", existingUsername);
        return credMap;
    }

    //Makes insert statement into user table with new credentials
    public boolean registerNewUser(String email, String username, String password, String salt, byte[] image){
        //Predefined fields for new accounts
        boolean res = false;
        int role = 0;
        int rankId = 1;
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        int status = 1;
        int verificationStatus = 0;

        try(Connection con = getConnection()){
            logger.info("Attempting to insert new user into db");
            String insertQuery = "insert into user (username, password, email, role, rank_id, created_at, status, verification_status, salt, admin, profile_picture) " +
                                 "values (?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(insertQuery);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setInt(4, role);
            stmt.setInt(5, rankId);
            stmt.setTimestamp(6, createdAt);
            stmt.setInt(7, status);
            stmt.setInt(8, verificationStatus);
            stmt.setString(9, salt);
            stmt.setInt(10, 0);
            stmt.setBlob(11, new ByteArrayInputStream(image));

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                logger.info("New user was successfully inserted!");
                res = true;
            } else {
               logger.error("Something went wrong, no user was inserted");
            }

        } catch(SQLException e){
            logConnectionError(e);
        }

        return res;
    }

    //Makes insert statement into login_attempts table with new credentials
    public boolean logAuthAttempt(String email, boolean valid, boolean userExists){
        boolean res = false;

        //Predefined values
        Timestamp logTime = new Timestamp(System.currentTimeMillis());

        try(Connection con = getConnection()){
            logger.info("Logging auth attempt in db");
            String query = "insert into login_attempts (login_time, email, valid_auth, user_exists) values (?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setTimestamp(1, logTime);
            stmt.setString(2, email);
            stmt.setBoolean(3, valid);
            stmt.setBoolean(4, userExists);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                logger.info("Auth attempt was successful logged");
                res = true;
            } else{
                logger.error("Something went wrong, the auth attempt was not logged");
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return res;
    }

    //Generates a new email token to be used for verification
    public boolean generateEmailVerificationToken(String email) throws SQLException {
        boolean res = false;

        //Predefined values
        String token = UUID.randomUUID().toString();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Date currentDate = new Date(currentTime.getTime());
        Date nextDayDate = new Date(currentDate.getTime() + (24 * 60 * 60 * 1000)); // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds
        Timestamp expirationTime = new Timestamp(nextDayDate.getTime());

        //Check for pre-existing token.  If one exists, delete it and recall method
        if(emailTokenExists(email)){
            boolean deleted = deleteEmailToken(email);
            if(!deleted) throw new SQLException("Token exists, but deletion failed");
            // if(deleted) generateEmailVerificationToken(email);
        }

        try(Connection con = getConnection()){
            logger.info("Attempting to insert new email verification token");
            String query = "insert into email_verification (uid, email, expires_by) values (?,?,?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, token);
            stmt.setString(2, email);
            stmt.setTimestamp(3, expirationTime);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                logger.info("Insert into email_verification successful for user " + email);
                res = true;

            }
        } catch(SQLException e){
            logConnectionError(e);
        }
        return res;
    }

    //Checks for existing verification token.  If it exists, deletes current one and replaces it with 
    //new token
    private boolean emailTokenExists(String email){
        logger.info("Checking for existing verification token");
        boolean res = false;

        try(Connection con = getConnection()){
            String query = "select count(*) from email_verification where email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                if(count > 0){
                    logger.info("Email token exists, generate new one");
                    res = true;
                } else{ 
                    logger.info("Email token does not currently exist");
                }
            } else{
                logger.error("There was an error when checking for existing email token.  Generating" +
                " new email token");
                res = true;
            }

        } catch(SQLException e){
            logConnectionError(e);
        }
        return res;
    }

    //Deletes old verification email token from the database
    private boolean deleteEmailToken(String email){
        logger.info("Deleting current token for user " + email);
        boolean res = false;

        try(Connection con = getConnection()){
            String query = "delete from email_verification where email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                logger.info("Deleted existing token");
                res = true;
            } else{
                logger.error("No rows were found to be deleted for user " + email);
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return res;
    }

    //Retrieves verification token from database
    public String getVerificationToken(String email){
        logger.info("Retrieving token for user " + email);
        String res = "";

        try(Connection con = getConnection()){
            String query = "select uid from email_verification where email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                res = rs.getString("uid");
            } else{
                logger.error("No uid found for user " + email);
            }

        } catch(SQLException e){
            logConnectionError(e);
        }
        return res;
    }

    //Generates a new email token to be used for password reset attempt
    public boolean generatePasswordResetEmailVerificationToken(String email) throws SQLException {
        boolean res = false;

        //Pre-defined values
        String token = UUID.randomUUID().toString();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Date currentDate = new Date(currentTime.getTime());
        Date nextDayDate = new Date(currentDate.getTime() + (15 * 60 * 1000)); // 15 minutes * 60 seconds * 1000 milliseconds
        Timestamp expirationTime = new Timestamp(nextDayDate.getTime());
    
        //Check for pre-existing token.  If one exists, delete it and recall method
        if(passwordResetEmailTokenExists(email)){
            boolean deleted = deletePasswordResetEmailToken(email);
            if(!deleted) throw new SQLException("Token exists, but deletion failed");
        }

        try(Connection con = getConnection()){
            logger.info("Attempting to insert new password token");
            String query = "insert into password_reset (uid, email, expires_by) values (?,?,?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, token);
            stmt.setString(2, email);
            stmt.setTimestamp(3, expirationTime);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                logger.info("Insert into password_reset successful for user " + email);
                res = true;

            }
        } catch(SQLException e){
            logConnectionError(e);
        }
        return res;
    }

    //Checks for existing password reset token.  If it exists, deletes current one and replaces it with 
    //new token
    private boolean passwordResetEmailTokenExists(String email){
        logger.info("Checking for existing verification token");
        boolean res = false;

        try(Connection con = getConnection()){
            String query = "select count(*) from password_reset where email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                if(count > 0){
                    logger.info("Email token exists, generate new one");
                    res = true;
                } else{ 
                    logger.info("Email token does not currently exist");
                }
            } else{
                logger.error("There was an error when checking for existing email token.  Generating" +
                " new email token");
                res = true;
            }

        } catch(SQLException e){
            logConnectionError(e);
        }
        return res;
    }

    //Deletes old password reset email token from the database
    private boolean deletePasswordResetEmailToken(String email){
        logger.info("Deleting current password reset token for user " + email);
        boolean res = false;

        try(Connection con = getConnection()){
            String query = "delete from password_reset where email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                logger.info("Deleted existing token");
                res = true;
            } else{
                logger.error("No rows were found to be deleted for user " + email);
            }
        }catch(SQLException e){
            logConnectionError(e);
        }

        return res;
    }

    //Deletes old password reset email token after successful password change
    public boolean deletePasswordResetEmailTokenAfterSuccess(String token){
        logger.info("Deleting current password reset token for token " + token);
        boolean res = false;

        try(Connection con = getConnection()){
            String query = "delete from password_reset where uid = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, token);

            int rowsAffected = stmt.executeUpdate();
            res = rowsAffected > 0;
        }catch(SQLException e){
            logConnectionError(e);
        }

        return res;
    }

    //Retrieves password reset token from database
    public String getPasswordResetVerificationToken(String email){
        logger.info("Retrieving password reset token for user " + email);
        String res = "";

        try(Connection con = getConnection()){
            String query = "select uid from password_reset where email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                res = rs.getString("uid");
            } else{
                logger.error("No uid found for user " + email);
            }

        } catch(SQLException e){
            logConnectionError(e);
        }
        return res;
    }

    //Gets the email verification details to be sent to the registration template
    public EmailVerificationDetail getEmailVerificationDetail(String email){
        logger.info("Retrieving email verification details for user " + email);
        EmailVerificationDetail evd = null;

        try(Connection con = getConnection()){
            String query = "select user.username, user.email, email_verification.uid from user inner join " + 
                            "email_verification on user.email = email_verification.email where user.email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String username = rs.getString("username");
                String email2 = rs.getString("email");
                String uid = rs.getString("uid");

                //Make sure no fields are null
                if(CommonUtil.isEmpty(username) ||  CommonUtil.isEmpty(email2) || CommonUtil.isEmpty(uid)){
                    logger.error("One of the email verification details fields is null");
                    return evd;
                }

                evd = new EmailVerificationDetail(email2, username, uid);
            }
            
        } catch(SQLException e){
            logConnectionError(e);
        }
        logger.info("Returning email verification details to service layer");
        return evd;
    }

    //Gets the password verification details to be sent to the password reset email template.
    public PasswordVerificationDetail getPasswordVerificationDetail(String email){
        logger.info("Retrieving password verification details for user " + email);
        PasswordVerificationDetail pvd = null;

        try(Connection con = getConnection()){
            String query = "select uid, email, expires_by from password_reset where email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String email2 = rs.getString("email");
                String uid = rs.getString("uid");
                Timestamp expiresBy = rs.getTimestamp("expires_by");

                //Make sure no fields are null
                if(CommonUtil.isEmpty(email2) || CommonUtil.isEmpty(uid) || CommonUtil.isEmpty(expiresBy.toString())){
                    logger.error("One of the password reset verification details fields is null");
                    return pvd;
                }

                pvd = new PasswordVerificationDetail(email2, uid, expiresBy);
            }
            
        } catch(SQLException e){
            logConnectionError(e);
        }
        logger.info("Returning password verification details to service layer");
        return pvd;
    }

    //Lookups email verification detail with uid and returns the email, uid, and timestamp
    public EmailVerificationDetail getEmailToken(String uid){
        logger.info("Getting email token from verification link");
        EmailVerificationDetail evd = null;

        try(Connection con = getConnection()){
            String query = "select user.email, email_verification.uid, email_verification.expires_by from user inner join " + 
                            "email_verification on user.email = email_verification.email where email_verification.uid = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, uid);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String email = rs.getString("email");
                String uid2 = rs.getString("uid");
                Timestamp expiresBy = rs.getTimestamp("expires_by");

                //Make sure no fields are null
                if(CommonUtil.isEmpty(email) ||  CommonUtil.isEmpty(uid2) || expiresBy == null){
                    logger.error("One of the email verification details fields is null");
                    return evd;
                }

                evd = new EmailVerificationDetail(email, uid2, expiresBy);
            }
            
        } catch(SQLException e){
            logConnectionError(e);
        }
        logger.info("Returning email verification details to service layer");
        return evd;
    }

    //Update user verification status
    public boolean updateUserVerificationStatus(String email){
        logger.info("Updating verification status for user " + email);
        boolean res = false;

        try(Connection con = getConnection()){
            String query = "update user set verification_status = 1 where email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                logger.info("Successfully updated user's verification status");
                res = true;
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return res;
    }

    //Remove email verification record
    public boolean removeEmailVerificationRecord(String uid){
        logger.info("Removing verification record for uid " + uid);
        boolean res = false;

        try(Connection con = getConnection()){
            String query = "delete from email_verification where uid = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, uid);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0) logger.info("Successfully removed verification record");
            else logger.info("No record was found for uid " + uid);
            res = true;
        } catch(SQLException e){
            logConnectionError(e);
            res = false;
        }

        return res;
    }

    //Looks up user verification status and returns result
    public String checkUserVerificationStatus(String email){
        String res = "";

        try(Connection con = getConnection()){
            String query = "select verification_status from user where email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int status = rs.getInt("verification_status");

                //Confirms that the user has verified their email address
                if(status == 1){
                    return JwtUtil.generateEmailToken(email, 3600000);
                }
            } else{
                logger.error("No results were found, returning false");
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return res;
    }

    //Looks for the username in the db and returns whether it exists or not
    public boolean checkIfUsernameTaken(String username) {
        boolean isTaken = false;
        try(Connection con = getConnection()){
            String query = "select username from user where username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String currUsername = rs.getString("username");
                if(CommonUtil.isEmpty(currUsername)){
                    isTaken = false;
                } else {
                    isTaken = true;
                }
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return isTaken;
    }

    //Update username and return true if the update was successful
    public boolean updateUsername(String id, String username){
        boolean isUpdated = false;
        try(Connection con = getConnection()){
            String query = "update user set username = ? where id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, id);

            int rowsAffected = stmt.executeUpdate();
            isUpdated = rowsAffected > 0;
        } catch (SQLException e){
            logConnectionError(e);
        }

        return isUpdated;
    }

    //Looks for existing password verification detail record given a token
    public PasswordVerificationDetail getPasswordVerificationRecord(String token){
        logger.info("Attempting to retrieve password verification record from db...");
        logger.info("token is " + token);
        PasswordVerificationDetail pvd = null;
        try(Connection con = getConnection()){
            String query = "select email, uid, expires_by from password_reset where uid = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, token);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String email = rs.getString("email");
                String uid = rs.getString("uid");
                Timestamp expiresBy = rs.getTimestamp("expires_by");
                pvd = new PasswordVerificationDetail(email, uid, expiresBy);
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return pvd;
    }

    //Update the user's password in the user table
    public boolean updatePassword(String email, String pw, String salt){
        boolean isUpdated = false;
        try(Connection con = getConnection()){
            String query = "update user set password = ?, salt = ? where email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, pw);
            stmt.setString(2, salt);
            stmt.setString(3, email);

            int rowsAffected = stmt.executeUpdate();
            isUpdated = rowsAffected > 0;
        } catch (SQLException e){
            logConnectionError(e);
        }


        return isUpdated;
    }

    //Update the user's password in the user table given their ID
    public boolean updatePasswordWithID(String id, String pw, String salt){
        boolean isUpdated = false;

        try(Connection con = getConnection()){
            String query = "update user set password = ?, salt = ? where id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, pw);
            stmt.setString(2, salt);
            stmt.setString(3, id); 

            int rowsAffected = stmt.executeUpdate();
            isUpdated = rowsAffected > 0;
        } catch (SQLException e){
            logConnectionError(e);
        }


        return isUpdated;
    }

    //Update the verification status to '1' for the user
    public boolean verifyUserEmail(String id){
        boolean isVerified = false;

        try(Connection con = getConnection()){
            String query = "update user set verification_status = 1 where id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, id);

            int rowsAffected = stmt.executeUpdate();
            isVerified = rowsAffected > 0;
        } catch (SQLException e){
            logConnectionError(e);
        }

        return isVerified;
    }

    //Get the admin status of the user
    public int adminAuthenticate(String username){
        int isAdmin = 0;
        try(Connection con = getConnection()){
            String query = "select admin from user where username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                isAdmin = rs.getInt("admin");
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return isAdmin;
    }

    //Get password and salt based on username
    public ArrayList<String> getPasswordDetails(String username){
        ArrayList<String> passwordDetails = new ArrayList<String>();
        String pw = "";
        String salt = "";
        try(Connection con = getConnection()){
            String query = "select password, salt from user where username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                pw = rs.getString("password");
                salt = rs.getString("salt");

                //Only add password details if values returned non-empty
                if(!CommonUtil.isEmpty(pw) && !CommonUtil.isEmpty(salt)){
                    passwordDetails.add(pw);
                    passwordDetails.add(salt);
                }
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return passwordDetails;
    }
}
