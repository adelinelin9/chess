package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    public void testRegisterSuccess() throws DataAccessException {
        AuthData result = userService.register("alice", "password", "alice@email.com");
        assertNotNull(result);
    }

    @Test
    public void testRegisterFail() throws DataAccessException {
        userService.register("alice", "password", "alice@email.com");
        try {
            userService.register("alice", "password", "alice@email.com");
            fail("Should have thrown an exception");
        } catch (DataAccessException e) {
            assertEquals("already taken", e.getMessage());
        }
    }

    @Test
    public void testLoginSuccess() throws DataAccessException {
        userService.register("bob", "1234", "bob@email.com");
        AuthData result = userService.login("bob", "1234");
        assertNotNull(result);
        assertEquals("bob", result.username());
    }

    @Test
    public void testLoginFail() throws DataAccessException {
        userService.register("bob", "1234", "bob@email.com");
        try {
            userService.login("bob", "wrongpassword");
            fail("Should have thrown an exception for wrong password");
        } catch (DataAccessException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testLogoutSuccess() throws DataAccessException {
        AuthData auth = userService.register("carol", "pass", "carol@email.com");
        userService.logout(auth.authToken());
        // if no exception was thrown, logout worked
        assertTrue(true);
    }

    @Test
    public void testLogoutFail() {
        try {
            userService.logout("fakebadtoken");
            fail("Should have thrown an exception");
        } catch (DataAccessException e) {
            assertNotNull(e.getMessage());
        }
    }

}
