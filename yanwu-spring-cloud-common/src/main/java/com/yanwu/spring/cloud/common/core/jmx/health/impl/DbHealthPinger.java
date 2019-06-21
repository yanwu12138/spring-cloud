package com.yanwu.spring.cloud.common.core.jmx.health.impl;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public final class DbHealthPinger {

    final static String TEST_SQL = "select 1";

    private DbHealthPinger() {
    }

    public static boolean pingDataSource(final DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeQuery(TEST_SQL);
                return true;
            }
        } catch (SQLException e) {
            log.warn("Checking {} failed.", dataSource, e);
            return false;
        }
    }

}
