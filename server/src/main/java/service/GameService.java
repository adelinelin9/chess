package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import java.util.List;

public class GameService {
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;

    public GameService(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    private String authenticate(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("unauthorized");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("unauthorized");
        }
        return auth.username();
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        authenticate(authToken);
        return gameDAO.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        authenticate(authToken);
        return gameDAO.createGame(gameName);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException {
        String username = authenticate(authToken);

        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("bad request");
        }

        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("already taken");
            }
            gameDAO.updateGame(new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game()));
        } else {
            if (game.blackUsername() != null) {
                throw new DataAccessException("already taken");
            }
            gameDAO.updateGame(new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game()));
        }
    }
}
