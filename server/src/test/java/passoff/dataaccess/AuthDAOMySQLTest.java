package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOMySQLTest {

    private AuthDAOMySQL authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DatabaseInitializer.initializeDatabase();
        authDAO = new AuthDAOMySQL();
        authDAO.clear();
    }

    // create auth success
    @Test
    public void testCreateAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth("authToken");
        assertNotNull(retrievedAuth);
        assertEquals("authToken", retrievedAuth.getAuthToken());
    }

    // create auth failure (auth already taken)
    @Test
    public void testCreateAuthFailure() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }

    // get auth success
    @Test
    public void testGetAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth("authToken");
        assertNotNull(retrievedAuth);
        assertEquals("authToken", retrievedAuth.getAuthToken());
        assertEquals("username", retrievedAuth.getUsername());
    }

    // get auth failure (auth not found)
    @Test

    public void testGetAuthFailure() throws DataAccessException {
        assertThrows(DataAccessException.class, () ->authDAO.getAuth("authToken"));
    }

    // delete auth success
    @Test
    public void testDeleteAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);

        authDAO.deleteAuth("authToken");
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("authToken"));
    }

    // delete auth failure (auth is still in database)
    @Test
    public void testDeleteAuthFailure() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("authToken")); // fix this
    }

    // clear auths
    @Test
    public void testClearAuthsSuccess() throws DataAccessException {
        AuthData auth1 = new AuthData("authToken1", "username1");
        AuthData auth2 = new AuthData("authToken2", "username");
        authDAO.createAuth(auth1);
        authDAO.createAuth(auth2);

        authDAO.clear();
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("authToken1"));
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("authToken2"));
    }
}