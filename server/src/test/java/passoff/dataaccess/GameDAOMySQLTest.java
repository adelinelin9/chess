package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOMySQLTest {

    private GameDAOMySQL gameDAO;
    private UserDAOMySQL userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DatabaseInitializer.initializeDatabase();
        gameDAO = new GameDAOMySQL();
        userDAO = new UserDAOMySQL();
        gameDAO.clear();
        userDAO.clear();

        UserData whiteUser = new UserData("whiteUser", "white@example.com", "password");
        UserData blackUser = new UserData("blackUser", "black@example.com", "password");
        userDAO.createUser(whiteUser);
        userDAO.createUser(blackUser);
    }

    // create game success
    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(1, "whiteUser", "blackUser", "testGame", chessGame);
        gameDAO.createGame(game);

        GameData retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals(1, retrievedGame.getGameID());
    }

    // create game failure (gameID already exists)
    @Test
    public void testCreateGameFailure() throws DataAccessException {
        ChessGame newGame = new ChessGame();
        GameData game = new GameData(1, "whiteUser", "blackUser", "testGame", newGame);
        gameDAO.createGame(game);
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(game));
    }

    // get game success
    @Test
    public void testGetGameSuccess() throws DataAccessException {
        ChessGame newChessGame = new ChessGame();
        GameData game = new GameData(1, "whiteUser", "blackUser", "testGame", newChessGame);
        gameDAO.createGame(game);

        GameData retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals("testGame", retrievedGame.getGameName());
    }

    // get game failure (game doesn't exist)
    @Test
    public void testGetGameFailure() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(1));
    }

    // list games success
    @Test
    public void testListGamesSuccess() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        GameData game1 = new GameData(1, "whiteUser", "blackUser", "testGame", chessGame1);
        ChessGame chessGame2 = new ChessGame();
        GameData game2 = new GameData(2, "whiteUser", "blackUser", "testGame", chessGame2);
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);

        List<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    // list games failure (no games to list)
    @Test
    public void testListGamesFailure() throws DataAccessException {
        List<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }

    // update games success
    @Test
    public void testUpdateGameSuccess() throws DataAccessException {
        ChessGame newChessGame = new ChessGame();
        GameData game = new GameData(1, "whiteUser", "blackUser", "testGame", newChessGame);
        gameDAO.createGame(game);

        UserData whiteUser2 = new UserData("whiteUser2", "white@example.com", "password");
        userDAO.createUser(whiteUser2);

        game.setWhiteUsername("whiteUser2");
        gameDAO.updateGame(game);
        GameData updatedGame = gameDAO.getGame(1);

        assertEquals("whiteUser2", updatedGame.getWhiteUsername());
    }

    // update games failure (game not found, never created)
    @Test
    public void testUpdateGameFailure() throws DataAccessException {
        ChessGame newChessGame = new ChessGame();
        GameData game = new GameData(2, "whiteUser1", "blackUser1", "testGame", newChessGame);

        assertTrue(gameDAO.listGames().isEmpty());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(game));
    }

    // clear games
    @Test
    public void testClearGamesSuccess() throws DataAccessException {
        ChessGame newGame1 = new ChessGame();
        ChessGame newGame2 = new ChessGame();
        GameData game1 = new GameData(1, "whiteUser", "blackUser", "testGame1", newGame1);
        GameData game2 = new GameData(2, "whiteUser", "blackUser", "testGame2", newGame2);
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);

        gameDAO.clear();
        assertTrue(gameDAO.listGames().isEmpty());
    }
}