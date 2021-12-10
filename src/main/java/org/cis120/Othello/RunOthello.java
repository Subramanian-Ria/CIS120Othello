package org.cis120.Othello;

/**
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class sets up the top-level frame and widgets for the GUI.
 * 
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 * 
 * In a Model-View-Controller framework, Game initializes the view,
 * implements a bit of controller functionality through the reset
 * button, and then instantiates a GameBoard. The GameBoard will
 * handle the rest of the game's view and controller functionality, and
 * it will instantiate a TicTacToe object to serve as the game's model.
 */
public class RunOthello implements Runnable {
    public void run() {
        // NOTE: the 'final' keyword denotes immutability even for local variables.

        // Top-level frame in which game components live
        final JFrame frame = new JFrame("Othello");
        frame.setLocation(330, 15);

        // prevents the window from being resizable
        frame.setResizable(false);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Setting up...");
        status_panel.add(status);

        // event status object
        EventStatus eventStatus = new EventStatus();

        // Game board
        final GameBoard board = new GameBoard(status, eventStatus);

        // sets background to green like classic othello boards
        Color boardColor = new Color(50, 130, 50);
        board.setBackground(boardColor);
        frame.add(board, BorderLayout.CENTER);

        // Reset button
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> board.reset());

        // Instructions button
        // Displays instructions for the user
        final JButton instructions = new JButton("Instructions");

        instructions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        frame, "I implemented the board game Othello/Reversi\n" +
                                "Two players, Black and White, take turns placing disks in " +
                                "empty places on the board by clicking in the squares of the " +
                                "board \n"
                                +
                                "A move is valid if it \"outflanks\" or surrounds some disks of " +
                                "the opposite color. These moves are highlighted with circle " +
                                "outlines\n"
                                +
                                "When such a move is made then the flanked disks are flipped to " +
                                "the opposite color\n"
                                +
                                "If no valid moves are available then a player must pass their " +
                                "turn\n"
                                +
                                "Players cannot pass their turn if they have moves available\n" +
                                "Once neither player can move then the game is over\n" +
                                "The winner is the player with the most disks in their color " +
                                "at the end of the game\n"
                                +
                                "The game has a reset button to restart and saving and loading " +
                                "functionalities to save and load a game\n"
                                +
                                "Current points and the current turn are displayed in the status " +
                                "bar at the bottom of the screen\n"
                                +
                                "The window cannot be resized to maintain the correct dimensions " +
                                "of the gameboard"
                );
            }
        });

        // save button
        final JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    board.saveGameBoard();
                    JOptionPane.showMessageDialog(frame, "Saved at " + board.getFileString());
                } catch (FileNotFoundException exception) {
                    JOptionPane.showMessageDialog(
                            frame, "File Note Found", "Error", JOptionPane.ERROR_MESSAGE
                    );
                    // File Not Found exception will alert the user
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(
                            frame, "IO Exception", "Error", JOptionPane.ERROR_MESSAGE
                    );
                    // IOException will give an error dialogue to the user
                }
            }
        });

        // load button
        // allows user to specify filepath
        final JButton load = new JButton("Load");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String filepath = JOptionPane.showInputDialog(frame, "Enter filepath");
                    board.loadGameBoard(filepath);
                } catch (FileNotFoundException exception) {
                    JOptionPane.showMessageDialog(
                            frame, "File Not Found", "Error", JOptionPane.ERROR_MESSAGE
                    );
                    // file not found exception will alert the user
                } catch (IOException exception) {
                    // IO exception will alert the user with an error dialogue
                    JOptionPane.showMessageDialog(
                            frame, "IO Exception", "Error", JOptionPane.ERROR_MESSAGE
                    );
                } catch (IllegalArgumentException exception) {
                    // ILLEGAL ARG Exception indicates improper file formatting
                    JOptionPane.showMessageDialog(
                            frame, "Improper File Formatting", "Error", JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        control_panel.add(reset);
        control_panel.add(save);
        control_panel.add(load);
        control_panel.add(instructions);

        // event listener inner class
        // listens to the event status object for any changes and if they are detected
        // then displays option frames with messages for the user
        class EventStatusListener implements PropertyChangeListener {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("EventStatus")) {
                    EventStatus eventSource = (EventStatus) event.getSource();
                    EventStatusEnum eventValue = (EventStatusEnum) event.getNewValue();
                    if (eventValue == EventStatusEnum.ERROR) {
                        JOptionPane.showMessageDialog(
                                frame, "Invalid Move", "Error", JOptionPane.ERROR_MESSAGE
                        );
                        eventSource.setEventNull();
                    } else if (eventValue == EventStatusEnum.WIN_BLACK) {
                        optionPane("BLACK Wins!", frame);
                        eventSource.setEventNull();
                    } else if (eventValue == EventStatusEnum.WIN_WHITE) {
                        optionPane("WHITE Wins!", frame);
                        eventSource.setEventNull();
                    } else if (eventValue == EventStatusEnum.TIE) {
                        optionPane("It's a Tie!", frame);
                        eventSource.setEventNull();
                    } else if (eventValue == EventStatusEnum.PASS_BLACK) {
                        optionPane("BLACK must pass their turn", frame);
                        eventSource.setEventNull();
                        board.passStatus();
                    } else if (eventValue == EventStatusEnum.PASS_WHITE) {
                        optionPane("WHITE must pass their turn", frame);
                        eventSource.setEventNull();
                        board.passStatus();
                    }
                }
                board.boardUpdate();
            }

            // helper method so i don't have to write JOptionPane a lot
            private void optionPane(String msg, JFrame frame) {
                JOptionPane.showMessageDialog(frame, msg);
            }
        }

        // instantiates listener and adds it to the event status object
        EventStatusListener listener = new EventStatusListener();
        eventStatus.addPropertyChangeListener(listener);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start the game
        board.reset();
    }
}