package dataaccess;

import records.GameData;

import java.util.Collection;
import java.util.Map;

public interface GameDAO {

    Collection<Map<String, Object>> getGames();

    void createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void clear();
}