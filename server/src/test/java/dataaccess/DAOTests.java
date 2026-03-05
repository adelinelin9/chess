package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTests {

    private MySqlUserDAO userDAO;
    private MySqlAuthDAO authDAO;
    private MySqlGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        gameDAO = new MySqlGameDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    // ---- MySqlUserDAO tests ----

    @Test
    public void testCreateUserSuccess() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@email.com");
        userDAO.createUser(user);
        UserData result = userDAO.getUser("alice");
        assertNotNull(result);
    }

    @Test
    public void testCreateUserFail() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@email.com");
        userDAO.createUser(user);
        try {
            userDAO.createUser(user);
            fail("Should have thrown an exception for duplicate user");
        } catch (DataAccessException e) {
            assertEquals("already taken", e.getMessage());
        }
    }

    @Test
    public void testGetUserSuccess() throws DataAccessException {
        UserData user = new UserData("bob", "pass", "bob@email.com");
        userDAO.createUser(user);
        UserData result = userDAO.getUser("bob");
        assertNotNull(result);
        assertEquals("bob", result.username());
    }

    @Test
    public void testGetUserFail() throws DataAccessException {
        UserData result = userDAO.getUser("nonexistentuser");
        assertNull(result);
    }

    @Test
    public void testClearUsersSuccess() throws DataAccessException {
        UserData user = new UserData("carol", "pass", "carol@email.com");
        userDAO.createUser(user);
        userDAO.clear();
        UserData result = userDAO.getUser("carol");
        assertNull(result);
    }

    @Test
    public void testClearUsersAllowsReinsert() throws DataAccessException {
        UserData user = new UserData("carol", "pass", "carol@email.com");
        userDAO.createUser(user);
        userDAO.clear();
        userDAO.createUser(user);
        UserData result = userDAO.getUser("carol");
        assertNotNull(result);
    }

    // ---- MySqlAuthDAO tests ----

    @Test
    public void testCreateAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "dave");
        authDAO.createAuth(auth);
        AuthData result = authDAO.getAuth("token123");
        assertNotNull(result);
    }

    @Test
    public void testCreateAuthFail() throws DataAccessException {
        AuthData auth = new AuthData("token123", "dave");
        authDAO.createAuth(auth);
        try {
            authDAO.createAuth(auth);
            fail("Should have thrown an exception for duplicate token");
        } catch (DataAccessException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testGetAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("mytoken", "eve");
        authDAO.createAuth(auth);
        AuthData result = authDAO.getAuth("mytoken");
        assertNotNull(result);
        assertEquals("eve", result.username());
    }

    @Test
    public void testGetAuthFail() throws DataAccessException {
        AuthData result = authDAO.getAuth("badtoken");
        assertNull(result);
    }

    @Test
    public void testDeleteAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("deletetoken", "frank");
        authDAO.createAuth(auth);
        authDAO.deleteAuth("deletetoken");
        AuthData result = authDAO.getAuth("deletetoken");
        assertNull(result);
    }

    @Test
    public void testDeleteAuthFail() {
        try {
            authDAO.deleteAuth("nonexistenttoken");
            fail("Should have thrown an exception for deleting nonexistent token");
        } catch (DataAccessException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testClearAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("sometoken", "grace");
        authDAO.createAuth(auth);
        authDAO.clear();
        AuthData result = authDAO.getAuth("sometoken");
        assertNull(result);
    }

    @Test
    public void testClearAuthAllowsReinsert() throws DataAccessException {
        AuthData auth = new AuthData("sometoken", "grace");
        authDAO.createAuth(auth);
        authDAO.clear();
        authDAO.createAuth(auth);
        AuthData result = authDAO.getAuth("sometoken");
        assertNotNull(result);
    }

    // ---- MySqlGameDAO tests ----

    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("mygame");
        assertTrue(gameID > 0);
    }

    @Test
    public void testCreateGameFail() {
        try {
            gameDAO.createGame(null);
            fail("Should have thrown an exception for null game name");
        } catch (DataAccessException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testGetGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("testgame");
        GameData result = gameDAO.getGame(gameID);
        assertNotNull(result);
        assertEquals("testgame", result.gameName());
    }

    @Test
    public void testGetGameFail() throws DataAccessException {
        GameData result = gameDAO.getGame(99999);
        assertNull(result);
    }

    @Test
    public void testListGamesSuccess() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        List<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void testListGamesFail() throws DataAccessException {
        List<GameData> games = gameDAO.listGames();
        assertEquals(0, games.size());
    }

    @Test
    public void testUpdateGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("updategame");
        GameData game = gameDAO.getGame(gameID);
        GameData updated = new GameData(gameID, "henry", game.blackUsername(), game.gameName(), game.game());
        gameDAO.updateGame(updated);
        GameData result = gameDAO.getGame(gameID);
        assertEquals("henry", result.whiteUsername());
    }

    @Test
    public void testUpdateGameFail() throws DataAccessException {
        int gameID = gameDAO.createGame("testgame");
        GameData game = gameDAO.getGame(gameID);
        GameData updated = new GameData(99999, "henry", game.blackUsername(), game.gameName(), game.game());
        gameDAO.updateGame(updated);
        GameData result = gameDAO.getGame(99999);
        assertNull(result);
    }

    @Test
    public void testClearGamesSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("game1");
        gameDAO.clear();
        GameData result = gameDAO.getGame(gameID);
        assertNull(result);
    }

    @Test
    public void testClearGamesAllowsReinsert() throws DataAccessException {
        gameDAO.createGame("game1");
        gameDAO.clear();
        int gameID = gameDAO.createGame("game1");
        GameData result = gameDAO.getGame(gameID);
        assertNotNull(result);
    }
}
