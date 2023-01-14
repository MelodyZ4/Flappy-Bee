//This program will be a variation on the classic mobile game Flappy Bird except the player plays as a Minecraft bee, there is a possiblity of gaining extra lives, and a high score system has been implemented.

//Imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.File;

//Bee class
//Child of JFrame, uses KeyListener and ActionListener
//Creates and displays the game
public class Bee extends JFrame implements KeyListener, ActionListener {

  //Final variables
  private static final int WIDTH = 600;
	private static final int HEIGHT = 500;
  private final int GAP_HEIGHT = 70;
  private final int BEE_WIDTH = 40;
  private final int BEE_HEIGHT = 40;

  //Variables
	private double xPos = 40;
	private double yPos = HEIGHT/2;
	private double xSpeed = 4;
  private int lives;
  private int score;
  private int temp, temp2; //used to calculate the score and lives variables respectively
  private static int highScore = 0;
  private static ArrayList<Rectangle> pipes = new ArrayList<Rectangle>();
  private static ArrayList<Rectangle> life = new ArrayList<Rectangle>();
  private boolean start, finish, skin;

  //Color variables
  Color skyBlue = new Color(138, 212, 223);
  Color grass = new Color(110, 204, 138);
  Color ground = new Color(223, 217, 163);
  Color pipeGreen = new Color(138, 209, 82);

  //Graphic variables
  public Rectangle bee;
  //private Rectangle life;
  private Image beeImage;
  private Image heartImage;

  //Text variables
  private Font smallBold = new Font("Helvetica", Font.BOLD, 16);
  private Font bigBold = new Font("Helvetica", Font.BOLD, 32);
  private Font med = new Font("Helvetica", Font.PLAIN, 24);

  //Constructor
  //Pre: none
  //Post: A new window is created with the width and height, the bee and pipes are created, and the timer is started
  public Bee (String n) {
    super(n);
    setSize(WIDTH, HEIGHT);

    //Creating the bee
    bee = new Rectangle((int)xPos, (int)yPos, BEE_WIDTH, BEE_HEIGHT);

    //KeyListener is enabled
    addKeyListener(this);

    //A life powerup is created
    createLife();

    start = false;
    finish = false;
    skin = false;

    //The timer periodically calls on actionPerformed
    Timer time = new Timer(20, this);
    time.start();
  }

  //Overrides paint method
  //Pre: none
  //Post: draws the ground, trees, pipes, bee, powerups and certain sentences on the screen depending on the status of gameplay
  public void paint(Graphics g) {
    //super.paint(g);

    //Sky
    g.setColor(skyBlue);
    g.fillRect(0, 0, WIDTH, HEIGHT);

    //"Trees"
    g.setColor(grass);
    g.fillRect(0, HEIGHT-70, WIDTH, 70);

    //Ground
    g.setColor(ground);
    g.fillRect(0, HEIGHT-50, WIDTH, 50);
    
    //Pipes
    for(int i = 0; i < pipes.size()-1; i += 2) {
      paintPipes(g, pipes.get(i), pipes.get(i+1));
    }

    //Powerups
    if(life.size() > 0) {
      paintLife(g, life.get(0));
    }
    
    //Bee
    g.setColor(skyBlue);
    g.fillRect((int)xPos, (int)yPos, BEE_WIDTH, BEE_HEIGHT);
    g.drawImage(beeImage, (int)xPos, (int)yPos, this);

    //Game screen
    g.setColor(Color.BLACK);
    if(!finish && start)
		{
      g.setFont(smallBold);
			g.drawString("Current score: " + String.valueOf(score), WIDTH-160, 60);
      g.drawString("High score: " + String.valueOf(highScore), WIDTH-160, 80);
      g.drawString("Lives: " + String.valueOf(lives), WIDTH-160, 100);
		}

    //Game over screen
    if(finish) {
      g.setFont(bigBold);
      g.drawString("GAME OVER :(", 200, HEIGHT/2-50);
      g.setFont(med);
      g.drawString("Score: " + score, WIDTH/2-40, HEIGHT/2-20);
      g.drawString("Best: " + highScore, WIDTH/2-40, HEIGHT/2+10);
      g.drawString("Press SPACE to RESTART!", 150, HEIGHT/2+50);
    }
    //Starting screen
    else if (!start && !finish && !skin) {
      g.setFont(bigBold);
      g.drawString("WELCOME TO FLAPPY BEE!", 75, HEIGHT/2-50);
      g.setFont(med);
      g.drawString("Press the NUMBER of the SKIN you want to choose!", 20, HEIGHT/2-20);
      g.drawString("1. Bee           2. Gay bee           3. Bi bee", 75, HEIGHT/2+10);
      g.drawString("    4. Lesbian bee           5. Trans bee", 75, HEIGHT/2+40);
    }
    //Screen after skin is chosen
    else if (!start && !finish && skin) {
      g.setFont(med);
      g.drawString("Press SPACE to JUMP", 160, HEIGHT/2-100);
      g.drawString("CLOSE the window to QUIT", 140, HEIGHT/2-70);
      g.drawString("Get the HEARTS for EXTRA LIVES", 120, HEIGHT/2-40);
      g.drawString("Press SPACE to START!", 150, HEIGHT/2+50);
    }
  }

  //Overrides actionPerformed method
  //Pre: none
  //Post: scrolls the pipes across the screen and sets up death conditions
  public void actionPerformed(ActionEvent evt) {
    if(start && !finish){
      //Continuously makes bee fall
      yPos += 4;
      
      createPipe();

      //Loop to scroll pipes across the screen
      for(int i = 0; i < pipes.size(); i++) {
        Rectangle rect = pipes.get(i);

        //Makes the pipes scroll leftwards
        rect.x -= xSpeed;

        //Removes rectangles as soon as they are fully off the screen to ensure positioning of new pipes remains consistent
        if(rect.x == -50) {
          pipes.remove(rect);
          //Creates a new pipe every other rectangle removed as there are two rectangles per pipe
          if(i%2 == 0) {
            createPipe();
          }
        }

        if(life.size() > 0 && (life.get(0).intersects(rect) || rect.intersects(life.get(0)))) {
          life.remove(0);
          createLife();
        }
      }

      //Does not allow bee to go off the screen and kills bee if it touches the ground
      if(yPos < 37) {
        yPos = 38;
      }
      else if(yPos > HEIGHT-110) {
        yPos = HEIGHT-110;
        lives--;
      }

      //Tests each pipe to see if bee meets death conditions
      for(int i = 1; i < pipes.size(); i += 2) {
        Rectangle sky = pipes.get(i);
          
        //If the bee is in between the pipes and goes too high or too low, player loses a life
        if(bee.x + BEE_WIDTH >= sky.x && bee.x <= sky.x + 50) {
          if(yPos < sky.y || yPos + BEE_HEIGHT > sky.y + GAP_HEIGHT) {
            lives--;
          }
          //Increases number used to calculate score if bee doesn't die
          else {
            temp++;
          }
        }
      }

      //Makes sure score is increased by 1 for 1 pipe
      if(temp % 20 == 0 || (temp-1) % 20 == 0 || (temp+1) % 20 == 0) {
        score = temp/20;
      }

      if(life.size() == 0) {
        createLife();
      }

      //Moves life powerup to the left
      //if(life.size() > 0) {
        life.get(0).x -= xSpeed;
      //}

      //Randomly spawns powerup when the previous one moves off the screen
      if(life.get(0).x <= -50) {
        life.remove(0);
        createLife();
      }

      //If the bee is over the powerup, increase the number used to calculate the number of lives
      //Note the the bee must be completely over the heart in order to acquire the life, not just touching 
      if(life.get(0).intersects(bee) || bee.intersects(life.get(0))) {
        //temp2++;
        lives++;
        life.remove(0);
      }

      //Makes sure lives is increased by 1 for 1 powerup
      // if(temp2 % 17 == 0 || (temp2+1) % 17 == 0 || (temp2-1) % 17 == 0) {
      //   lives = 1 + temp2/17;
      // }

      //If the bee dies, it's game over
      if(lives == 0) {
        finish = true;
        start = false;
        highScore();
      }
    }
    repaint();
  }

  //Overrides keyTyped method
  //Pre: none
  //Post: allows bee to jump, game to start and skin to be chosen
  public void keyTyped(KeyEvent e) {

    //When space is pressed and the game is over, the player restarts
    if(finish && !start && e.getKeyChar() == ' ') {
      restart();
    }

    //When certain numbers are pressed, a specific skin is chosen
    if(!skin) {
      if (e.getKeyChar() == '2') {
        try {
          beeImage = ImageIO.read(new File("gayBee.png"));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      else if (e.getKeyChar() == '3') {
        try {
          beeImage = ImageIO.read(new File("biBee.png"));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      else if (e.getKeyChar() == '4') {
        try {
          beeImage = ImageIO.read(new File("lesBeean.png"));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      else if (e.getKeyChar() == '5') {
        try {
          beeImage = ImageIO.read(new File("transBee.png"));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      //If the player doesn't choose any of the listed numbers, the game defaults to the normal bee skin
      else {
        try {
          beeImage = ImageIO.read(new File("bee.png"));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      skin = true;
    }

		if(!finish && e.getKeyChar() == ' ') {
      //If the game has not started yet, pressing space starts the game
      if(!start) {
        start = true;
        finish = false;
        score = 0;
        temp = 0;
        temp2 = 0;
        lives = 1;
        xPos = 40;
        yPos = HEIGHT/2;
      }
      //If the game has started, pressing space makes the bee jump
      else {
        yPos -= 35;
      }
		}
	}
	
  //An empty method that is needed for the KeyListener interface
  public void keyReleased(KeyEvent e) {
	}
	
  //An empty method that is needed for the KeyListener interface
	public void keyPressed(KeyEvent e) {
	}

  //Helper method
  //Pre: none
  //Post: generates a random number between two specified integers
  private int random(int max, int min) {
    return (int)(Math.random() * (max-min-1)) + min;
  }

  //Modifier method
  //Pre: none
  //Post: makes pipes with gap position according to a random number
  public void createPipe() {
    //adds green pipe from screen top to screen bottom
    pipes.add(new Rectangle(WIDTH + pipes.size()*160, 0, 50, HEIGHT - 70));

    //adds a randomly placed gap on pipe
    pipes.add(new Rectangle(WIDTH + (pipes.size()-1)*160, random(320, 100), 50, GAP_HEIGHT));
  }

  //Modifier method
  //Pre: none
  //Post: colours the pipes green and the gaps sky blue
  public void paintPipes(Graphics g, Rectangle pipe, Rectangle sky) {
    //paints the pipe
    g.setColor(pipeGreen);
    g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);

    //paints the sky section in the pipe
    g.setColor(skyBlue);
    g.fillRect(sky.x, sky.y, sky.width, sky.height);
  }

  //Modifier method
  //Pre: score and highScore must be initialized
  //Post: calculates the highest score
  public void highScore() {
    if(score > highScore) {
      highScore = score;
    }
  }

  //Modifier method
  //Pre: none
  //Post: Determines whether or not a life powerup appears based on the random number
  public void createLife() {
    //life = new Rectangle((score + random(score + 50, score + 4)) * 100 + WIDTH, (HEIGHT-70)/2, 40, 40);
    life.add(new Rectangle((score + random(score + 50, score + 4)) * 100 + WIDTH, (HEIGHT-70)/2, 40, 40));
  }

  //Modifier method
  //Pre: none
  //Post: colours the life powerup red
  public void paintLife(Graphics g, Rectangle life) {
    g.setColor(skyBlue);
    g.fillRect(life.x, life.y, life.width, life.height);
    try {
      heartImage = ImageIO.read(new File("heart.png"));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    g.drawImage(heartImage, life.x, life.y, this);
  }

  //Modifier method
  //Pre: none
  //Post: places the bee at its starting position and restarts the game
  public void restart() {
    bee = new Rectangle((int)xPos, (int)yPos, BEE_WIDTH, BEE_HEIGHT);

    createLife();

    pipes.clear();

    start = false;
    finish = false;
    score = 0;
    temp = 0;
    temp2 = 0;
    lives = 1;
    xPos = 40;
    yPos = HEIGHT/2;
  }
}
