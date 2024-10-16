import chess.*;
import dataaccess.DatabaseInitializer;
import dataaccess.DataAccessException;
import server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseInitializer.initializeDatabase();

            var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            System.out.println("♕ 240 Chess Server: " + piece);
        } catch (DataAccessException e) {
            System.err.println("Error initializing the database: " + e.getMessage());
            e.printStackTrace();
        }

//        new Server().run(8080);
    }
}



