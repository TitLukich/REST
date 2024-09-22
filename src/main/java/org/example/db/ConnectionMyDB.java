package org.example.db;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionMyDB implements ConnectionManager {

    private HikariDataSource dataSource;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ConnectionMyDB(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                throw new SQLException("DataSource is closed or not initialized");
            }
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting database connection", e);
        }
    }
}
