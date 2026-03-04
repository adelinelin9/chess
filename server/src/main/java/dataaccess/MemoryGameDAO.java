package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    public int createGame(String gameName) throws DataAccessException {
        int id = nextId++;
        games.put(id, new GameData(id, null, null, gameName, new ChessGame()));
        return id;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    public void clear() throws DataAccessException {
        games.clear();
        nextId = 1;
    }
}
