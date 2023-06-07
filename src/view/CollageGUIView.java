package view;

import java.io.IOException;

/**
 * This interface represents the Collage Graphical User Interface View.
 * Allows the user to see a graphical
 * representation of the collage program.
 */
public interface CollageGUIView {

  /**
   * The method which renders the graphical user interface view.
   * @param message string message.
   * @throws IOException throws IO exception.
   */
  void renderMessage(String message) throws IOException;
}
