package main.java.main;

import main.java.game.GameState;

import java.util.Optional;

/**
 * Run this program to see a demonstration of the GUI interface
 */
public class GUImain {
  /**
   * The Smain.java.main program.
   */
  public static void main(String[] args) {
    Optional<Long> seed = Utilities.parseSeedArgs(args);
    GameState.runNewGame((seed.isPresent() ? seed.get() : 0), true);
  }
}
