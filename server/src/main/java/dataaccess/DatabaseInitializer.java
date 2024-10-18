package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initializeDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        createTables();
    }

    private static void createTables() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            // create users table here
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(255) PRIMARY KEY," +
                    "email VARCHAR(255) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL)";
            try (PreparedStatement stmt = conn.prepareStatement(createUsersTable)) {
                stmt.executeUpdate();
            }

            // create games table here
            String createGamesTable = "CREATE TABLE IF NOT EXISTS games (" +
                    "gameID INT PRIMARY KEY," +
                    "whiteUsername VARCHAR(255)," +
                    "blackUsername VARCHAR(255)," +
                    "gameName VARCHAR(255) NOT NULL," +
                    "gameState TEXT NOT NULL," +
                    "FOREIGN KEY (whiteUsername) REFERENCES users(username)," +
                    "FOREIGN KEY (blackUsername) REFERENCES users(username))";
            try (PreparedStatement stmt = conn.prepareStatement(createGamesTable)) {
                stmt.executeUpdate();
            }

            // create auths table here
            String createAuthsTable = "CREATE TABLE IF NOT EXISTS auths (" +
                    "authToken VARCHAR(255) PRIMARY KEY," +
                    "username VARCHAR(255) NOT NULL)";
            try (PreparedStatement stmt = conn.prepareStatement(createAuthsTable)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating tables: " + e.getMessage());
        }
    }
}