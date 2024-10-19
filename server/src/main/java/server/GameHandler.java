//package server;
//
//import dataaccess.DataAccessException;
//import service.GameService;
//import records.GameData;
//import java.util.List;
//import utils.JSONUtil;
//import java.util.Map;
//import java.util.HashMap;
//
//import spark.Route;
//import spark.Request;
//import spark.Response;
//
//
//public class GameHandler {
//
//    private GameService gameService;
//
//    public GameHandler(GameService gameService) {
//        this.gameService = gameService;
//    }
//
//    // list games
//    public Route listGames = (Request req, Response res) -> {
//        String authtoken = req.headers("Authorization").replace("Bearer ", "");
//        try {
//            List<GameData> games = gameService.listGames(authtoken);
//            Map<String, List<GameData>> response = new HashMap<>();
//            response.put("games", games);
//            res.status(200);
//            return JSONUtil.toJson(response);
//        } catch (DataAccessException e) {
//            res.status(401);
//            return "{\"message\" : \"Error: unauthorized\"}";
//        }
//    };
//
//    // create games
//    public Route createGame = (Request req, Response res) -> {
//        String authtoken = req.headers("Authorization").replace("Bearer ", "");
//        CreateGameRequest createRequest = JSONUtil.fromJson(req.body(), CreateGameRequest.class);
//        try {
//            GameData game = gameService.createGame(authtoken, createRequest.gameName);
//            res.status(200);
//            return JSONUtil.toJson(game);
//        } catch (DataAccessException e) {
//            res.status(401);
//            return "{\"message\" : \"Error: bad request\"}";
//        }
//    };
//
//    // join game
//    public Route joinGame = (Request req, Response res) -> {
//        String authtoken = req.headers("Authorization").replace("Bearer ", "");
//        JoinGameRequest joinRequest = JSONUtil.fromJson(req.body(), JoinGameRequest.class);
//        try {
//            gameService.joinGame(authtoken, joinRequest.getGameID(), joinRequest.getPlayerColor());
//            res.status(200);
//            return "{}";
//        } catch (DataAccessException e) {
//            if (e.getMessage().equals("Unauthorized")) {
//                res.status(401);
//                return "{\"message\" : \"Error: unauthorized\"}";
//            } else if (joinRequest.getGameID() == 0 || joinRequest.getPlayerColor() == null) {
//                res.status(400);
//                return "{\"message\" : \"Error: bad request\"}";
//            } else if (e.getMessage().equals("Bad request")) {
//                res.status(403);
//                return "{\"message\" : \"Error: already taken\"}";
//            } else {
//                res.status(401);
//                return "{\"message\" : \"Error: " + e.getMessage() + "\"}";
//            }
//        }
//    };
//
//
//    private static class JoinGameRequest {
//        private String playerColor;
//        private int gameID;
//
//        public String getPlayerColor() {
//            return playerColor;
//        }
//
//        public int getGameID() {
//            return gameID;
//        }
//    }
//
//    private static class CreateGameRequest {
//        private String gameName;
//
//        public String getGameName() {
//            return gameName;
//        }
//    }
//}