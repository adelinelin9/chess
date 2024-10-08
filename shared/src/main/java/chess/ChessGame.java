package chess;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeam;
    private ChessBoard board;

    public ChessGame() {
        this.currentTeam = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeam = team;
    }

    public void switchTeams() {
        if(currentTeam == TeamColor.WHITE) {
            currentTeam = TeamColor.BLACK;
        } else {
            currentTeam = TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = getBoard().getPiece(startPosition);
        if (piece == null) return Collections.emptyList();

        Collection<ChessMove> moves = piece.pieceMoves(getBoard(), startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();

        for(ChessMove move : moves) {
            try {
                ChessBoard cloneBoard = getBoard().cloneBoard();
                cloneBoard.executeMove(move);
                if(!isInCheckClonedBoard(cloneBoard, piece.getTeamColor())) {
                    legalMoves.add(move);
                }
            } catch (InvalidMoveException e) {

            }
        }
        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = getBoard().getPiece(start);


        if(!validMoves(start).contains(move)) {
            throw new InvalidMoveException("Move isn't allowed");
        }

        if(piece.getTeamColor() != currentTeam) {
            throw new InvalidMoveException("Not your turn");
        }

        if(piece == null) {
            throw new InvalidMoveException("Piece needed to move");
        }

        ChessBoard clonedBoard = this.board.cloneBoard();

        clonedBoard.executeMove(move);
        if (isInCheckClonedBoard(clonedBoard, currentTeam)) {
            throw new InvalidMoveException("This Move will end with your king in check");
        }
        this.board.executeMove(move);
        switchTeams();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessBoard board = getBoard();
        ChessPosition kingPosition = board.findKing(teamColor);
        int numRows = board.getRowCount();
        int numCols = board.getColCount(numRows);

        ChessGame.TeamColor opposingColor = (teamColor == TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        for(int i=0; i < numRows; i++) {
            for (int j=0; j < numCols; j++) {
                ChessPosition piecePosition = new ChessPosition(i+1, j+1);
                ChessPiece piece = board.getPiece(piecePosition);
                if(piece != null && piece.getTeamColor() == opposingColor) {
                    Collection<ChessMove> moves = ChessMovesCalculator.moveCalculator(piece, board, piecePosition);
                    for(ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isInCheckClonedBoard(ChessBoard board, TeamColor teamColor) {
        ChessPosition kingPosition = board.findKing(teamColor);
        TeamColor opposingTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        for (int i=0; i < board.getRowCount(); i++){
            for (int j = 0; j < board.getColCount(board.getRowCount()); j++) {
                ChessPosition piecePosition = new ChessPosition(i+1, j+1);
                ChessPiece piece = board.getPiece(piecePosition);
                if (piece != null && piece.getTeamColor() == opposingTeam) {
                    Collection<ChessMove> pieceMoves = ChessMovesCalculator.moveCalculator(piece, board, piecePosition);
                    for (ChessMove move : pieceMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        ChessBoard board = getBoard();

        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColCount(board.getRowCount()); j++) {
                ChessPosition piecePosition = new ChessPosition(i+1, j+1);
                ChessPiece piece = board.getPiece(piecePosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = ChessMovesCalculator.moveCalculator(piece, board, piecePosition);
                    for (ChessMove move : moves) {
                        ChessBoard clonedBoard = board.cloneBoard();
                        try {
                            clonedBoard.executeMove(move);
                            if (!isInCheckClonedBoard(clonedBoard, teamColor)) {
                                return false;
                            }
                        } catch (InvalidMoveException e) {
                        }
                    }
                }
            }

        } return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            return false;
        }

        ChessBoard board = getBoard();

        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColCount(board.getRowCount()); j++) {
                ChessPosition piecePosition = new ChessPosition(i+1, j+1);
                ChessPiece piece = board.getPiece(piecePosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    for (ChessMove move : validMoves(piecePosition)) {
                        ChessBoard clonedBoard = board.cloneBoard();
                        try {
                            clonedBoard.executeMove(move);
                            if (!isInCheckClonedBoard(clonedBoard, teamColor)) {
                                return false;
                            }
                        } catch (InvalidMoveException ignore) {
                        }
                    }
                }
            }

        } return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        if(board == null) {
            throw new IllegalArgumentException("Board can't be null");
        }
        this.board=board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
