//This program will be a variation on the classic mobile game Flappy Bird except the player plays as a Minecraft bee, there is a possiblity of gaining extra lives, and a high score system has been implemented.

//Imports
import java.awt.*;
import javax.swing.*;

//Main class
//Creates a bee variable and sets the windwo parameters
class Main {

  //Main method
  //Pre: none
  //Post: A new Bee object is created and a frame is displayed
  public static void main(String[] args) {
    Bee game = new Bee("FLAPPY BEE");
		game.setLocationRelativeTo(null);
		game.setResizable(true); 
		game.setVisible(true); 
		game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }
}
