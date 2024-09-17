package chess;

import java.util.Collection;
import java.util.ArrayList;


public class ChessMovesCalculator {
    public static Collection<ChessMove> moveCalculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = position.getRow()-1;
        int col = position.getColumn()-1;
        int[][] directions = getDirections(piece);
        int maxSteps = getSteps(piece, position);

        for(int[] direction : directions) {
            for(int step = 1; step <= maxSteps; step++) {
                int nextRow = row + direction[0] * step;
                int nextCol = col + direction[1] * step;

                if (nextRow < 0 || nextRow >= 8 || nextCol < 0 || nextCol >= 8) {
                    break;
                }

                ChessPosition nextPosition = new ChessPosition(nextRow + 1, nextCol + 1);
                ChessPiece pieceAtNextPosition = board.getPiece(nextPosition);

                if (pieceAtNextPosition == null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.PAWN && direction[1] != 0) {
                        continue;
                    }
                } else if (pieceAtNextPosition.getTeamColor() != piece.getTeamColor()) {
                    if (piece.getPieceType() == ChessPiece.PieceType.PAWN && direction[1] == 0) {
                        continue;
                    }
                } else {
                    break;
                }

                if (pawnUp(piece, nextRow)) {
                    validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.ROOK));
                } else {
                    validMoves.add(new ChessMove(position, nextPosition, null));
                    if (pieceAtNextPosition != null && pieceAtNextPosition.getTeamColor() != piece.getTeamColor()) {
                        break;
                    }
                }
            }
        }
        return validMoves;
    }

    private static int[][] getDirections(ChessPiece piece) {
        ChessPiece.PieceType pieceType = piece.getPieceType();
        ChessGame.TeamColor pieceColor = piece.getTeamColor();

        switch (pieceType) {
            case KING:
                return new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            case QUEEN:
                return new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
            case BISHOP:
                return new int[][]{{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
            case KNIGHT:
                return new int[][]{{-1, 2}, {1, 2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
            case ROOK:
                return new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
            case PAWN:
                if(pieceColor == ChessGame.TeamColor.WHITE) {
                    return new int[][]{{1,0}, {1,1}, {1,-1}};
                } else {
                    return new int[][]{{-1,0}, {-1,-1}, {-1,1}};
                }
            default:
                return new int[][]{};
        }
    }

    public static boolean pawnUp(ChessPiece piece, int nextRow) {
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return (piece.getTeamColor() == ChessGame.TeamColor.BLACK && nextRow == 0) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.WHITE && nextRow == 7);
        }
        return false;
    }

    private static int getSteps(ChessPiece piece, ChessPosition position) {
        return switch (piece.getPieceType()) {
            case PAWN -> piece.pawnFirstMove(position) ? 2 : 1;
            case KNIGHT, KING -> 1;
            default -> 7;
        };
    }
}