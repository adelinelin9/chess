package server.handler;

import com.google.gson.Gson;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;

import request.game.CreateGameRequest;
import request.game.JoinGameRequest;
import request.game.ListGameRequest;
import server.response.exception.AlreadyTaken;
import server.response.exception.BadRequest;
import server.response.exception.Unauthorized;
import server.response.game.CreateGameResult;
import server.response.game.JoinGameResult;
import server.response.game.ListGamesResult;
import service.GameService;

import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.Collection;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGames(Request req, Response resp) {
        ListGameRequest listGameRequest = new ListGameRequest(req.headers("Authorization"));

        try{
            Collection<Map<String, Object>> games = gameService.listGames(listGameRequest.authToken());
            return ListGamesResult.response(resp, games);

        } catch (UnauthorizedException e) {
            return Unauthorized.response(resp);
        }
    }

    public Object createGame(Request req, Response resp) {
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        createGameRequest = new CreateGameRequest(req.headers("Authorization"), createGameRequest.gameName());

        try {
            int gameID = gameService.createGame(createGameRequest.authToken(), createGameRequest.gameName());
            return CreateGameResult.response(resp, gameID);

        } catch (BadRequestException e) {
            return BadRequest.response(resp);

        } catch (UnauthorizedException e) {
            return Unauthorized.response(resp);
        }

    }

    public Object joinGame(Request req, Response resp) {
        JoinGameRequest joinGameData = new Gson().fromJson(req.body(), JoinGameRequest.class);
        joinGameData = new JoinGameRequest(req.headers("Authorization"), joinGameData.playerColor(), joinGameData.gameID());

        try {
            gameService.joinGame(joinGameData.authToken(), joinGameData.playerColor(), joinGameData.gameID());
            return JoinGameResult.response(resp);

        } catch (BadRequestException e) {
            return BadRequest.response(resp);

        } catch (UnauthorizedException e) {
            return Unauthorized.response(resp);

        } catch (AlreadyTakenException e) {
            return AlreadyTaken.response(resp);
        }
    }
}