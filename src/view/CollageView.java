package view;

import java.io.IOException;

/**
 * This interface displays the state of the model to the user. It displays a welcoming message
 * to the user and any errors that occur.
 */
public interface CollageView {

  /**
   * Renders a message to the data output.
   * @param message - message to be outputted to the user.
   */
  void transmitMessage(String message) throws IOException;

}
