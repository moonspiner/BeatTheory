package com.fw.listenup.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fw.listenup.mappers.TestRowMapper;
import com.fw.listenup.models.Test;

import org.springframework.jdbc.core.JdbcTemplate;


@Repository
public class LoggingDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LoggingDAO(DataSource ds){
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    public List<Test> getAllTestEntities() {
        String sql = "SELECT * FROM test";
        return jdbcTemplate.query(sql, new TestRowMapper());
    }
}
