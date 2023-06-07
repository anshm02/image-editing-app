package view;

import java.io.IOException;

import model.CollageModel;

/**
 * Displays the messages to the user so that the user can better understand the status of the
 * model and interact with it better. Displays welcome and error messages to the user.
 */
public class CollageViewImpl implements CollageView {
  private CollageModel collageModel;
  private Appendable ap;

  /**
   * Initializes fields.
   * @param collageModel - model of the image creation program.
   * @param ap - used to store all the messages transmitted from the model.
   * @throws IllegalArgumentException - if any provided fields are null.
   */
  public CollageViewImpl(CollageModel collageModel, Appendable ap) throws IllegalArgumentException {
    if (collageModel != null && ap != null) {
      this.collageModel = collageModel;
      this.ap = ap;
    } else {
      throw new IllegalArgumentException("Invalid null input for the model state or appendable");
    }
  }

  @Override
  public void transmitMessage(String message) throws IOException {
    try {
      ap.append(message + "\n");
    } catch (IOException ex) {
      throw new IOException("Transmission of the message to the data output fails.");
    }
  }
}
