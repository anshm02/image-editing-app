import java.io.IOException;
import java.io.InputStreamReader;

import controller.CollageController;
import controller.CollageControllerImpl;
import model.CollageModel;
import model.CollageModelImpl;
import view.CollageView;
import view.CollageViewImpl;

/**
 * This program allows the user to run the program by running the main method. After running
 * the program, the user can input their desired commands.
 */
public class CollageProgram {
  /**
   * Allows the user to specify what game they want to play and allows the user to play the game.
   * @param args - user input specifying the game they want to play.
   */
  public static void main(String[] args) throws IOException {
    CollageModel collageModel = new CollageModelImpl();
    Readable rd = new InputStreamReader(System.in);
    Appendable ap = System.out;
    CollageView view = new CollageViewImpl(collageModel, ap);
    CollageController controller = new CollageControllerImpl(collageModel, view, rd);
    controller.executeCommand();
      
  }
}