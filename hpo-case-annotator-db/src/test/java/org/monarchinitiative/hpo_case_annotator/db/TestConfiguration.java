package org.monarchinitiative.hpo_case_annotator.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class TestConfiguration {

    /**
     * @return in-memory database for testing
     */
    public static DataSource dataSource() {
        String jdbcUrl = "jdbc:h2:mem:hca;INIT=CREATE SCHEMA IF NOT EXISTS HCA";
        final HikariConfig config = new HikariConfig();
        config.setUsername("sa");
        config.setPassword("sa");
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(jdbcUrl);

        return new HikariDataSource(config);
    }
}
