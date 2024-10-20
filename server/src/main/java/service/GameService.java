//package service;
//
//import dataaccess.AuthDAO;
//import dataaccess.DataAccessException;
//import dataaccess.GameDAO;
//import dataaccess.UserDAO;
//import records.AuthData;
//import records.GameData;
//import requests.CreateGameRequest;
//import requests.JoinGameRequest;
//import requests.ListGamesRequest;
//import results.CreateGameResult;
//import results.JoinGameResult;
//import results.ListGamesResult;
//
//import java.util.Objects;
//
//public class GameService {
//
//    UserDAO userDAO;
//    GameDAO gameDAO;
//    AuthDAO authDAO;
//
//    private int newGameID = 1;
//
//    public GameService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
//        this.userDAO=userDAO;
//        this.gameDAO=gameDAO;
//        this.authDAO=authDAO;
//    }
//
//    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
//        if (!Objects.equals(createGameRequest.getGameName(), null) && !Objects.equals(createGameRequest.getAuthorization(), null)) {
//            if (authDAO.inAuths(createGameRequest.getAuthorization())) {
//                GameData newGame = gameDAO.addGame(createGameRequest);
//                return new CreateGameResult(newGame.getGameID());
//            }
//            throw new DataAccessException("Error: unauthorized");
//        }
//        throw new DataAccessException("Error: bad request");
//    }
//
//    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
//        if (!Objects.equals(listGamesRequest.getAuthorization(), "")) {
//            if (authDAO.inAuths(listGamesRequest.getAuthorization())) {
//                return new ListGamesResult(gameDAO.getGames());
//            }
//            throw new DataAccessException("Error: unauthorized");
//        }
//        throw new DataAccessException("Error: bad request");
//    }
//
//    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
//        if (authDAO.inAuths(joinGameRequest.getAuthorization())) {
//            AuthData authToken = authDAO.getAuth(joinGameRequest.getAuthorization());
//            GameData game = gameDAO.getGame(joinGameRequest.getGameID());
//            if (joinGameRequest.getGameID() != null && game != null) {
//                if (gameDAO.setPlayer(authToken.getUsername(), joinGameRequest.getPlayerColor(), game)) {
//                    return new JoinGameResult();
//                }
//                throw new DataAccessException("Error: already taken");
//            }
//            throw new DataAccessException("Error: bad request");
//        }
//        throw new DataAccessException("Error: unauthorized");
//    }
//}

package service;

import chess.ChessGame;

import records.AuthData;
import records.GameData;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import dataaccess.AlreadyTakenException;

import java.util.Map;
import java.util.Collection;
import java.util.UUID;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    private int getGameID() {
        int gameID;
        do {
            gameID = Math.abs(UUID.randomUUID().hashCode());
            try {
                gameDAO.getGame(gameID);
            } catch (DataAccessException e) {
                break;
            }
        } while (true);
        return gameID;
    }

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<Map<String, Object>> listGames(String authToken) throws UnauthorizedException {
        // Verify the authToken, throw UnauthorizedException if invalid
        try {
            authDAO.getAuth(authToken);
            return gameDAO.getGames();

        } catch (DataAccessException e) {
            throw new UnauthorizedException("Invalid authToken");
        }
    };

    public int createGame(String authToken, String gameName) throws BadRequestException, UnauthorizedException {
        // Verify the authToken, throw UnauthorizedException if invalid
        try {
            authDAO.getAuth(authToken);

            // Check for valid gameName, throw BadRequestException if invalid
            if (gameName == null) {
                throw new BadRequestException("Invalid game name");
            }

            // Create a unique gameID
            int gameID = getGameID();

            // Create a new game, throw BadRequestException if failed
            try {
                GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
                gameDAO.createGame(gameData);

            } catch (DataAccessException e) {
                throw new BadRequestException("Game already exists");
            }

            return gameID;

        } catch (DataAccessException e) {
            throw new UnauthorizedException("Invalid authToken");
        }
    }

    public void joinGame(String authToken, String color, int gameID) throws BadRequestException, UnauthorizedException, AlreadyTakenException {
        // Verify the authToken, throw UnauthorizedException if invalid
        try {
            AuthData authData = authDAO.getAuth(authToken);

            String username = authData.username();

            // Check for valid color, throw BadRequestException if invalid
            if (color == null || (!color.equals("WHITE") && !color.equals("BLACK"))) {
                throw new BadRequestException("Invalid color");
            }

            // Get the game, throw BadRequestException if failed
            try {
                GameData game = gameDAO.getGame(gameID);

                if (color.equals("WHITE")) {
                    // Check if the requested color is already taken, throw AlreadyTakenException if taken
                    if (game.whiteUsername() != null) {
                        throw new AlreadyTakenException("White player already joined");
                    }
                    gameDAO.updateGame(new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game()));

                } else {
                    // Check if the requested color is already taken, throw AlreadyTakenException if taken
                    if (game.blackUsername() != null) {
                        throw new AlreadyTakenException("Black player already joined");
                    }
                    gameDAO.updateGame(new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game()));
                }

            } catch (DataAccessException e) {
                throw new BadRequestException("Game not found");
            }

        } catch (DataAccessException e) {
            throw new UnauthorizedException("Invalid authToken");
        }

    }
}