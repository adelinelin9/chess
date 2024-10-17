package dataaccess;


import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAOMySQL extends AuthDAO{

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.getAuthToken());
            stmt.setString(2, auth.getUsername());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth in database: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auths WHERE authToken =?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AuthData(
                        rs.getString("authToken"),
                        rs.getString("username")
                );
            } else {
                throw new DataAccessException("Auth token not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error querying auth: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            int deletedAuths = stmt.executeUpdate();
            if (deletedAuths == 0) {
                throw new DataAccessException("No auths to delete");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth from database: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auths";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auths from database: " + e.getMessage());
        }
    }

}