package org.cis120.Othello;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class OthelloTest {

    Othello o = new Othello();

    // tests basic reset functionality
    @Test
    public void resetOthelloTest() {
        o.reset();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i == 3 || i == 4) && (j == 3 || j == 4)) {
                    continue;
                }
                assertTrue(
                        o.getBoardSpace(i, j) == PlayerColor.EMPTY || o.getBoardSpace(i, j) == null
                );
            }
        }

        assertEquals(PlayerColor.WHITE, o.getBoardSpace(3, 3));
        assertEquals(PlayerColor.WHITE, o.getBoardSpace(4, 4));
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(3, 4));
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(4, 3));
        assertEquals(PlayerColor.BLACK, o.getCurrentTurn());
        assertEquals(2, o.getBlackPoints());
        assertEquals(2, o.getWhitePoints());
    }

    // tests a basic turn test
    @Test
    public void simpleTurnTest() {
        o.reset();
        o.playTurn(3, 2, PlayerColor.BLACK);
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(3, 2));
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(3, 3));
        assertEquals(PlayerColor.WHITE, o.getCurrentTurn());
    }

    // tests exception if the wrong player tries to play a turn
    @Test
    public void wrongPlayerTest() {
        o.reset();
        assertThrows(IllegalArgumentException.class, () -> {
            o.playTurn(4, 2, PlayerColor.WHITE);
        });
    }

    // test if exception is thrown when invalid move is played
    @Test
    public void invalidMoveTest() {
        o.reset();
        assertThrows(IllegalArgumentException.class, () -> {
            o.playTurn(1, 1, PlayerColor.BLACK);
        });
    }

    // tests the basic load board functionality and turn determination, point
    // counting, and being able to play turns after a game is loaded
    @Test
    public void loadBoardTest() throws IOException {
        o.reset();
        o.loadFile("files/input/testBoard.txt");
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(3, 2));
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(3, 3));
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(4, 3));
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(3, 4));
        assertEquals(PlayerColor.WHITE, o.getBoardSpace(4, 4));
        assertEquals(PlayerColor.WHITE, o.getCurrentTurn());
        assertEquals(4, o.getBlackPoints());
        assertEquals(1, o.getWhitePoints());
        o.playTurn(2, 4, PlayerColor.WHITE);
        assertEquals(PlayerColor.WHITE, o.getBoardSpace(2, 4));
    }

    // tests if when the game is won the win function returns the correct player
    // and that moves cannot be played after the game is won
    @Test
    public void winTest() {
        o.reset();
        o.playTurn(5, 4, PlayerColor.BLACK);
        o.playTurn(3, 5, PlayerColor.WHITE);
        o.playTurn(2, 4, PlayerColor.BLACK);
        o.playTurn(5, 3, PlayerColor.WHITE);
        o.playTurn(4, 2, PlayerColor.BLACK);
        o.playTurn(5, 5, PlayerColor.WHITE);
        o.playTurn(6, 4, PlayerColor.BLACK);
        o.playTurn(4, 5, PlayerColor.WHITE);
        o.playTurn(4, 6, PlayerColor.BLACK);
        assertEquals(PlayerColor.BLACK, o.checkWinner());
        o.playTurn(1, 1, PlayerColor.BLACK);
        assertNull(o.getBoardSpace(1, 1));
    }

    // tests if correct passing functionality works
    @Test
    public void passTurnTest() throws IOException {
        o.reset();
        o.loadFile("files/input/playToPass.txt");
        o.playTurn(5, 3, PlayerColor.WHITE);
        assertEquals(PlayerColor.WHITE, o.getCurrentTurn());
        o.playTurn(2, 1, PlayerColor.WHITE);
        assertEquals(PlayerColor.WHITE, o.getBoardSpace(2, 1));
        assertEquals(PlayerColor.WHITE, o.getBoardSpace(3, 2));
    }

    // checks if points are correctly calculated when players make moves
    @Test
    public void pointsCalcTest() {
        o.reset();
        o.playTurn(5, 4, PlayerColor.BLACK);
        assertEquals(4, o.getBlackPoints());
        assertEquals(1, o.getWhitePoints());
        o.playTurn(3, 5, PlayerColor.WHITE);
        assertEquals(3, o.getBlackPoints());
        assertEquals(3, o.getBlackPoints());
    }

    // ensures that winner is null if the game is not complete
    @Test
    public void checkWinnerIncompleteGameTest() {
        o.reset();
        assertNull(o.checkWinner());
        o.playTurn(5, 4, PlayerColor.BLACK);
        assertNull(o.checkWinner());
    }

    // checks that play move throws exception when x, y are out of bounds
    @Test
    public void invalidMoveLocationTest() {
        o.reset();
        assertThrows(IllegalArgumentException.class, () -> o.playTurn(-1, -1, PlayerColor.BLACK));
    }

    // checks that getboardspace throws exception when x, y are out of bound
    @Test
    public void invalidGetBoardSpaceLocationTest() {
        o.reset();
        assertThrows(IllegalArgumentException.class, () -> o.getBoardSpace(-1, -1));
    }

    // tests basic save game functionality and tests load functionality in terms of
    // resetting to a previous state
    // guarantees that all variables are updated after a game is loaded into a
    // previous state
    @Test
    public void saveGameTest() throws IOException {
        o.reset();
        o.playTurn(5, 4, PlayerColor.BLACK);
        o.saveGameBoard();
        o.playTurn(3, 5, PlayerColor.WHITE);
        o.loadFile("files/output/output.txt");
        assertEquals(PlayerColor.WHITE, o.getCurrentTurn());
        assertTrue(o.getBoardSpace(3, 5) == null || o.getBoardSpace(3, 5) == PlayerColor.EMPTY);
        assertEquals(PlayerColor.WHITE, o.getBoardSpace(3, 3));
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(4, 3));
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(3, 4));
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(4, 4));
        assertEquals(PlayerColor.BLACK, o.getBoardSpace(5, 4));
        assertEquals(4, o.getBlackPoints());
        assertEquals(1, o.getWhitePoints());
        o.playTurn(3, 5, PlayerColor.WHITE);
        assertEquals(PlayerColor.WHITE, o.getBoardSpace(3, 5));
    }

    // checks for file not found exception
    @Test
    public void loadGameFileNotFoundTest() {
        o.reset();
        assertThrows(FileNotFoundException.class, () -> {
            o.loadFile("notfound");
        });
    }

    // checks if a won game is loaded then the winner is correctly determined
    @Test
    public void loadWonGameTest() throws IOException {
        o.reset();
        o.loadFile("files/input/wonBoard.txt");
        assertEquals(PlayerColor.BLACK, o.checkWinner());
        assertEquals(64, o.getBlackPoints());
        assertEquals(0, o.getWhitePoints());
    }

    // tests for invalid characters
    @Test
    public void loadInvalidCharactersTest() {
        o.reset();
        assertThrows(
                IllegalArgumentException.class, () ->
                        o.loadFile("files/input/invalidCharacters.txt")
        );
    }

    // tests for exeption when a file of the wrong width is input
    @Test
    public void loadInvalidLengthFileTest() {
        o.reset();
        assertThrows(
                IllegalArgumentException.class, () -> o.loadFile("files/input/invalidLength.txt")
        );
    }

    // tests for exception if a file of the wrong height is inputted
    @Test
    public void loadInvalidHeightFileTest() {
        o.reset();
        assertThrows(
                IllegalArgumentException.class, () -> o.loadFile("files/input/invalidHeight.txt")
        );
    }

    // checks that a game loaded in on a pass turn correctly passes
    @Test
    public void loadPassTurnTest() throws IOException {
        o.reset();
        o.loadFile("files/input/passTurn.txt");
        assertTrue(o.getPass());
        assertEquals(PlayerColor.WHITE, o.getCurrentTurn());
    }

    // checks if all valid moves are available in a board state
    @Test
    public void multipleValidMovesTest() throws IOException {
        o.reset();
        o.loadFile("files/input/testBoard.txt");
        o.playTurn(2, 2, PlayerColor.WHITE);
        assertEquals(PlayerColor.WHITE, o.getBoardSpace(2, 2));
        o.loadFile("files/input/testBoard.txt");
        o.playTurn(2, 4, PlayerColor.WHITE);
        assertEquals(PlayerColor.WHITE, o.getBoardSpace(2, 4));
        o.loadFile("files/input/testBoard.txt");
        o.playTurn(4, 2, PlayerColor.WHITE);
        assertEquals(PlayerColor.WHITE, o.getBoardSpace(4, 2));
    }

    // checks for empty file loading
    @Test
    public void loadEmptyFileTest() throws IOException {
        o.reset();
        assertThrows(IllegalArgumentException.class, () -> o.loadFile("files/input/emptyText.txt"));
    }

    // tests for invalid input for a file that has rows that are too short
    @Test
    public void invalidShortFileLengthTest() throws IOException {
        o.reset();
        assertThrows(
                IllegalArgumentException.class, () -> o.loadFile("files/input/shortLengthBoard.txt")
        );
    }

    // tests for invalid input for a file that has too small a height
    @Test
    public void invalidShortHeightFileTest() throws IOException {
        o.reset();
        assertThrows(
                IllegalArgumentException.class, () -> o.loadFile("files/input/shortHeightBoard.txt")
        );
    }

    // checks that loading an empty board results in a tie game not a continuing
    // game
    @Test
    public void loadEmptyBoardTest() throws IOException {
        o.reset();
        o.loadFile("files/input/emptyBoard.txt");
        assertEquals(PlayerColor.EMPTY, o.checkWinner());
    }
}
