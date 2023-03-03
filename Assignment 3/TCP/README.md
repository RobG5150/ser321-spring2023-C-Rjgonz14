# Assignment 3 Starter Code

## GUI Usage

### Code

1. Create an instance of the GUI

   ```
   ClientGui main = new ClientGui();
   ```
   ```

*Depending on how you want to run the system, 2 and 3 can be run how you want*

2. Insert image

   ```
   // the filename is the path to an image
   // the first coordinate(0) is the row to insert in to
   // the second coordinate(1) is the column to insert in to
   // you can change coordinates to see the image move around the box
   main.insertImage("img/Pineapple-Upside-down-cake_0_1.jpg", 0, 1);
   ```

3. Show GUI

   ```
   // true makes the dialog modal meaning that all interaction allowed is 
   //   in the windows methods.
   // false makes the dialog a pop-up which allows the background program 
   //   that spawned it to continue and process in the background.
   main.show(true);
   ```

### Terminal 

```
gradle Gui
```
*Note the current example will show you some sample errors, see main inside ClientGUI.java*


## Files

### GridMaker.java

#### Summary

> This takes in an image and a dimension and makes a grid of the image, we do not really need a grid in this assignment. 


### ClientGui.java
#### Summary

> This is the main GUI to display the picture grid. 

#### Methods
  - show(boolean modal) :  Shows the GUI frame with the current state
     * NOTE: modal means that it opens the GUI and suspends background processes. Processing still happens in the GUI If it is desired to continue processing in the background, set modal to false.
   * newGame(int dimension) :  Start a new game with a grid of dimension x dimension size
   * insertImage(String filename, int row, int col) :  Inserts an image into the grid
   * appendOutput(String message) :  Appends text to the output panel
   * submitClicked() :  Button handler for the submit button in the output panel

### PicturePanel.java

#### Summary

> This is the image grid

#### Methods

- newGame(int dimension) :  Reset the board and set grid size to dimension x dimension
- insertImage(String fname, int row, int col) :  Insert an image at (col, row)

### OutputPanel.java

#### Summary

> This is the input box, submit button, and output text area panel

#### Methods

- getInputText() :  Get the input text box text
- setInputText(String newText) :  Set the input text box text
- addEventHandlers(EventHandlers handlerObj) :  Add event listeners
- appendOutput(String message) :  Add message to output text

### Project Description
### Requirements for Assignment
#### "x" next to task means completed "-" means incomplete

- Connect server and client                                                                                 x
- Have GUI for program pop up x
- have server ask for name x
- have client send name x
- have server greet client and give options x
- have server show leaderboard when prompted x
- have server start game when prompted x
- have server display quotes and wait for answer x
- have client respond with answer x
- have server distinguish if client answer is right or wrong and display result x
- when client gives correct answer have server display new quote x
- when client types "more", server will display more quotes if it has them available x
- when client types "more", client will lose a point out of the possible points they could have earned, if the player has reached the last quote, client will only be awarded one point when correct xx
- when client types "next" server will display a new quote from a different character x
- when client types "next" they lose two of the possible points they could have received x
- when client has 3 correct answers, server will send a "you win!" message on GUI and terminal x
- if client does not have 3 correct answers in one minute, server will send a "you lose" message -
- When game is over, have server update the leaderboard if applicable x
- When game is over, have server offer player to restart game or quit the program x 
- if client types name when game is over, game will restart. x

### How to run the program
Step 1: open two git bash windows making sure that one directed to the local TCP file for assignment 3 
and the other is connected to the server TCP file for assignment 3. if you plan to run this program locally,
make sure that both git bash windows are in the local TCP assignment 3 folder. 

Step 2: Once your git bash windows are in their proper directories, run the following command: 
"gradle build" on both windows. This will build and compile the project but will not run anything.

Step 3: Once the project is built on both windows, select your server git bash window and run the
following command: "gradle Server -Pport=(your selected port)". Once you run this command with 
your desired port, the server will run and will wait for a response from your client class.

Step 4: Once the server is running, go to your client git bash window and run the following command:
"gradle Client -Phost=(ip of server or localhost) -Pport=(your selected port from above)" This will then 
run the client and have it connect with the server. When it establishes a successful connection, the program
GUI will then pop up and run the game for the client. 

### UML
[](https://drive.google.com/drive/folders/1EY4ikTnqitbKN1sEoSXkCoYT3R7m21Mc?usp=share_link)
### Protocol explanation