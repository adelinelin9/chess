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

   