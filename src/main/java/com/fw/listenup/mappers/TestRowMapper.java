package com.fw.listenup.mappers;

import org.springframework.jdbc.core.RowMapper;

import com.fw.listenup.models.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestRowMapper implements RowMapper<Test> {

    @Override
    public Test mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        Test entity = new Test(id, name, email);
        return entity;
    }
}