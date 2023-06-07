import controller.CollageController;
import controller.CollageControllerImpl;
import controller.CollageGUIController;
import controller.CollageGUIControllerImpl;
import model.CollageModel;
import model.CollageModelImpl;
import view.CollageGUIView;
import view.CollageGUIViewImpl;
import view.CollageView;
import view.CollageViewImpl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This program allows the user to run the program by running the main method. After running
 * the program, the user can input their desired commands.
 */
public class Program {
  /**
   * Allows the user to specify what game they want to play and allows the user to play the game.
   *
   * @param args - user input specifying the game they want to play.
   */
  public static void main(String[] args) throws IOException {
    CollageModel collageModel = new CollageModelImpl();
    if (args.length > 0) {
      Readable rd = null;
      if (args[0].equals("-file")) {
        Path path = Paths.get(args[1]);
        if (!Files.exists(path)) {
          System.out.println(
                  "File does not exist. Try again.");
          return;
        }
        String read = Files.readAllLines(path).get(0);
        rd = new StringReader(read);

      } else if (args[0].equals("-text")) {
        rd = new InputStreamReader(System.in);
      } else {
        System.out.println("Invalid Input");
        return;
      }
      Appendable ap = System.out;
      CollageView view = new CollageViewImpl(collageModel, ap);
      CollageController controller = new CollageControllerImpl(collageModel, view, rd);
      controller.executeCommand();
    } else {
      CollageGUIController guiController = new CollageGUIControllerImpl(collageModel);
      CollageGUIView collageGUIView = new CollageGUIViewImpl(guiController);
    }


  }
}