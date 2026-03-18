package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

public class BoardRenderer {

    public static String render(ChessBoard board, boolean whiteBottom) {
        StringBuilder sb = new StringBuilder();
        String[] colLabels = {"a", "b", "c", "d", "e", "f", "g", "h"};

        sb.append("   ");
        if (whiteBottom) {
            for (String col : colLabels) {
                sb.append(" " + col + " ");
            }
        } else {
            for (int i = colLabels.length - 1; i >= 0; i--) {
                sb.append(" " + colLabels[i] + " ");
            }
        }
        sb.append("\n");

        int rowStart;
        int rowEnd;
        int rowStep;
        if (whiteBottom) {
            rowStart = 8;
            rowEnd = 1;
            rowStep = -1;
        } else {
            rowStart = 1;
            rowEnd = 8;
            rowStep = 1;
        }

        for (int row = rowStart; row != rowEnd + rowStep; row += rowStep) {
            sb.append(" " + row + " ");

            int colStart;
            int colEnd;
            int colStep;
            if (whiteBottom) {
                colStart = 1;
                colEnd = 8;
                colStep = 1;
            } else {
                colStart = 8;
                colEnd = 1;
                colStep = -1;
            }

            for (int col = colStart; col != colEnd + colStep; col += colStep) {
                boolean lightSquare = (row + col) % 2 == 0;
                String bg;
                if (lightSquare) {
                    bg = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                } else {
                    bg = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                }
                sb.append(bg);

                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) {
                    sb.append(EscapeSequences.EMPTY);
                } else {
                    sb.append(getPieceString(piece));
                }
                sb.append(EscapeSequences.RESET_TEXT_COLOR);
            }

            sb.append(EscapeSequences.RESET_BG_COLOR + " " + row + "\n");
        }

        sb.append("   ");
        if (whiteBottom) {
            for (String col : colLabels) {
                sb.append(" " + col + " ");
            }
        } else {
            for (int i = colLabels.length - 1; i >= 0; i--) {
                sb.append(" " + colLabels[i] + " ");
            }
        }
        sb.append("\n");

        return sb.toString();
    }

    private static String getPieceString(ChessPiece piece) {
        String color;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            color = EscapeSequences.SET_TEXT_COLOR_WHITE;
        } else {
            color = EscapeSequences.SET_TEXT_COLOR_BLACK;
        }

        String symbol;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                symbol = EscapeSequences.WHITE_KING;
            } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                symbol = EscapeSequences.WHITE_QUEEN;
            } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                symbol = EscapeSequences.WHITE_BISHOP;
            } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                symbol = EscapeSequences.WHITE_KNIGHT;
            } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                symbol = EscapeSequences.WHITE_ROOK;
            } else {
                symbol = EscapeSequences.WHITE_PAWN;
            }
        } else {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                symbol = EscapeSequences.BLACK_KING;
            } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                symbol = EscapeSequences.BLACK_QUEEN;
            } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                symbol = EscapeSequences.BLACK_BISHOP;
            } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                symbol = EscapeSequences.BLACK_KNIGHT;
            } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                symbol = EscapeSequences.BLACK_ROOK;
            } else {
                symbol = EscapeSequences.BLACK_PAWN;
            }
        }

        return color + symbol;
    }
}
