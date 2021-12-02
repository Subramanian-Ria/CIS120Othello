package org.cis120.Othello;

import java.util.LinkedList;
import java.util.TreeSet;

//used for keeping track of the current turn or what pieces are on the board
//empty squares are represented by null in order to maintain logical consistency between the
//enum representing a player and the enum representing a piece on the board
//public so that it can be referenced from anywhere and the representation of the pieces and the
//players is consistent
enum PlayerColor
{
    BLACK, WHITE
}
public class Othello {
    //x,y format
    private PlayerColor[][] board;
    private PlayerColor currentTurn;

    private int blackPoints;
    private int whitePoints;

    private TreeSet<ValidMove> blackValidMoves;
    private TreeSet<ValidMove> whiteValidMoves;

    //Othello constructor
    public Othello() {
        reset();
    }

    //Sets up the starting board state
    private void initBoard()
    {
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                board[i][j] = null;
            }
        }
        board[3][3] = PlayerColor.BLACK;
        board[3][4] = PlayerColor.WHITE;
        board[4][3] = PlayerColor.WHITE;
        board[4][4] = PlayerColor.BLACK;
    }

    private TreeSet<ValidMove> generateValidMoves(PlayerColor player)
    {
        TreeSet<ValidMove> moves = new TreeSet<>();
        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0 ; j < board[0].length; j++)
            {
                if (board[i][j] != null)
                {
                    for (int k = -1; k < 2; k++)
                    {
                        for (int l = -1; l < 2; l++)
                        {
                            if (k == 0 && l == 0 || board[i + k][j + l] != null)
                            {
                                continue;
                            }
                            LinkedList<int[]> flipped = checkMove(i + k, j + l, player);
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
        TreeSet<ValidMove> moves;
        if (player == PlayerColor.BLACK)
        {
            moves = blackValidMoves;
        }
        else
        {
            moves = whiteValidMoves;
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
            blackValidMoves = generateValidMoves(PlayerColor.BLACK);
            whiteValidMoves = generateValidMoves(PlayerColor.WHITE);
            return true;
        }
        return false;
    }

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

    private LinkedList<int[]> checkMove(int x, int y, PlayerColor player)
    {
        LinkedList<int[]> flipped = new LinkedList<>();
        if (board[x][y] == null)
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
                            new LinkedList<>()));
                }
            }
        }
        return flipped;
    }

    //Helper function to cast a ray over the board to figure out if a certain direction is valid
    //i.e. will result in a valid "flanking maneuver"
    private LinkedList<int[]> rayCast(int x, int y, int[] ray, PlayerColor player,
                                     LinkedList<int[]> rayQueue)
    {
        if (x > 7 || y > 7 || x < 0 || y < 0 || board[x][y] == null)
        {
            return new LinkedList<>();
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
    }

    public boolean turnValid(int x, int y, PlayerColor player)
    {
        return true;
    }

    /**
     * checkWinner checks whether the game has reached a win condition.
     * checkWinner only looks for horizontal wins.
     *
     * @return 0 if nobody has won yet, 1 if player 1 has won, and 2 if player 2
     *         has won, 3 if the game hits stalemate
     *
    public int checkWinner() {
        // Check horizontal win
        for (int i = 0; i < board.length; i++) {
            if (board[i][0] == board[i][1] &&
                    board[i][1] == board[i][2] &&
                    board[i][1] != 0) {
                gameOver = true;
                if (player1) {
                    return 1;
                } else {
                    return 2;
                }
            }
        }

        if (numTurns >= 9) {
            gameOver = true;
            return 3;
        } else {
            return 0;
        }
    }

    /**
     * printGameState prints the current game state
     * for debugging.
     * Since we are printing the i represents y and j represents x. reversed from all other for loops in the program
     */
    public void printGameState() {
        for (int i = 0; i< board.length; i++)
        {
            System.out.print(i);
        }
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.print(i);
            for (int j = 0; j < board[0].length; j++) {
                PlayerColor space = board[j][i];
                if (space == null)
                {
                    if (getValidMove(blackValidMoves, j, i) != null)
                    {
                        System.out.print("*");
                    }
                    else if (getValidMove(whiteValidMoves, j, i) != null)
                    {
                        System.out.print("#");
                    }
                    else
                    {
                        System.out.print(".");
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
     * resets the game state to start a new game.
     **/
    public void reset() {
        board = new PlayerColor[8][8];
        currentTurn = PlayerColor.BLACK;
        blackPoints = 0;
        whitePoints = 0;
        blackValidMoves = new TreeSet<>();
        whiteValidMoves = new TreeSet<>();
        initBoard();
        blackValidMoves = generateValidMoves(PlayerColor.BLACK);
        whiteValidMoves = generateValidMoves(PlayerColor.WHITE);
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
        return board[x][y];
    }

    public int getBlackPoints()
    {
        return blackPoints;
    }

    public int getWhitePoints()
    {
        return whitePoints;
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
    public static void main(String[] args) {
        Othello o = new Othello();
        o.printGameState();
        System.out.println(o.playTurn(2,4, PlayerColor.BLACK));
        o.printGameState();
    }
}
