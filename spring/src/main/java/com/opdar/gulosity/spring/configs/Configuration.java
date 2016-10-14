package com.opdar.gulosity.spring.configs;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class Configuration {
    private JdbcConfiguration jdbcConfiguration;

    public JdbcConfiguration getJdbcConfiguration() {
        return jdbcConfiguration;
    }

    public void setJdbcConfiguration(JdbcConfiguration jdbcConfiguration) {
        this.jdbcConfiguration = jdbcConfiguration;
    }
}
