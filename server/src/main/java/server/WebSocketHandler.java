package server;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;

import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@WebSocket
public class WebSocketHandler {
    private static final Map<Integer, Set<Session>> GAME_SESSIONS = new ConcurrentHashMap<>();
    private static final Gson GSON = new Gson();
    private final GameService gameService;
    private static final Set<Integer> FINISHED_GAMES = ConcurrentHashMap.newKeySet();

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connected: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Closed: " + session.getRemoteAddress().getAddress() + " with statusCode: " + statusCode + " reason: " + reason);
        GAME_SESSIONS.values().forEach(sessions -> sessions.remove(session));
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = GSON.fromJson(message, UserGameCommand.class);
        String authToken = command.getAuthString();

        switch (command.getCommandType()) {
            case CONNECT:
                ConnectCommand connectCommand = GSON.fromJson(message, ConnectCommand.class);
                handleConnect(session, authToken, connectCommand.getGameID());
                break;
            case LEAVE:
                LeaveCommand leaveCommand = GSON.fromJson(message, LeaveCommand.class);
                handleLeave(session, authToken, leaveCommand.getGameID());
                break;
            case MAKE_MOVE:
                MakeMoveCommand makeMoveCommand = GSON.fromJson(message, MakeMoveCommand.class);
                handleMakeMove(session, authToken, makeMoveCommand.getGameID(), makeMoveCommand.getMove());
                break;
            case RESIGN:
                ResignCommand resignCommand = GSON.fromJson(message, ResignCommand.class);
                handleResign(session, authToken, resignCommand.getGameID());
                break;
            default:
                sendError(session, "Unknown command type ");
        }
    }

    private void handleConnect(Session session, String authToken, int gameID) throws IOException {
        try {
            String username = gameService.getUsername(authToken);
            GameData gameData = gameService.getGameData(gameID);
            GAME_SESSIONS.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
            sendLoadGameMessage(gameID, session);
            sendNotificationToOthers(gameID, session, username + " connected to game " + gameID);
        } catch (Exception e) {
            System.out.println("handleconnect");
            sendError(session, "Failed to find username: " + e.getMessage());}

    }

    private void handleMakeMove(Session session, String authToken, int gameID, ChessMove move) throws IOException {
        try {
            if (FINISHED_GAMES.contains(gameID)) {
                sendError(session, "Game has already finished. ");
                return;
            }
            gameService.makeMove(gameID, authToken, move);
            sendLoadGameMessageToAll(gameID, session);
            String username = gameService.getUsername(authToken);
            sendNotificationToOthers(gameID, session, username + " made a move: " + move); // clean up move
            // implement notif for check or checkmate or stalemate
        } catch (DataAccessException e) {
            System.out.println("handlemove");
            sendError(session, "Failed to make move: " + e.getMessage());
        }
    }

    private void handleLeave(Session session, String authToken, int gameID) throws IOException {
        Set<Session> sessions = GAME_SESSIONS.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            try {
                System.out.println("Removing player with authToken: " + authToken + " from gameID: " + gameID); // Debugging
                gameService.leave(authToken, gameID);
                System.out.println("hi bbg");
                String username = gameService.getUsername(authToken);
                sendNotificationToOthers(gameID, session, username + " left the game");
            } catch (Exception e) {
                System.out.println("handleleave");
                sendError(session, "Failed to find username: " + e.getMessage());
            }

        }
    }

    private void handleResign(Session session, String authToken, int gameID) throws IOException {
        try {
            if (FINISHED_GAMES.contains(gameID)) {
                sendError(session, "Game is already over ");
                return;
            }
            String winner = gameService.resign(authToken, gameID);
            FINISHED_GAMES.add(gameID); // mark game over
            String username = gameService.getUsername(authToken);
            sendNotificationToAll(gameID, username + " resigned. " + winner + " wins. ");
        } catch (DataAccessException e) {
            sendError(session, "Failed to resign: " + e.getMessage());
        }
    }

    private void sendLoadGameMessage(int gameID, Session session) throws IOException {
        try {
            GameData gameData = gameService.getGameData(gameID);
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            String message = GSON.toJson(loadGameMessage);
            System.out.println("Sending LOAD_GAME message to others for gameID: " + gameID);
            session.getRemote().sendString(message);
        } catch (DataAccessException e) {
            sendError(session, "Failed to load game data");
        }
    }

    private void sendLoadGameMessageToAll(int gameID, Session session) throws IOException {
        try {
            GameData gameData = gameService.getGameData(gameID);
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            String message = GSON.toJson(loadGameMessage);
            System.out.println("Sending LOAD_GAME message to all for gameID: " + gameID);
            sendToAll(gameID, message);
        } catch (DataAccessException e) {
            sendError(session, "Failed to load game data");
        }
    }

    private void sendNotificationToOthers(int gameID, Session senderSession, String notification) throws IOException {
        NotificationMessage notificationMessage = new NotificationMessage(notification);
        String message = GSON.toJson(notificationMessage);
        sendToOthers(gameID, senderSession, message);
    }

    private void sendNotificationToAll(int gameID, String notification) throws IOException {
        NotificationMessage notificationMessage = new NotificationMessage(notification);
        String message = GSON.toJson(notificationMessage);
        System.out.println("Sending NOTIFICATION to others for gameID: " + gameID + " with message: " + notification);
        sendToAll(gameID, message);
    }

    private void sendToOthers(int gameID, Session senderSession, String message) throws IOException {
        Set<Session> sessions = GAME_SESSIONS.get(gameID);
        if (sessions != null) {
            for (Session session : sessions) {
                if (!session.equals(senderSession)) {
                    session.getRemote().sendString(message);
                }
            }
        }
    }

    private void sendToAll(int gameID, String message) throws IOException {
        Set<Session> sessions = GAME_SESSIONS.get(gameID);
        if (sessions != null) {
            for (Session session : sessions) {
                session.getRemote().sendString(message);
            }
        }
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        ErrorMessage errorMessageObj = new ErrorMessage(errorMessage);
        String message = GSON.toJson(errorMessageObj);
        System.out.println("Sending error to root client: " + session.getRemoteAddress().getAddress());
        session.getRemote().sendString(message);
    }
}
