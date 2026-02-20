package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO {
    private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    public int createGame(String gameName) {
        int id = nextId++;
        games.put(id, new GameData(id, null, null, gameName, new ChessGame()));
        return id;
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public void clear() {
        games.clear();
        nextId = 1;
    }
}
