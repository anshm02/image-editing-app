package controller;

import java.io.IOException;

/**
 * This interface allows a user to interact with the image manipulation application by inputting
 * a set of commands which correspond to different operations. This interface is strictly used
 * for the text based user interface version of the collage application.
 */
public interface CollageController {
  /**
   * Allows the user to create new images.
   */
  void executeCommand() throws IOException;

}
