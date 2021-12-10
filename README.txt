=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: riasub
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays
    Used to model the board state.
    This is a good use of 2D arrays as a 2D array is basically equivalent to the othello board. I used a custom enum type to fill the array. This was useful as i wanted to represent a black disk, a white disk, and an empty space.
    This allowed my code to be easily readable and for me to not put null values into my board array.
    My board is completely encapsulated and all of my iteration is either enhanced for, based on board.length, or has specific if statements to handle out of bounds values before the array is accessed
    the array is iterated through for display, for generating valid moves, for printing the board, for generating point values after the board is loaded
    the board is the only 2d array (besides tempboard which is used so that invalid file info is not passed directly into the board array)


  2. Recursion
    Recursion is used to generate valid moves for the players.
    These are stored and then determine what moves can be made by the player
    Recursion is necessary as the terminating condition of the recursion is a specific state rather than a number of iterations
    Recursion is used to search through the board object for "rays" to check if disks will be flipped by a certain move
    The base case is if the raycast reaches a blank square or a disk of the opposite color
    Each call of the function changes the position of the current disk in the direction of the ray argument and builds on the linked list of flipped disks

  3. JUnit Tests
    Used to test the basic implementation of the game in Othello.java
    Since the code is designed such that Othello.java runs on its own without a GUI, unit tests were straighforward to implement
    I used correct assertions and all of my tests are unique
    Some edge cases I tested were: what happens if a file of invalid format is loaded, what happens if a player tries to play in an invalid location, what if the wrong player tries to move

  4. File IO
    Used to save and load the game state
    The save implementation overwrites a certain .txt file whereas the load implementation allows for loading any filepath the user specifies.
    The board state and the current turn are saved as this is all that is required since the game does not have undo functionality and valid moves/points can be inferred from the board state
    This allows for picking up where you left off in your game, creating game states that would be impossible otherwise, and playing out from certain positions to see what results
    IO Exceptions are caught and handled by the GUI and event systems which will give the user a pop up informing them of what happened
    The parsing and writing work correctly

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
    EventStatus: An Object that stores "events (game won, turn passed, move error, etc.)" and notifies listeners when the event value is changed. THis allows a JOptionPane to be raised in the JFrame when an important event occurs
    GameBoard: interacts with the Othello class and creates the display based on the values returned by Othello, also registers mouseclicks and updates the status and changes event values
    Othello: the game model. Works on its own. deals with moves, checking move validity, checks win state, calculates points, saves and loads games, stores board state, etc.
    OthelloTest: Unit tests for Othello.java
    RunOthello: JFrame of the GUI, has load, save and run buttons. displays the jframe with buttons and gameboard. implements listeners to display events as JOption Panes
    ValidMove: object that stores a move's coordinates and all of the disks that would be flipped if that move was played. These objects are created when a valid move is generated and they are stored in a Treeset in Othello.java

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
    One challenge that I had was dealing with what I've chosen to call "events". These are things like errors, winning, turn passing, ties, etc.
    I wanted to display all of these in JOptionPanes to the player however JOptionPane requires access to the JFrame. This became a problem since the events were raised in the GameBoard.java class but the JFrame was in the RunOthello class
    I eventually solved the problem by using propertyChangeSupport as a kind of variable change listener which allowed me to implement variable changing in one class and variable listening in another

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
    I would like to change the way i represented valid moves. I ended up using null values to represent some squares in getboardspace despite having the empty value avaliable as i needed a seperate way to represent valid moves and non-valid moves. I feel this not ideal and unclear.
    I also would like to experiment further with the event listener structure and see if I could make it a little more streamlined. Currently I'm passing the eventstatus object as an argument to GmaeBoard.java since that is the class where the eventstatus needs to be modified. I would rather have the actual object declaraction take place in GameBoard.java but the listener and the status object must be declared in the same class therefore I had to pass the event status object as an arg to GameBoard.java instead.
    I think most of my private state is encapsulated. There are definitely a few things that I did not encapsulate as they were used in a few places and I could control every instance of that usage. At the very least, the board itself is encapsulated.
    I used the same separation of functionality as in the template code and I think that worked ok. One thing that bothers me is the fact that mouseclicks are handled both in gameboard and runothello rather than one or the other. But I think seperating this would complicate the program significantly
    In contrast, Othello.java functions entirely on its own as an othello game and that worked well for me.
    I also started implementing a basic minimax ai in a branch of my code. However, since I did not design my methods with this in mind, I found that it would require major changes to implement. I will probably look into implementing this over winter break
========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.
    Java Docs