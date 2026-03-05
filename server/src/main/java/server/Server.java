package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {

    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    private final Gson gson = new Gson();
    private final Javalin javalin;

    public Server() {
        try {
            DatabaseManager.createDatabase();
            UserDAO userDAO = new MySqlUserDAO();
            AuthDAO authDAO = new MySqlAuthDAO();
            GameDAO gameDAO = new MySqlGameDAO();
            userService = new UserService(userDAO, authDAO);
            gameService = new GameService(gameDAO, authDAO);
            clearService = new ClearService(userDAO, authDAO, gameDAO);
        } catch (DataAccessException e) {
            throw new RuntimeException("failed to initialize database: " + e.getMessage());
        }

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
        try {
            clearService.clear();
            ctx.status(200).result("{}").contentType("application/json");
        } catch (DataAccessException e) {
            sendError(ctx, 500, e.getMessage());
        }
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
            } else if (e.getMessage().contains("failed")) {
                sendError(ctx, 500, e.getMessage());
            } else {
                sendError(ctx, 400, "bad request");
            }
        } catch (Exception e) {
            sendError(ctx, 500, e.getMessage());
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
            if (e.getMessage().contains("failed")) {
                sendError(ctx, 500, e.getMessage());
            } else {
                sendError(ctx, 401, "unauthorized");
            }
        } catch (Exception e) {
            sendError(ctx, 500, e.getMessage());
        }
    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            userService.logout(authToken);
            ctx.status(200).result("{}").contentType("application/json");
        } catch (DataAccessException e) {
            if (e.getMessage().contains("failed")) {
                sendError(ctx, 500, e.getMessage());
            } else {
                sendError(ctx, 401, "unauthorized");
            }
        } catch (Exception e) {
            sendError(ctx, 500, e.getMessage());
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
            if (e.getMessage().contains("failed")) {
                sendError(ctx, 500, e.getMessage());
            } else {
                sendError(ctx, 401, "unauthorized");
            }
        } catch (Exception e) {
            sendError(ctx, 500, e.getMessage());
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
            if (e.getMessage().contains("failed")) {
                sendError(ctx, 500, e.getMessage());
            } else {
                sendError(ctx, 401, "unauthorized");
            }
        } catch (Exception e) {
            sendError(ctx, 500, e.getMessage());
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
            } else if (e.getMessage().contains("failed")) {
                sendError(ctx, 500, e.getMessage());
            } else {
                sendError(ctx, 400, "bad request");
            }
        } catch (Exception e) {
            sendError(ctx, 500, e.getMessage());
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
