package client;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.List;
import java.util.Scanner;

public class ChessClient {

    private ServerFacade server;
    private String authToken = null;
    private String username = null;
    private Scanner scanner = new Scanner(System.in);

    public ChessClient(int port) {
        server = new ServerFacade(port);
    }

    public void run() {
        System.out.println("Welcome to 240 Chess!");
        System.out.println("Type 'help' to see available commands.");

        while (true) {
            if (authToken == null) {
                System.out.print("[LOGGED_OUT] >>> ");
            } else {
                System.out.print("[" + username + "] >>> ");
            }

            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");
            if (parts.length == 0 || parts[0].isEmpty()) {
                continue;
            }

            String command = parts[0].toLowerCase();

            if (authToken == null) {
                handlePreLogin(command, parts);
            } else {
                handlePostLogin(command, parts);
            }
        }
    }

    private void handlePreLogin(String command, String[] parts) {
        if (command.equals("help")) {
            System.out.println("  register <username> <password> <email> - create an account");
            System.out.println("  login <username> <password> - log in");
            System.out.println("  quit - exit the program");
            System.out.println("  help - show this help message");
        } else if (command.equals("register")) {
            if (parts.length < 4) {
                System.out.println("Usage: register <username> <password> <email>");
                return;
            }
            try {
                ServerFacade.AuthResult result = server.register(parts[1], parts[2], parts[3]);
                authToken = result.authToken;
                username = result.username;
                System.out.println("Registered and logged in as " + username);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (command.equals("login")) {
            if (parts.length < 3) {
                System.out.println("Usage: login <username> <password>");
                return;
            }
            try {
                ServerFacade.AuthResult result = server.login(parts[1], parts[2]);
                authToken = result.authToken;
                username = result.username;
                System.out.println("Logged in as " + username);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (command.equals("quit")) {
            System.out.println("Goodbye!");
            System.exit(0);
        } else {
            System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }

    private void handlePostLogin(String command, String[] parts) {
        if (command.equals("help")) {
            System.out.println("  list - list all games");
            System.out.println("  create <game name> - create a new game");
            System.out.println("  join <game number> <WHITE|BLACK> - join a game");
            System.out.println("  observe <game number> - observe a game");
            System.out.println("  logout - log out");
            System.out.println("  quit - exit the program");
            System.out.println("  help - show this help message");
        } else if (command.equals("list")) {
            try {
                List<ServerFacade.GameEntry> games = server.listGames(authToken);
                if (games.isEmpty()) {
                    System.out.println("No games available.");
                } else {
                    for (int i = 0; i < games.size(); i++) {
                        ServerFacade.GameEntry game = games.get(i);
                        String white = game.whiteUsername != null ? game.whiteUsername : "open";
                        String black = game.blackUsername != null ? game.blackUsername : "open";
                        System.out.println((i + 1) + ". " + game.gameName + " | White: " + white + " | Black: " + black);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (command.equals("create")) {
            if (parts.length < 2) {
                System.out.println("Usage: create <game name>");
                return;
            }
            String gameName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
            try {
                int gameID = server.createGame(authToken, gameName);
                System.out.println("Created game '" + gameName + "'");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (command.equals("join")) {
            if (parts.length < 3) {
                System.out.println("Usage: join <game number> <WHITE|BLACK>");
                return;
            }
            try {
                List<ServerFacade.GameEntry> games = server.listGames(authToken);
                int gameNumber = Integer.parseInt(parts[1]) - 1;
                if (gameNumber < 0 || gameNumber >= games.size()) {
                    System.out.println("Invalid game number.");
                    return;
                }
                String color = parts[2].toUpperCase();
                if (!color.equals("WHITE") && !color.equals("BLACK")) {
                    System.out.println("Color must be WHITE or BLACK.");
                    return;
                }
                ServerFacade.GameEntry game = games.get(gameNumber);
                server.joinGame(authToken, color, game.gameID);
                System.out.println("Joined game '" + game.gameName + "' as " + color);
                boolean whiteBottom = color.equals("WHITE");
                ChessBoard board = new ChessGame().getBoard();
                System.out.print(BoardRenderer.render(board, whiteBottom));
            } catch (NumberFormatException e) {
                System.out.println("Game number must be a number.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (command.equals("observe")) {
            if (parts.length < 2) {
                System.out.println("Usage: observe <game number>");
                return;
            }
            try {
                List<ServerFacade.GameEntry> games = server.listGames(authToken);
                int gameNumber = Integer.parseInt(parts[1]) - 1;
                if (gameNumber < 0 || gameNumber >= games.size()) {
                    System.out.println("Invalid game number.");
                    return;
                }
                ServerFacade.GameEntry game = games.get(gameNumber);
                System.out.println("Observing game '" + game.gameName + "'");
                ChessBoard board = new ChessGame().getBoard();
                System.out.print(BoardRenderer.render(board, true));
            } catch (NumberFormatException e) {
                System.out.println("Game number must be a number.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (command.equals("logout")) {
            try {
                server.logout(authToken);
                System.out.println("Logged out.");
                authToken = null;
                username = null;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (command.equals("quit")) {
            System.out.println("Goodbye!");
            System.exit(0);
        } else {
            System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }
}
