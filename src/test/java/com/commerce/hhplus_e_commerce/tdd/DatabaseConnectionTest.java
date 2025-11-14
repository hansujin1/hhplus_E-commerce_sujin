package com.commerce.hhplus_e_commerce.tdd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testConnection() {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("✅ DB 연결 성공: " + connection.getMetaData().getURL());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
