package chess;
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

        switch (type) {
            case KING -> getKing(board, myPosition, validMoves);
            case QUEEN -> getQueen(board, myPosition, validMoves);
            case BISHOP -> getBishop(board, myPosition, validMoves);
            case KNIGHT -> getKnight(board, myPosition, validMoves);
//            case ROOK -> getRook(board, myPosition, validMoves);
//            case PAWN -> getPawn(board, myPosition, validMoves);
        };
        return validMoves;
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

                if (onBoard(newRow, newCol) && (board.getPiece(newPosition) == null || board.getPiece(newPosition).pieceColor != this.pieceColor)) {
                    validMoves.add(new ChessMove(position, newPosition));
                }
            }
        }
    }

    private void getQueen(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves) {
        int currentRow0 = position.getRow();
        int currentCol0 = position.getColumn();

        int[] directions0 = {-1, 0, 1};

        for (int rowMove : directions0) {
            for (int colMove : directions0) {
                int newRow = currentRow0 + rowMove;
                int newCol = currentCol0 + colMove;
                movePiece(rowMove, colMove, newRow, newCol, board, position, validMoves);
            }
        }
    }

    private void getBishop(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves) {
        int currentRow1 = position.getRow();
        int currentCol1 = position.getColumn();
        int[] directions1 = {-1, 1};
        for (int rowMove : directions1) {
            for (int colMove : directions1) {
                int newRow = currentRow1 + rowMove;
                int newCol = currentCol1 + colMove;
                movePiece(rowMove, colMove, newRow, newCol, board, position, validMoves);
            }
        }
    }

    private void getKnight(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves) {
        int currentRow2 = position.getRow();
        int currentCol2 = position.getColumn();
        int[] offsets = {-1, 1, -2, 2};
        for (int rowMove : offsets) {
            for (int colMove : offsets) {
                if (Math.abs(rowMove) + Math.abs(colMove) != 3) {
                    continue;
                }
                int newRow = currentRow2 + rowMove;
                int newCol = currentCol2 + colMove;
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if (onBoard(newRow, newCol) && (board.getPiece(newPosition) == null || this.pieceColor != board.getPiece(newPosition).pieceColor)){
                    validMoves.add(new ChessMove(newPosition, newPosition));
                }
            }
        }
    }

    private boolean onBoard(int row, int col) {
        return row >= 1 && row < 8 && col >= 1 && col < 8;
    }

    public void movePiece(int rowMove, int colMove, int newRow, int newCol, ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves) {
        while (onBoard(newRow, newCol)) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece spot = board.getPiece(newPosition);
            if (spot == null) {
                validMoves.add(new ChessMove(position, newPosition));
            } else if (spot.pieceColor != this.pieceColor) {
                validMoves.add(new ChessMove(position, newPosition));
                break;
            } else{
                break;
            }
            newRow += rowMove;
            newCol += colMove;
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
