package server;

public class ServerMain {
    public static void main(String[] args) {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        Server server = new Server();
        int actualPort = server.run(port);
        System.out.println("Server started on port " + actualPort);
    }
}
