package client;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        System.out.println("♕ 240 Chess Client");
        new ChessClient(port).run();
    }
}
