package service;

import chess.ChessMove;
import dataaccess.*;
import model.GameData;
import java.util.List;
import java.util.Random;
import chess.ChessGame;

public class GameService {

    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO; //GameDAOMySQL();
        this.authDAO = authDAO;
        this.userDAO = userDAO; //UserDAOMySQL();
    }

    public void clear() throws DataAccessException {
        authDAO.clear();
        userDAO.clear();
        gameDAO.clear();
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        verifyAuth(authToken);
        return gameDAO.listGames();
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        verifyAuth(authToken);
        int gameID = generateGameID();
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDAO.createGame(game);
        return game;
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws  DataAccessException {
        verifyAuth(authToken);
        GameData game = gameDAO.getGame(gameID);
        if(gameDAO.getGame(gameID) == null) {
            throw new DataAccessException("Bad request");
        }
        if(playerColor == null) {
            throw new DataAccessException("No color listed");
        }
        String username = getUsername(authToken);
        if(username == null || userDAO.getUser(username) == null) {
            throw new DataAccessException("User not found");
        }
        if (playerColor.equalsIgnoreCase("WHITE") && game.getWhiteUsername() == null) {
            game.setWhiteUsername(getUsername(authToken));
        } else if (playerColor.equalsIgnoreCase("BLACK") && game.getBlackUsername() == null) {
            game.setBlackUsername(getUsername(authToken));
        } else if (game.getWhiteUsername() != null && game.getBlackUsername() != null) {
            throw new DataAccessException("Already taken");
        } else {
            throw new DataAccessException("Bad request");
        }
        gameDAO.updateGame(game);
    }

    public void verifyAuth(String authToken) throws DataAccessException {
        if(authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }
    }

    public String getUsername(String authToken) throws DataAccessException{
        return authDAO.getAuth(authToken).getUsername();
    }

    private int generateGameID() {
        return new Random().nextInt(10000);
    }

    public GameData getGameData(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public void makeMove(int gameID, String authToken, ChessMove move) throws DataAccessException {
        verifyAuth(authToken);
        GameData gameData = getGameData(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }

        String username = getUsername(authToken);
        ChessGame.TeamColor currentTurn = gameData.getGame().getTeamTurn();
        ChessGame.TeamColor playerColor = null;

        if (username.equals(gameData.getWhiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.getBlackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            throw new DataAccessException("You are not a player in this game. ");
        }

        if (currentTurn != playerColor) {
            throw new DataAccessException("It is not your turn. ");
        }

        try {
            gameData.getGame().makeMove(move);
        } catch (Exception e) {
            throw new DataAccessException("Invalid move: " + e.getMessage());
        }

        gameDAO.updateGame(gameData);
    }

    public String resign(String authToken, int gameID) throws DataAccessException {
        verifyAuth(authToken);
        GameData gameData = getGameData(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }

        String username = getUsername(authToken);
        String winner;

        if (username.equals(gameData.getWhiteUsername())) {
            winner = gameData.getBlackUsername();
        } else if (username.equals(gameData.getBlackUsername())) {
            winner = gameData.getWhiteUsername();
        } else {
            throw new DataAccessException("You are not a player in this game. ");
        }

        return winner;
    }

    public void leave(String authToken, int gameID) throws DataAccessException {
        verifyAuth(authToken);
        GameData gameData = getGameData(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }
        gameDAO.removePlayer(authToken, gameID);
    }
}