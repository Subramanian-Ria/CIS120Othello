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

  1. 2d array. The 2d array keeps track of the state of the board. Since an othello board is an
  8x8 square, a 2d array is a natural representation of it.

  2. Recursion. Used to generate valid moves for each player. Recursion is used in the raycast function
  which "casts a ray" across the board searching for the end of the line of pieces to see if those
  pieces will be flipped given the move played. Recursion makes sense here as I need to build up
  a list of flipped disks and follow along the ray so I recursively move one step at a time along
  the ray to see if it will result in flipped disks.

  3. Collections. Used to store flipped disks in a linked list and store valid moves in a treeset.
  a treeset allows for no duplicates and to ensure it worked correctly i overrode the equals and
  compareTo methods. A linked list is a queue implementation and preserves insertion order.
  this is beneficial as the order will reflect the distance of a disk from the move made.
  the linked list of a valid move contains sequential rays of disks that will be flipped if that
  move was played.

  4.

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
  ValidMove contains the valid move object which is stored in a collection in the othello class.
  The valid move object has coordinates and a linked list of disks that would be flipped if the move
  was played. This allows for storage of valid moves for both players in any given game state

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?



========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

  java docs
