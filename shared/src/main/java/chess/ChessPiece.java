package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

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
        ArrayList<ChessMove> moves = new ArrayList<>();

        switch (type) {
            case KING -> addKingMoves(board, myPosition, moves);
            case QUEEN -> addQueenMoves(board, myPosition, moves);
            case BISHOP -> addBishopMoves(board, myPosition, moves);
            case KNIGHT -> addKnightMoves(board, myPosition, moves);
            case ROOK -> addRookMoves(board, myPosition, moves);
            case PAWN -> addPawnMoves(board, myPosition, moves);
        }

        return moves;
    }
    private void addKingMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
        for (int[] dir : directions) {
            addMoveIfValid(board, myPosition, dir[0], dir[1], moves);
        }
    }

    private void addQueenMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        addBishopMoves(board, myPosition, moves);
        addRookMoves(board, myPosition, moves);
    }

    private void addBishopMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int[][] directions = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
        for (int[] dir : directions) {
            addSlidingMoves(board, myPosition, dir[0], dir[1], moves);
        }
    }

    private void addRookMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        for (int[] dir : directions) {
            addSlidingMoves(board, myPosition, dir[0], dir[1], moves);
        }
    }

    private void addKnightMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int[][] jumps = {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,2}, {1,-2}, {-1,2}, {-1,-2}};
        for (int[] jump : jumps) {
            addMoveIfValid(board, myPosition, jump[0], jump[1], moves);
        }
    }

    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int newRow = myPosition.getRow() + direction;
        int col = myPosition.getColumn();

        if (isInBounds(newRow, col)) {
            ChessPosition forward = new ChessPosition(newRow, col);
            if (board.getPiece(forward) == null) {
                if (newRow == promotionRow) {
                    addPromotionMoves(myPosition, forward, moves);
                } else {
                    moves.add(new ChessMove(myPosition, forward, null));
                }

                if (myPosition.getRow() == startRow) {
                    ChessPosition doubleForward = new ChessPosition(newRow + direction, col);
                    if (board.getPiece(doubleForward) == null) {
                        moves.add(new ChessMove(myPosition, doubleForward, null));
                    }
                }
            }
        }

        for (int colOffset : new int[]{-1, 1}) {
            int newCol = col + colOffset;
            if (isInBounds(newRow, newCol)) {
                ChessPosition capturePos = new ChessPosition(newRow, newCol);
                ChessPiece target = board.getPiece(capturePos);
                if (target != null && target.getTeamColor() != pieceColor) {
                    if (newRow == promotionRow) {
                        addPromotionMoves(myPosition, capturePos, moves);
                    } else {
                        moves.add(new ChessMove(myPosition, capturePos, null));
                    }
                }
            }
        }
    }

    private void addPromotionMoves(ChessPosition start, ChessPosition end, ArrayList<ChessMove> moves) {
        moves.add(new ChessMove(start, end, PieceType.QUEEN));
        moves.add(new ChessMove(start, end, PieceType.ROOK));
        moves.add(new ChessMove(start, end, PieceType.BISHOP));
        moves.add(new ChessMove(start, end, PieceType.KNIGHT));
    }

    private void addSlidingMoves(ChessBoard board, ChessPosition myPosition, int rowDir, int colDir, ArrayList<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        while (true) {
            row += rowDir;
            col += colDir;

            if (!isInBounds(row, col)) break;

            ChessPosition newPos = new ChessPosition(row, col);
            ChessPiece target = board.getPiece(newPos);

            if (target == null) {
                moves.add(new ChessMove(myPosition, newPos, null));
            } else {
                if (target.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
                break;
            }
        }
    }

    private void addMoveIfValid(ChessBoard board, ChessPosition myPosition, int rowOffset, int colOffset, ArrayList<ChessMove> moves) {
        int newRow = myPosition.getRow() + rowOffset;
        int newCol = myPosition.getColumn() + colOffset;

        if (isInBounds(newRow, newCol)) {
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessPiece target = board.getPiece(newPos);

            if (target == null || target.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, newPos, null));
            }
        }
    }

    private boolean isInBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
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
}