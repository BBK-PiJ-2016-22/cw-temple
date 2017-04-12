package main.java.gui;

public interface Constants {
  double GAME_WIDTH_PROP = 0.78;       //Width of the Smain.java.game portion (prop of total)
  double GAME_HEIGHT_PROP = 1.0;      //Height of the Smain.java.game portion (prop of total)

  //Dimensions of the error pane (in pixels)
  int ERROR_WIDTH = 500;
  int ERROR_HEIGHT = 150;
  double INFO_SIZE = 0.5;    //How much of the screen should the info make up?

  //String ROOT = "/Users/keith/Courses/PiJ/coursework/cw-temple/code/src/Smain.java.images/";
  //replaced with:
    String ROOT = System.getProperty("user.dir")+"/src/Smain.java.images/";
}