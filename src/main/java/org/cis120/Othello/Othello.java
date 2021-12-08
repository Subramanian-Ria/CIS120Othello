package org.cis120.Othello;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.TreeSet;

// Player color enum
// used for keeping track of the current turn or what pieces are on the board
// public so that it can be referenced from anywhere and the representation of
// the pieces and the
// players is consistent
enum PlayerColor {
    BLACK, WHITE, EMPTY
}

public class Othello {
    // board 2d array
    // x,y format
    private PlayerColor[][] board;

    // stores current turn using player color enum
    private PlayerColor currentTurn;

    // keeps track of points for black and white
    private int blackPoints;
    private int whitePoints;

    // stores valid moves for both players
    private TreeSet<ValidMove> blackValidMoves;
    private TreeSet<ValidMove> whiteValidMoves;

    // Tracks game over state
    private boolean gameOver;

    // true if next turn will be a pass turn and false otherwise
    private boolean pass;

    // output filestring
    private final String fileString = "files/output/output.txt";

    // Othello constructor
    public Othello() {
        reset();
    }

    /**
     * resets the game state to start a new game.
     **/
    public void reset() {
        board = new PlayerColor[8][8];
        currentTurn = PlayerColor.BLACK;
        blackPoints = 2;
        whitePoints = 2;
        blackValidMoves = new TreeSet<>();
        whiteValidMoves = new TreeSet<>();
        gameOver = false;
        pass = false;
        initBoard();
        blackValidMoves = generateValidMoves(PlayerColor.BLACK);
        whiteValidMoves = generateValidMoves(PlayerColor.WHITE);
    }

    // Sets up the starting board state
    private void initBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = PlayerColor.EMPTY;
            }
        }
        board[3][3] = PlayerColor.WHITE;
        board[3][4] = PlayerColor.BLACK;
        board[4][3] = PlayerColor.BLACK;
        board[4][4] = PlayerColor.WHITE;
    }

    // Swaps the current turn variable to change over the turns
    private void currentTurnFlip() {
        if (currentTurn == PlayerColor.BLACK) {
            currentTurn = PlayerColor.WHITE;
        } else {
            currentTurn = PlayerColor.BLACK;
        }
    }

    // generates all valid moves for one player
    // specifically only checks squares that are adjacent to placed disks for
    // greater efficiency
    private TreeSet<ValidMove> generateValidMoves(PlayerColor player) {
        TreeSet<ValidMove> moves = new TreeSet<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != PlayerColor.EMPTY) {
                    for (int k = -1; k < 2; k++) {
                        for (int l = -1; l < 2; l++) {
                            if (k == 0 && l == 0 || i + k > 7 || j + l > 7 || i + k < 0 ||
                                    j + l < 0 || board[i + k][j + l] != PlayerColor.EMPTY) {
                                continue;
                            }
                            LinkedHashSet<int[]> flipped = checkMove(i + k, j + l, player);
                            if (!flipped.isEmpty()) {
                                ValidMove v = new ValidMove(i + k, j + l, flipped);
                                moves.add(v);
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }

    // gets a move from a valid moves treeset based on coordinates given
    private ValidMove getValidMove(TreeSet<ValidMove> moves, int x, int y) {
        for (ValidMove move : moves) {
            if (move.getX() == x && move.getY() == y) {
                return move;
            }
        }
        return null;
    }

    // checks if a move is valid using raycast helper
    // takes a move and casts a ray out in all 8 directions to see if any disks are
    // flipped when that move is played
    private LinkedHashSet<int[]> checkMove(int x, int y, PlayerColor player) {
        LinkedHashSet<int[]> flipped = new LinkedHashSet<>();
        if (board[x][y] == PlayerColor.EMPTY) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (i == 0 && j == 0) {
                        continue;
                    }
                    flipped.addAll(
                            rayCast(
                                    x + i, y + j, new int[] { i, j }, player,
                                    new LinkedHashSet<>()
                            )
                    );
                }
            }
        }
        return flipped;
    }

    // Helper function to cast a ray over the board to figure out if a certain
    // direction is valid
    // i.e. will result in a valid "flanking maneuver"
    // aka will disks be flipped
    private LinkedHashSet<int[]> rayCast(
            int x, int y, int[] ray, PlayerColor player,
            LinkedHashSet<int[]> rayQueue
    ) {
        if (x > 7 || y > 7 || x < 0 || y < 0 || board[x][y] == PlayerColor.EMPTY) {
            return new LinkedHashSet<>();
        } else if (board[x][y] != player) {
            rayQueue.add(new int[] { x, y });
            return rayCast(x + ray[0], y + ray[1], ray, player, rayQueue);
        } else {
            return rayQueue;
        }
    }

    // generates points from scratch
    // used when loading a board
    private void resetPoints() {
        blackPoints = 0;
        whitePoints = 0;
        for (PlayerColor[] spaces : board) {
            for (PlayerColor space : spaces) {
                if (space == PlayerColor.BLACK) {
                    blackPoints++;
                } else if (space == PlayerColor.WHITE) {
                    whitePoints++;
                }
            }
        }
    }

    /**
     * playTurn allows players to play a turn. Returns true if the move is
     * successful and false if a player tries to play in a location that is
     * taken or after the game has ended. If the turn is successful and the game
     * has not ended, the player is changed. If the turn is unsuccessful or the
     * game has ended, the player is not changed.
     *
     * @param x      column to play in
     * @param y      row to play in
     * @param player move color
     **/
    public void playTurn(int x, int y, PlayerColor player) {
        // also mostly for debugging or manual input of moves without using GUI
        if (player != currentTurn) {
            throw new IllegalArgumentException();
        }

        // loads correct valid moves tree
        TreeSet<ValidMove> moves;
        if (player == PlayerColor.BLACK) {
            moves = blackValidMoves;
        } else {
            moves = whiteValidMoves;
        }

        // kills play turn function if the game is over
        if (gameOver || checkWinner() != null) {
            gameOver = true;
            return;
        }

        // gets the valid move x y from the moves set
        ValidMove move = getValidMove(moves, x, y);

        // if the move is valid
        if (move != null) {
            // place the disk
            board[x][y] = player;

            // flip all disks changed by the move
            LinkedHashSet<int[]> flipped = move.getFlippedDisks();
            for (int[] disk : flipped) {
                board[disk[0]][disk[1]] = player;
            }

            // update points
            if (player == PlayerColor.BLACK) {
                blackPoints += flipped.size() + 1;
                whitePoints -= flipped.size();
            } else {
                whitePoints += flipped.size() + 1;
                blackPoints -= flipped.size();
            }

            // update valid moves sets
            blackValidMoves = generateValidMoves(PlayerColor.BLACK);
            whiteValidMoves = generateValidMoves(PlayerColor.WHITE);

            // flips turn
            currentTurnFlip();

            // passing code (operates on next turn)
            if (player == PlayerColor.BLACK && whiteValidMoves.isEmpty()) {
                pass = true;
                currentTurnFlip();
            } else if (player == PlayerColor.WHITE && blackValidMoves.isEmpty()) {
                pass = true;
                currentTurnFlip();
            } else {
                pass = false;
            }
            return;
        }
        // if move is invalid an exception is thrown to be handled in GameBoard.java
        throw new IllegalArgumentException();
    }

    /**
     * checkWinner checks whether the game has reached a win condition.
     * checkWinner only looks for horizontal wins.
     *
     * @return PlayerColor of the winner, empty if the game is a tie, or null if the
     *         game is not over
     **/
    public PlayerColor checkWinner() {
        if (blackValidMoves.isEmpty() && whiteValidMoves.isEmpty()) {
            if (blackPoints > whitePoints) {
                return PlayerColor.BLACK;
            } else if (whitePoints > blackPoints) {
                return PlayerColor.WHITE;
            } else {
                return PlayerColor.EMPTY;
            }
        }
        return null;
    }

    /**
     * printGameState prints the current game state
     * for debugging.
     * Since we are printing the i represents y and j represents x. reversed from
     * all other
     * for loops in the program
     * also prints potential moves for black and white depending on who's turn it
     * is.
     */
    public void printGameState() {
        System.out.println("B: " + blackPoints + ", W: " + whitePoints);
        System.out.print(" ");
        for (int i = 0; i < board.length; i++) {
            System.out.print(i);
        }
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.print(i);
            for (int j = 0; j < board[0].length; j++) {
                PlayerColor space = board[j][i];
                if (space == PlayerColor.EMPTY) {
                    if (currentTurn == PlayerColor.BLACK) {
                        if (getValidMove(blackValidMoves, j, i) != null) {
                            System.out.print("*");
                        } else {
                            System.out.print(".");
                        }
                    } else {
                        if (getValidMove(whiteValidMoves, j, i) != null) {
                            System.out.print("#");
                        } else {
                            System.out.print(".");
                        }
                    }
                } else if (space == PlayerColor.BLACK) {
                    System.out.print("X");
                } else {
                    System.out.print("O");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * getCurrentPlayer is a getter for the player
     * whose turn it is in the game.
     * 
     * @return current turn player color
     **/
    public PlayerColor getCurrentTurn() {
        return currentTurn;
    }

    /**
     * getCell is a getter for the contents of the cell specified by the method
     * arguments.
     *
     * @param x column to retrieve
     * @param y row to retrieve
     * @return a player color value, empty if the space is a valid move, null if the
     *         space is empty and not a valid move
     *         This return paradigm allows for easy access to valid move spaces
     *         which can be displayed by the GUI
     **/
    public PlayerColor getBoardSpace(int x, int y) {
        PlayerColor space = board[x][y];
        if (space != PlayerColor.EMPTY) {
            return space;
        } else if (currentTurn == PlayerColor.BLACK) {
            if (getValidMove(blackValidMoves, x, y) != null) {
                return PlayerColor.EMPTY;
            }
            return null;
        } else {
            if (getValidMove(whiteValidMoves, x, y) != null) {
                return PlayerColor.EMPTY;
            }
            return null;
        }
    }

    // return points for black or white
    public int getBlackPoints() {
        return blackPoints;
    }

    public int getWhitePoints() {
        return whitePoints;
    }

    // returns the pass variable to see if the next turn will be passed
    public boolean getPass() {
        return pass;
    }

    // saves the current game board by printing it into an output file
    // first prints the current turn and then prints the board
    public void saveGameBoard() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileString));
        if (currentTurn == PlayerColor.BLACK) {
            bw.write("BLACK");
            bw.newLine();
        } else {
            bw.write("WHITE");
            bw.newLine();
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                PlayerColor space = board[j][i];
                if (space == PlayerColor.BLACK) {
                    bw.write("X");
                } else if (space == PlayerColor.WHITE) {
                    bw.write("O");
                } else {
                    bw.write("*");
                }
            }
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    // returns the output file string
    public String getFileString() {
        return fileString;
    }

    // loads a file from a user specific location
    // first reads the currnt turn then the board
    // also resets points and recalculates valid moves
    // Will throw Illegal argument exception if a file has incorrect formatting
    public void loadFile(String file) throws IOException {
        PlayerColor[][] boardTemp = new PlayerColor[8][8];
        BufferedReader br = new BufferedReader(new FileReader(file));
        int c;
        int x = 0;
        int y = 0;
        String turn = br.readLine();
        if (turn == null) {
            throw new IllegalArgumentException();
        }
        while ((c = br.read()) != -1) {
            char disk = (char) c;
            if (disk == 'X') {
                boardTemp[x][y] = PlayerColor.BLACK;
                x++;
            } else if (disk == 'O') {
                boardTemp[x][y] = PlayerColor.WHITE;
                x++;
            } else if (disk == '*') {
                boardTemp[x][y] = PlayerColor.EMPTY;
                x++;
            } else if (x == 8) {
                y++;
                x = 0;
            }
        }
        for (PlayerColor[] spaces : boardTemp) {
            for (PlayerColor space : spaces) {
                if (space == null) {
                    throw new IllegalArgumentException();
                }
            }
        }
        if (turn.equals("BLACK")) {
            currentTurn = PlayerColor.BLACK;
        } else if (turn.equals("WHITE")) {
            currentTurn = PlayerColor.WHITE;
        } else {
            throw new IllegalArgumentException();
        }

        // board temp is used so that a board is not loaded in until it has been
        // verified as valid
        board = boardTemp;

        // points and valid moves are reset
        resetPoints();
        blackValidMoves = generateValidMoves(PlayerColor.BLACK);
        whiteValidMoves = generateValidMoves(PlayerColor.WHITE);

        // if the game is loaded into pass state some additional changes are required
        if (currentTurn == PlayerColor.BLACK && blackValidMoves.isEmpty()) {
            pass = true;
            currentTurnFlip();
        } else if (currentTurn == PlayerColor.WHITE && whiteValidMoves.isEmpty()) {
            pass = true;
            currentTurnFlip();
        }
    }

    /**
     * This main method illustrates how the model is completely independent of
     * the view and controller. We can play the game from start to finish
     * without ever creating a Java Swing object.
     *
     * This is modularity in action, and modularity is the bedrock of the
     * Model-View-Controller design framework.
     *
     * Run this file to see the output of this method in your console.
     */
    public static void main(String[] args) throws IOException {
        Othello o = new Othello();
        o.loadFile("files/input/passTurn.txt");
    }
}
