package server;

import io.javalin.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import service.ClearService;
import service.GameService;
import service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    private final Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clear(Context ctx) {
        clearService.clear();
        ctx.status(200).result("{}").contentType("application/json");
    }

    private void register(Context ctx) {
        try {
            var req = gson.fromJson(ctx.body(), RegisterReq.class);
            if (req == null || req.username() == null || req.password() == null || req.email() == null) {
                sendError(ctx, 400, "bad request");
                return;
            }
            AuthData auth = userService.register(req.username(), req.password(), req.email());
            ctx.status(200).result(gson.toJson(new AuthResp(auth.username(), auth.authToken()))).contentType("application/json");
        } catch (DataAccessException e) {
            if (e.getMessage().contains("already taken")) {
                sendError(ctx, 403, "already taken");
            } else {
                sendError(ctx, 400, "bad request");
            }
        }
    }

    private void login(Context ctx) {
        try {
            var req = gson.fromJson(ctx.body(), LoginReq.class);
            if (req == null || req.username() == null || req.password() == null) {
                sendError(ctx, 400, "bad request");
                return;
            }
            AuthData auth = userService.login(req.username(), req.password());
            ctx.status(200).result(gson.toJson(new AuthResp(auth.username(), auth.authToken()))).contentType("application/json");
        } catch (DataAccessException e) {
            sendError(ctx, 401, "unauthorized");
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            userService.logout(authToken);
            ctx.status(200).result("{}").contentType("application/json");
        } catch (DataAccessException e) {
            sendError(ctx, 401, "unauthorized");
        }
    }
    private void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            List<GameData> games = gameService.listGames(authToken);

            var entries = new ArrayList<GameEntry>();
            for (var g : games) {
                entries.add(new GameEntry(g.gameID(), g.gameName(), g.whiteUsername(), g.blackUsername()));
            }

            var response = new HashMap<String, Object>();
            response.put("games", entries);
            ctx.status(200).result(gson.toJson(response)).contentType("application/json");
        } catch (DataAccessException e) {
            sendError(ctx, 401, "unauthorized");
        }
    }

    private void createGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            var req = gson.fromJson(ctx.body(), CreateGameReq.class);
            if (req == null || req.gameName() == null) {
                sendError(ctx, 400, "bad request");
                return;
            }
            int gameID = gameService.createGame(authToken, req.gameName());
            var response = new HashMap<String, Object>();
            response.put("gameID", gameID);
            ctx.status(200).result(gson.toJson(response)).contentType("application/json");
        } catch (DataAccessException e) {
            sendError(ctx, 401, "unauthorized");
        }
    }

    private void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            var req = gson.fromJson(ctx.body(), JoinGameReq.class);

            if (req == null || req.playerColor() == null || req.gameID() == null) {
                sendError(ctx, 400, "bad request");
                return;
            }
            if (!req.playerColor().equals("WHITE") && !req.playerColor().equals("BLACK")) {
                sendError(ctx, 400, "bad request");
                return;
            }

            gameService.joinGame(authToken, req.playerColor(), req.gameID());
            ctx.status(200).result("{}").contentType("application/json");
        } catch (DataAccessException e) {
            if (e.getMessage().contains("already taken")) {
                sendError(ctx, 403, "already taken");
            } else if (e.getMessage().contains("unauthorized")) {
                sendError(ctx, 401, "unauthorized");
            } else {
                sendError(ctx, 400, "bad request");
            }
        }
    }

    private void sendError(Context ctx, int status, String message) {
        var response = new HashMap<String, String>();
        response.put("message", "Error: " + message);
        ctx.status(status).result(gson.toJson(response)).contentType("application/json");
    }

    // request/response helper records
    private record RegisterReq(String username, String password, String email) {}
    private record LoginReq(String username, String password) {}
    private record CreateGameReq(String gameName) {}
    private record JoinGameReq(String playerColor, Integer gameID) {}
    private record AuthResp(String username, String authToken) {}
    private record GameEntry(int gameID, String gameName, String whiteUsername, String blackUsername) {}


}

