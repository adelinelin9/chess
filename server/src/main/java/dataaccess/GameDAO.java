package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    //keep track of games
    private Map<Integer, GameData> games = new HashMap<>();
    private AuthDAO authDAO = new AuthDAO();

    public void clear() throws DataAccessException{
        games.clear();
    }

    public void createGame(GameData game) throws DataAccessException{
        if(games.containsKey(game.getGameID())) {
            throw new DataAccessException("This game already exists.");
        }
        games.put(game.getGameID(), game);
    }

    public GameData getGame(int gameID) throws DataAccessException{
        if(!games.containsKey(gameID)) {
            throw new DataAccessException("Game not found.");
        }
        return games.get(gameID);
    }

    public List<GameData> listGames() throws DataAccessException{
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData game) throws DataAccessException{
        if(!games.containsKey(game.getGameID())) {
            throw new DataAccessException("Game not found.");
        }
        games.put(game.getGameID(), game);
    }

    public void removePlayer(String authToken, int gameID) throws DataAccessException {
        String username = authDAO.getAuth(authToken).getUsername();
        GameData game = getGame(gameID);
        if (game.getWhiteUsername() != null && game.getWhiteUsername().equals(username)) {
            game.setWhiteUsername(null);
        } else if (game.getBlackUsername() != null && game.getBlackUsername().equals(username)) {
            game.setBlackUsername(null);
        } else {
            throw new DataAccessException("Player not found in the game.");
        }
        updateGame(game);
    }
}