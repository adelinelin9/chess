package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.GameData;
import model.UserData;
import model.AuthData;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
    private GameService gameService;
    private GameDAOMySQL gameDAO;
    private AuthDAO authDAO;
    private UserDAOMySQL userDAO;

    @BeforeEach
    public void setUp() {
        gameDAO = new GameDAOMySQL();
        authDAO = new AuthDAO();
        userDAO = new UserDAOMySQL();
        gameService = new GameService(gameDAO, authDAO, userDAO);
    }

    // list games success
    @Test
    public void testListGamesSuccess() throws Exception {
        AuthData auth = new AuthData("authToken", "testUser");
        authDAO.createAuth(auth);

        gameService.createGame(auth.getAuthToken(), "ChessGame 1");
        List<GameData> games = gameService.listGames(auth.getAuthToken());

        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    // list games failure (unauthorized)
    @Test
    public void testListGamesFailure() throws Exception {
        AuthData auth = new AuthData("authToken", "testUser");
        authDAO.createAuth(auth);

        gameService.createGame(auth.getAuthToken(), "ChessGame 1");
        assertThrows(DataAccessException.class, () -> gameService.listGames("invalid token"));
    }

    // create game success
    @Test
    public void testCreateGameSuccess() throws Exception {
        AuthData auth = new AuthData("authToken", "testUser");
        authDAO.createAuth(auth);

        GameData game = gameService.createGame(auth.getAuthToken(), "ChessGame 1");
        assertEquals("ChessGame 1", game.getGameName());
    }

    // create game failure (unauthorized)
    @Test
    public void testCreateGameFailure() throws Exception {
        AuthData auth = new AuthData("authToken", "testUser");
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () -> gameService.createGame("invalid token", "ChessGame 1"));
    }

    // join game success
    @Test
    public void testJoinGameSuccess() throws Exception {
        UserData user1 = new UserData("testUser", "password", "test@example.com");
        userDAO.createUser(user1);

        AuthData auth = new AuthData("authToken", "testUser");
        authDAO.createAuth(auth);

        GameData game = gameService.createGame(auth.getAuthToken(), "ChessGame 1");
        gameService.joinGame(auth.getAuthToken(), game.getGameID(), "WHITE");

        GameData updatedGame = gameDAO.getGame(game.getGameID());
        assertEquals("testUser", updatedGame.getWhiteUsername());
    }

    // join game failure (game not found)
    @Test
    public void testJoinGameFailure() throws Exception {
        AuthData auth = new AuthData("authToken", "testUser");
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () -> gameService.joinGame(auth.getAuthToken(), 7, "WHITE"));
    }

    // clear application
    @Test
    public void testClearApplication() throws Exception {
        AuthData auth = new AuthData("authToken","testUser");
        //userDAO.createUser(user);
        authDAO.createAuth(auth);
        gameService.createGame(auth.getAuthToken(), "ChessGame 1");

        gameService.clear();

        //assertTrue(userDAO.getUser(user.getUsername()) == null);
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth.getAuthToken()));
        assertTrue(gameDAO.listGames().isEmpty());
    }

}