package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        try {
            DatabaseInitilizer.initializeDatabase();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserDAO userDAO = new UserDAOMySQL();
        GameDAO gameDAO = new GameDAOMySQL();
        AuthDAO authDAO = new AuthDAOMySQL();

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO, userDAO);

        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        WebSocketHandler webSocketHandler = new WebSocketHandler(gameService);

        Spark.webSocket("/ws", webSocketHandler);

        Spark.post("/user", userHandler.register);
        Spark.post("/session", userHandler.login);
        Spark.delete("/session", userHandler.logout);
        Spark.get("/game", gameHandler.listGames);
        Spark.post("/game", gameHandler.createGame);
        Spark.put("/game", gameHandler.joinGame);

        Spark.delete("/db", (req, res) -> {
            try {
                userDAO.clear();
                gameDAO.clear();
                authDAO.clear();
                res.status(200);
                return "{}";
            } catch (Exception e) {
                res.status(400);
                return "{\"message\" : \"Error: " + e.getMessage()+ "\"}";
            }
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static void main(String[] args) {
        new Server().run(8080);
    }
}
