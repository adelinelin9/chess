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

    
}

