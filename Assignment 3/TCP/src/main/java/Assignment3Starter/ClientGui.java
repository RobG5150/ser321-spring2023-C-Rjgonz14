package Assignment3Starter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The ClientGui class is a GUI frontend that displays an image grid, an input text box,
 * a button, and a text area for status. 
 * 
 * Methods of Interest
 * ----------------------
 * show(boolean modal) - Shows the GUI frame with the current state
 *     -> modal means that it opens the GUI and suspends background processes. Processing still happens 
 *        in the GUI. If it is desired to continue processing in the background, set modal to false.
 * newGame(int dimension) - Start a new game with a grid of dimension x dimension size
 * insertImage(String filename, int row, int col) - Inserts an image into the grid
 * appendOutput(String message) - Appends text to the output panel
 * submitClicked() - Button handler for the submit button in the output panel
 * 
 * Notes
 * -----------
 * > Does not show when created. show() must be called to show he GUI.
 * 
 */
public class ClientGui implements Assignment3Starter.OutputPanel.EventHandlers {
  JDialog frame;
  PicturePanel picturePanel;
  OutputPanel outputPanel;

  //My added variables
  Random random = new Random();
  String input = "";
  String playerID = "";
  int score = 0;
  boolean gameOn = false;
  Map<String, Integer> leaderboard = new HashMap<String, Integer>();
  //Integer firstGuess = 5;
  //Integer secondGuess = 4;
  //Integer thirdGuess = 3;
  Integer points = 5;
  Integer correctAnswers = 0;
  String quoteSource[] = {"Captain_America", "Darth_Vader", "Homer_Simpson", "Jack_Sparrow", "Joker", "Tony_Stark", "Wolverine"};
  String ansChoices[] = {"Captain America", "Darth Vader", "Homer Simpson", "Jack Sparrow", "Joker", "Tony Stark", "Wolverine"};
  int moreCount = 0;
  boolean updated;
  int updateCode = -1;
  Integer currentImage = random.nextInt(0, 6);
  Integer currentImageNumber = 1;
  static int interval = 60; //60 second timer value
  static Timer timer;

  int delay = 1000;
  int period = 1000;


  /**
   * Construct dialog
   */
  public ClientGui() {
    frame = new JDialog();
    frame.setLayout(new GridBagLayout());
    frame.setMinimumSize(new Dimension(500, 500));
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // setup the top picture frame
    picturePanel = new PicturePanel();
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 0.25;
    frame.add(picturePanel, c);

    // setup the input, button, and output area
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    c.weighty = 0.75;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    outputPanel = new OutputPanel();
    outputPanel.addEventHandlers(this);
    frame.add(outputPanel, c);
  }

  /**
   * Shows the current state in the GUI
   *
   * @param makeModal - true to make a modal window, false disables modal behavior
   */
  public void show(boolean makeModal) {
    frame.pack();
    frame.setModal(makeModal);
    frame.setVisible(true);
  }

  /**
   * Creates a new game and set the size of the grid
   *
   * @param dimension - the size of the grid will be dimension x dimension
   */
  public void newGame(int dimension) {
    picturePanel.newGame(dimension);
    outputPanel.appendOutput("Started new game with a " + dimension + "x" + dimension + " board.");
  }

  /**
   * Insert an image into the grid at position (col, row)
   *
   * @param filename - filename relative to the root directory
   * @param row      - the row to insert into
   * @param col      - the column to insert into
   * @return true if successful, false if an invalid coordinate was provided
   * @throws IOException An error occured with your image file
   */
  public boolean insertImage(String filename, int row, int col) throws IOException {
    String error = "";
    try {
      // insert the image
      if (picturePanel.insertImage(filename, row, col)) {
        // put status in output
        outputPanel.appendOutput("Inserting " + filename + " in position (" + row + ", " + col + ")");
        return true;
      }
      error = "File(\"" + filename + "\") not found.";
    } catch (PicturePanel.InvalidCoordinateException e) {
      // put error in output
      error = e.toString();
    }
    outputPanel.appendOutput(error);
    return false;
  }

  /**
   * Submit button handling
   * <p>
   * Change this to whatever you need
   */
  @Override
  public void submitClicked() {
    input = outputPanel.getInputText();

    if (!input.isBlank()) {
      outputPanel.setInputText("");
      outputPanel.appendOutput(input);
    }

    if (input.isBlank()) {
      outputPanel.setInputText("");
    }
    else if (playerID.equals("")) {
      updateCode = 1;
      updated = true;
      System.out.println("received a name");
      playerID = input;
      outputPanel.appendOutput("Hello " + playerID + "! if you want to view leaderboard type 'leader' then press enter");
      outputPanel.appendOutput("if you want to start the game type 'start' then press enter\nTo exit the program, type 'quit' then press enter");
    }
    else if (input.equals("leader") && !gameOn) { // leaderboard
      updateCode = 2;
      updated = true;
      System.out.println("Got a leader");
      outputPanel.appendOutput(leaderboard.toString());
    } //Show leaderboard command
    else if (input.equals("start") && !gameOn) { // START
      updateCode = 3;
      updated = true;
      gameOn = true;
      nextImage();
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          System.out.println(setInterval());
        }
      }, delay, period);
    }//start game command
    else if (input.equals("next") && gameOn) { // NEXT
      updateCode = 4;
      updated = true;
      System.out.println("Skip Question");
      nextImage();
      score -= 2;
      outputPanel.setPoints(score);
      points = 5;
    } //next command
    else if (input.equals("more") && gameOn) { // MORE
      moreCount++;
      if (currentImageNumber < 4) {
        if (moreCount < 3) {
          updateCode = 4;
          updated = true;
          moreImages();
          points--;
        }
        else if (moreCount == 3) {
            updateCode = 4;
            updated = true;
            moreImages();
            points = 1;
          }
        }
      else {
          outputPanel.appendOutput("There are no more quotes.");
        }
      } //more command
    else if (input.equals("quit")) {
      System.out.println("Quitting");
      updateCode = 0;
      updated = true;
      System.exit(0);
    }//quit command
    else if (input.equals(ansChoices[currentImage].toLowerCase()) && gameOn) {
        System.out.println("Correct");
        outputPanel.appendOutput("Correct!");
        score += points;
        outputPanel.setPoints(score);
        points = 5;
        correctAnswers++;

        if (correctAnswers >= 3) {
          updateCode = 5;
          updated = true;
          System.out.println("Player Wins!");
          outputPanel.appendOutput("WINNER! You earned " + score + " points.");
          try {
            insertImage("img/win.jpg", 0, 0);
          }
          catch (IOException e) {
            e.printStackTrace();
          }
          gameReset();
        }
        else {
          nextImage();
        }

      } //correct answer case + win case
    else if (gameOn) {
        System.out.println("Wrong");
        outputPanel.appendOutput("WRONG!");
        if(interval == 0){
          updateCode = 5;
          updated = true;
          System.out.println("Player Lose");
          outputPanel.appendOutput("Out of time! You Lose! You earned " + score + "points.");
          try{
            insertImage("img/lose.jpg",0,0);
          }
          catch (IOException e){
            e.printStackTrace();
          }
          gameOn = false;
          gameReset();
        }
      }
    else {
        System.out.println("Unknown Command");
        outputPanel.appendOutput("Unknown Command!");
      }


    }

    /**
     * Key listener for the input text box
     *
     * Change the behavior to whatever you need
     */
    @Override
    public void inputUpdated (String input){
    }

    public void gameReset () {
      outputPanel.appendOutput("If you would like to play again, type your name in again and press enter. \nTo exit the program, type 'quit' and press enter");
      if (leaderboard.containsKey(playerID)) {
        if (score > leaderboard.get(playerID)) {
          leaderboard.put(playerID, score);
        }
      } else {
        leaderboard.put(playerID, score);
      }

      outputPanel.setPoints(0);
      points = 5;
      correctAnswers = 0;
      score = 0;
      playerID = "";
      gameOn = false;
    }

    public void nextImage () {
      currentImageNumber = 1;
      currentImage = (currentImage + 1) % 7;
      System.out.println(quoteSource[currentImage]);
      System.out.println("More details: 1");
      outputPanel.appendOutput("Who said this?");
      try {
        insertImage("img/" + quoteSource[currentImage] + "/quote1.png", 0, 0);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public void moreImages () {
      currentImageNumber++;
      points--;
      System.out.println("More details: " + currentImageNumber);
      outputPanel.appendOutput("Here is more details.");
      String imgStr = currentImageNumber.toString();

      try {
        insertImage("img/" + quoteSource[currentImage] + "/quote" + imgStr + ".png", 0, 0);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private static final int setInterval () {
      if (interval == 1) {
        timer.cancel();
      }
      return --interval;
    }
    public static void main (String[]args) throws IOException {

      // create the frame
      ClientGui cGui = new ClientGui();


      // set up the UI to display on image
      cGui.newGame(1);

      // add images to the grid
      cGui.insertImage("img/hi.png", 0, 0);
      cGui.outputPanel.appendOutput("Hello. Please enter your name: ");


      // show the GUI dialog as modal
      cGui.show(true); // you should not have your logic after this. You main logic should happen whenever "submit" is clicked
    }
  }
