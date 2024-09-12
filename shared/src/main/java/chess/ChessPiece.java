package chess;
import chess.moves.*;
import java.util.Collection;
import java.util.Objects;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;

    }


    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {

        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new HashSet<>();

//        switch (type) {
//            case KING -> King.getKing(board, myPosition, this.pieceColor);
//            case QUEEN -> Queen.getQueen(board, myPosition, this.pieceColor);
//            case BISHOP -> Bishop.getBishop(board, myPosition, this.pieceColor);
//            case KNIGHT -> Knight.getKnight(board, myPosition, this.pieceColor);
//            case ROOK -> Rook.getRook(board, myPosition, this.pieceColor);
//            case PAWN -> Pawn.getPawn(board, myPosition, this.pieceColor);
//        };
        return validMoves;
//        throw new RuntimeException("Not implemented");
    }

    private void getKing(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves) {
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        int[] directions = {-1, 0, 1};

        for (int rowMove : directions) {
            for (int colMove : directions) {

                int newRow = currentRow + rowMove;
                int newCol = currentCol + colMove;

                ChessPosition newPosition = new ChessPosition(newRow, newCol);

//                if (onBoard(newRow, newCol) && (board.getPiece(newPosition) == null || board.getPiece(newPosition).pieceColor != this.pieceColor)) {
//                    validMoves.add(new ChessMove(position, newPosition));
//                }
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
