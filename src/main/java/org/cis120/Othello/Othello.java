package org.cis120.Othello;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;

//used for keeping track of the current turn or what pieces are on the board
//public so that it can be referenced from anywhere and the representation of the pieces and the
//players is consistent
enum PlayerColor
{
    BLACK, WHITE, EMPTY
}
public class Othello {
    //x,y format
    private PlayerColor[][] board;

    //stores current turn using player color enum
    private PlayerColor currentTurn;

    //keeps track of points for black and white
    private int blackPoints;
    private int whitePoints;

    //stores valid moves for both players
    private TreeSet<ValidMove> blackValidMoves;
    private TreeSet<ValidMove> whiteValidMoves;

    //Tracks game over state
    private boolean gameOver;

    private final String fileString = "output/output.txt";

    //Othello constructor
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
        initBoard();
        blackValidMoves = generateValidMoves(PlayerColor.BLACK);
        whiteValidMoves = generateValidMoves(PlayerColor.WHITE);
    }

    //Sets up the starting board state
    private void initBoard()
    {
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                board[i][j] = PlayerColor.EMPTY;
            }
        }
        board[3][3] = PlayerColor.WHITE;
        board[3][4] = PlayerColor.BLACK;
        board[4][3] = PlayerColor.BLACK;
        board[4][4] = PlayerColor.WHITE;
    }

    private void currentTurnFlip()
    {
        if (currentTurn == PlayerColor.BLACK)
        {
            currentTurn = PlayerColor.WHITE;
        }
        else
        {
            currentTurn = PlayerColor.BLACK;
        }
    }

    //generates all valid moves for one player
    //specifically only checks squares that are adjacent to placed disks for greater efficiency
    private TreeSet<ValidMove> generateValidMoves(PlayerColor player)
    {
        TreeSet<ValidMove> moves = new TreeSet<>();
        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0 ; j < board[0].length; j++)
            {
                if (board[i][j] != PlayerColor.EMPTY)
                {
                    for (int k = -1; k < 2; k++)
                    {
                        for (int l = -1; l < 2; l++)
                        {
                            if (k == 0 && l == 0 || i + k > 7 || j + l > 7 || i + k < 0 ||
                                    j + l < 0 || board[i + k][j + l] != PlayerColor.EMPTY)
                            {
                                continue;
                            }
                            LinkedHashSet<int[]> flipped = checkMove(i + k, j + l, player);
                            if (!flipped.isEmpty())
                            {
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

    //gets a move from a valid moves treeset
    private ValidMove getValidMove(TreeSet<ValidMove> moves, int x, int y)
    {
        for (ValidMove move : moves)
        {
            if (move.getX() == x && move.getY() == y)
            {
                return move;
            }
        }
        return null;
    }

    //checks if a move is valid using raycast helper
    private LinkedHashSet<int[]> checkMove(int x, int y, PlayerColor player)
    {
        LinkedHashSet<int[]> flipped = new LinkedHashSet<>();
        if (board[x][y] == PlayerColor.EMPTY)
        {
            for (int i = -1; i < 2; i++)
            {
                for (int j = -1; j < 2; j++)
                {
                    if (i == 0 && j == 0)
                    {
                        continue;
                    }
                    flipped.addAll(rayCast(x + i, y + j, new int[]{i, j}, player,
                            new LinkedHashSet<>()));
                }
            }
        }
        return flipped;
    }

    //Helper function to cast a ray over the board to figure out if a certain direction is valid
    //i.e. will result in a valid "flanking maneuver"
    private LinkedHashSet<int[]> rayCast(int x, int y, int[] ray, PlayerColor player,
                                      LinkedHashSet<int[]> rayQueue)
    {
        if (x > 7 || y > 7 || x < 0 || y < 0 || board[x][y] == PlayerColor.EMPTY)
        {
            return new LinkedHashSet<>();
        }
        else if (board[x][y] != player)
        {
            rayQueue.add(new int[]{x, y});
            return rayCast(x + ray[0], y + ray[1], ray, player, rayQueue);
        }
        else
        {
            return rayQueue;
        }
    }

    /**
     * playTurn allows players to play a turn. Returns true if the move is
     * successful and false if a player tries to play in a location that is
     * taken or after the game has ended. If the turn is successful and the game
     * has not ended, the player is changed. If the turn is unsuccessful or the
     * game has ended, the player is not changed.
     *
     * @param x column to play in
     * @param y row to play in
     * @param player move color
     * @return whether the turn was successful
     **/
    public boolean playTurn(int x, int y, PlayerColor player) {
        System.out.println(x + ", " + y);
        TreeSet<ValidMove> moves;
        if (player!=currentTurn)
        {
            throw new IllegalArgumentException();
        }
        if (player == PlayerColor.BLACK)
        {
            moves = blackValidMoves;
        }
        else
        {
            moves = whiteValidMoves;
        }
        if (checkWinner() != null || gameOver)
        {
            gameOver = true;
            return false;
        }
        //TODO: pass pop up
        if (moves.isEmpty())
        {
            currentTurnFlip();
            return false;
        }
        ValidMove move = getValidMove(moves, x, y);
        if (move != null)
        {
            board[x][y] = player;
            LinkedList<int[]> flipped = move.getFlippedDisks();
            for (int[] disk : flipped)
            {
                board[disk[0]][disk[1]] = player;
            }
            if (player == PlayerColor.BLACK)
            {
                blackPoints += flipped.size() + 1;
                whitePoints -= flipped.size();
            }
            else
            {
                whitePoints += flipped.size() + 1;
                blackPoints -= flipped.size();
            }
            blackValidMoves = generateValidMoves(PlayerColor.BLACK);
            whiteValidMoves = generateValidMoves(PlayerColor.WHITE);
            currentTurnFlip();
            return true;
        }
        throw new IllegalArgumentException();
    }

    /*
    private void updateValidMoves(TreeSet<ValidMove> moves, LinkedList<int[]> flipped, PlayerColor player)
    {
        moves.removeIf(move -> board[move.getX()][move.getY()] != null);
        for (ValidMove move : moves)
        {
            for (int[] disk : flipped)
            {
                if (move.contains(disk) && checkMove(move.getX(), move.getY(), player).isEmpty())
                {
                    moves.remove(move);
                }
            }
        }
    }*/

    /**
     * checkWinner checks whether the game has reached a win condition.
     * checkWinner only looks for horizontal wins.
     *
     * @return 0 if nobody has won yet, 1 if player 1 has won, and 2 if player 2
     *         has won, 3 if the game hits stalemate
     **/
    public PlayerColor checkWinner() {
        if (blackValidMoves.isEmpty() && whiteValidMoves.isEmpty())
        {
            if (blackPoints > whitePoints)
            {
                return PlayerColor.BLACK;
            }
            else if (whitePoints > blackPoints)
            {
                return PlayerColor.WHITE;
            }
            else
            {
                return PlayerColor.EMPTY;
            }
        }
        return null;
    }

    /**
     * printGameState prints the current game state
     * for debugging.
     * Since we are printing the i represents y and j represents x. reversed from all other
     * for loops in the program
     * also prints potential moves for black and white depending on who's turn it is.
     */
    public void printGameState() {
        System.out.println("B: " + blackPoints + ", W: " + whitePoints);
        System.out.print(" ");
        for (int i = 0; i< board.length; i++)
        {
            System.out.print(i);
        }
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.print(i);
            for (int j = 0; j < board[0].length; j++) {
                PlayerColor space = board[j][i];
                if (space == PlayerColor.EMPTY)
                {
                    if (currentTurn == PlayerColor.BLACK)
                    {
                        if (getValidMove(blackValidMoves, j, i) != null)
                        {
                            System.out.print("*");
                        }
                        else
                        {
                            System.out.print(".");
                        }
                    }
                    else
                    {
                        if (getValidMove(whiteValidMoves, j, i) != null)
                        {
                            System.out.print("#");
                        }
                        else
                        {
                            System.out.print(".");
                        }
                    }
                }
                else if (space == PlayerColor.BLACK)
                {
                    System.out.print("X");
                }
                else
                {
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
     * @return an integer denoting the contents of the corresponding cell on the
     *         game board. 0 = empty, 1 = Player 1, 2 = Player 2
     **/
    public PlayerColor getBoardSpace(int x, int y) {
        PlayerColor space = board[x][y];
        if (space != PlayerColor.EMPTY)
        {
            return space;
        }
        else if (currentTurn == PlayerColor.BLACK)
        {
            if (getValidMove(blackValidMoves, x, y) != null)
            {
                return PlayerColor.EMPTY;
            }
            return null;
        }
        else
        {
            if (getValidMove(whiteValidMoves, x, y) != null)
            {
                return PlayerColor.EMPTY;
            }
            return null;
        }
    }

    public int getBlackPoints()
    {
        return blackPoints;
    }

    public int getWhitePoints()
    {
        return whitePoints;
    }

    public void saveGameBoard() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileString));
        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[0].length; j++)
            {
                PlayerColor space = board[j][i];
                if (space == PlayerColor.BLACK)
                {
                    bw.write("X");
                }
                else if (space == PlayerColor.WHITE)
                {
                    bw.write("O");
                }
                else
                {
                    bw.write("*");
                }
            }
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    public String getFileString()
    {
        return String.valueOf(fileString);
    }

    public void loadFile(String file) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(file));
        int c;
        int x = 0;
        int y = 0;
        while ((c=br.read()) != -1)
        {
            char disk = (char) c;
            if (disk == 'X')
            {
                board[x][y] = PlayerColor.BLACK;
                x++;
            }
            else if (disk == 'O')
            {
                board[x][y] = PlayerColor.WHITE;
                x++;
            }
            else if (disk == '*')
            {
                board[x][y] = PlayerColor.EMPTY;
                x++;
            }
            else if (x == 8)
            {
                y++;
                x = 0;
            }
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
        o.printGameState();
        o.loadFile("output/output.txt");
        o.printGameState();
        /*o.printGameState();
        o.playTurn(5,4, PlayerColor.BLACK);
        o.printGameState();
        o.playTurn(3, 5, PlayerColor.WHITE);
        o.printGameState();
        o.playTurn(2, 4, PlayerColor.BLACK);
        o.printGameState();
        o.playTurn(5, 3, PlayerColor.WHITE);
        o.printGameState();
        o.playTurn(4, 2, PlayerColor.BLACK);
        o.printGameState();
        o.playTurn(5, 5, PlayerColor.WHITE);
        o.printGameState();
        o.playTurn(6, 4, PlayerColor.BLACK);
        o.printGameState();
        o.playTurn(4, 5, PlayerColor.WHITE);
        o.printGameState();
        o.playTurn(4, 6, PlayerColor.BLACK);
        o.printGameState();
        o.playTurn(1, 2, PlayerColor.WHITE);*/
    }
}
