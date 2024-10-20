//package dataaccess;
//
//import chess.ChessGame;
//import com.google.gson.Gson;
//import records.GameData;
//import requests.CreateGameRequest;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Objects;
//
//public class MemoryGameDAO implements GameDAO {
//    ArrayList<GameData> games = new ArrayList<>();
//    private int newGameID = 1;
//
//    public void clearGames() {
//        games.clear();
//    }
//
//    public GameData addGame(CreateGameRequest request) {
//        GameData game = new GameData(newGameID++, request.getGameName());
//        games.add(game);
//        return game;
//    }
//
//    @Override
//    public boolean updateGame(int gameID, ChessGame game) {
//        return true;
//    }
//
//    public boolean removePlayer(int gameID, String username) {
//        return false;
//    }
//
//    public GameData getGame(Integer gameID) {
//        for (GameData game : games) {
//            if (game.getGameID() == gameID) {
//                return game;
//            }
//        }
//        return null;
//    }
//
//    public Collection<GameData> getGames() {
//        return games;
//    }
//
//    public boolean setPlayer(String username, String playerColor, GameData game) {
//        if (Objects.equals(playerColor, null)) {
//            return true;
//        } else if (Objects.equals(playerColor, "WHITE") && Objects.equals(game.getWhiteUsername(), null)) {
//            game.setWhiteUser(username);
//            return true;
//        } else if (Objects.equals(playerColor, "BLACK") && Objects.equals(game.getBlackUsername(), null)) {
//            game.setBlackUser(username);
//            return true;
//        }
//        return false;
//    }
//
//}

package dataaccess;

import records.GameData;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryGameDAO implements GameDAO {
    final private ConcurrentHashMap<Integer, GameData> games = new ConcurrentHashMap<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Game already exists");
        }

        games.put(game.gameID(), game);
    }


    @Override
    public Collection<Map<String, Object>> getGames() {
        return games.values().stream().map(game -> {
            Map<String, Object> gameMap = new HashMap<>();
            gameMap.put("gameID", game.gameID());
            gameMap.put("whiteUsername", game.whiteUsername());
            gameMap.put("blackUsername", game.blackUsername());
            gameMap.put("gameName", game.gameName());
            return gameMap;
        }).collect(Collectors.toList());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData gameData = games.get(gameID);

        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }

        return gameData;
    }

    @Override
    public void updateGame(GameData game) {
        games.remove(game.gameID());
        games.put(game.gameID(), game);
    }

    @Override
    public void clear() {
        games.clear();
    }

    public int size() {
        return games.size();
    }
}