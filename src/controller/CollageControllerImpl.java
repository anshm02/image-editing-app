package controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import model.CollageModel;
import model.Coord;
import model.Filter;
import model.ILayer;
import model.IPixels;
import model.Layer;
import model.Pixels;
import view.CollageView;


/**
 * Represents the implementation of the controller for the image creation application. Allows the
 * user to perform adding new layers to their images and filtering images. The user can then save
 * their respective images and project so that they can be worked on in a different session.
 */
public class CollageControllerImpl implements CollageController {
  private CollageModel model;
  private CollageView view;
  private Readable rd;


  /**
   * Iniliazes fields.
   * @param model - contains instructions for the program to be run.
   * @param view - displays the status of the program to the user.
   * @param rd - reads input from the user.
   */
  public CollageControllerImpl(CollageModel model, CollageView view, Readable rd) {
    if (model == null || view == null || rd == null) {
      throw new IllegalArgumentException("Invalid Parameters. Given null input.");
    }

    this.model = model;
    this.view = view;
    this.rd = rd;

  }

  @Override
  public void executeCommand() throws IOException {
    // scans the input from the user
    Scanner s = new Scanner(this.rd);

    // transmit a welcome message
    renderMessageEndingWithNewLine("Hello! Thank you for using this image creation application. " +
            "\n");
    renderMessageEndingWithNewLine(listOfCommandsMessage());

    while (s.hasNext()) {
      try {
        String command = s.next();
        switch (command) {
          case "quit":
            return;
          case "new-project":
            newProjectInput(s);
            break;

          case "load-project":
            String pathToProjectFile = s.next();
            if (!Files.exists(Paths.get(pathToProjectFile))) {
              renderMessageEndingWithNewLine(
                      "File does not exist. Try again.");
              break;
            }

            try {
              LinkedHashMap[] projectContent = loadProjectFile(pathToProjectFile);
              model.loadProject(projectContent);
              renderMessageEndingWithNewLine("Project successfully loaded.");
            } catch (IllegalStateException ie) {
              renderMessageEndingWithNewLine("Currently working on a project. Quit this session " +
                      "and load another project.");
            }
            break;

          case "save-project":
            try {
              String filename = s.next();
              String projectContent = model.saveProject();
              try {
                FileWriter writeNewFile = new FileWriter(filename);
                writeNewFile.write(projectContent);
                writeNewFile.close();
              } catch (IOException e) {
                throw new IOException("Unable to write to file.");
              }
              renderMessageEndingWithNewLine("Your work has been successfully saved!");
              break;
            } catch (Exception e) {
              if (e instanceof IOException) {
                renderMessageEndingWithNewLine("Unable to save file.");
              }
              if (e instanceof IllegalStateException) {
                renderMessageEndingWithNewLine("Unable to save project as no project has been " +
                        "loaded.");
              }
            }
            break;

          case "add-layer":
            try {
              String layerName = s.next();
              model.addLayer(layerName);
              renderMessageEndingWithNewLine("Layer " + layerName + " has been successfully " +
                      "added.");
            } catch (Exception e) {
              if (e instanceof IllegalStateException) {
                renderMessageEndingWithNewLine("No Project has been loaded - load or create a " +
                        "project to begin working.");
              } else if (e instanceof IllegalArgumentException) {
                renderMessageEndingWithNewLine("Layer already exists.");
              }
            }
            break;

          case "add-image-to-layer":
            String layerN = s.next();
            String imageN = s.next();
            if (!Files.exists(Paths.get(imageN))) {
              renderMessageEndingWithNewLine("Image does not exist. Try again.");
            } else {
              try {

                int x = s.nextInt();
                int y = s.nextInt();
                String fileFormat = s.next();
                if (x < 0 || y < 0) {
                  throw new Exception("Invalid x/y coords.");
                }

                int canvasHeight = model.getHeight();
                int canvasWidth = model.getWidth();
                int maxValue = model.maxValue();
                ArrayList imageContent;

                if (fileFormat.equals("ppm")) {
                  imageContent = readImageFile(imageN, canvasWidth, canvasHeight,
                          maxValue);
                } else {
                  imageContent =
                          readOtherImageFile(new FileInputStream(imageN), canvasWidth,
                                  canvasHeight, maxValue);
                }

                model.addImageToLayer(layerN, imageContent, x, y, fileFormat);
                renderMessageEndingWithNewLine("Image has been successfully added to layer " +
                        layerN);
              } catch (Exception e) {
                System.out.println(e);
                renderMessageEndingWithNewLine(
                        "Invalid x/y coords. Try the command again."
                                + " x/y coords should be a positive integer");
              }
            }
            break;

          case "set-filter":
            try {
              String layer = s.next();
              String filterO = s.next();
              model.setFilter(layer, filterO);
              renderMessageEndingWithNewLine("Filter has been successfully set.");
            } catch (IllegalArgumentException ie) {
              renderMessageEndingWithNewLine("Invalid input. Layer or filter option does not " +
                      "exist.");
            }
            break;
          case "save-image":
            try {
              String fileName = s.next();
              ArrayList projectContent = model.saveImage();
              if (this.model.getFileExtension().equals("ppm")) {
                try {
                  String writeContent = getPixelContent(projectContent,
                          model.getHeight(), model.getWidth(), model.maxValue());
                  FileWriter writeNewFile = new FileWriter(fileName);
                  writeNewFile.write(writeContent);
                  writeNewFile.close();
                } catch (IOException e) {
                  throw new IOException("Unable to write to file.");
                }
              } else {
                saveOtherImage(projectContent, fileName);
              }

              renderMessageEndingWithNewLine("Image has been successfully saved.");
            } catch (IOException ie) {
              renderMessageEndingWithNewLine("Unable to write to file.");
            }
            break;
          default:
            renderMessageEndingWithNewLine("Command does not exist. Try Again.");
            break;

        }
      } catch (Exception e) {
        String exceptionMessage = e.getMessage();
        if (exceptionMessage.equalsIgnoreCase("Quit")) {
          return;
        }
      }
    }
  }


  /**
   * Renders message ending with a new line.
   *
   * @param message message.
   * @throws IOException when unable to render message.
   */
  private void renderMessageEndingWithNewLine(String message) throws IOException {
    this.view.transmitMessage(message + System.lineSeparator());
  }

  /**
   * Used to display the list of commands as a message to the user.
   * @return a string with the appropriate commands for this program listed.
   */
  private String listOfCommandsMessage() {
    return "The following is a list of commands that are supported by this program: \n" +
            "- To create a new project (new-project canvas-height canvas-width) \n" +
            "- To load an existing project (load-project 'path-to-project-file') \n" +
            "- To save a project (save-project) \n" +
            "- To add a new layer to the project (add-layer layer-name) \n" +
            "- To filter a specific layer (set-filter layer-name filter-option) \n" +
            "  Filter Option can be one of: " +
            "   - normal \n" +
            "   - red-component: only uses the red portion of the RGB\n" +
            "   - blue-component: only uses the blue portion of the RGB\n" +
            "   - green-component: only uses the green portion of the RGB\n" +
            "   - brighten-value: adds brightness according to the value of each pixel \n" +
            "   - brighten-luma: adds brightness according to the luma of each pixel \n" +
            "   - brighten-intensity: adds brightness according to the intensity of each pixel \n" +
            "   - darken-value: darkens the image according to the value of each pixel \n" +
            "   - darken-luma: darkens the image according to the luma of each pixel \n" +
            "   - darken-intensity: darkens the image according to the intensity of each pixel \n";

  }

  /**
   * Creates a new project and checks if the canvas height and width are valid.
   *
   * @param s Scanner object.
   * @throws Exception on quit.
   */
  private void newProjectInput(Scanner s) throws Exception {
    try {
      int height = s.nextInt();
      int width = s.nextInt();

      if (height < 0 || width < 0) {
        renderMessageEndingWithNewLine(
                "Invalid height/width. Try the command again."
                        + " height/width should be a positive integer");
      } else {
        model.newProject(height, width);
        renderMessageEndingWithNewLine("New Project Successfully Created!");

      }
    } catch (Exception e) {
      if (e instanceof NoSuchElementException
              || e instanceof InputMismatchException
              || e instanceof IllegalArgumentException) {
        renderMessageEndingWithNewLine(
                "Invalid height/width. Try the command again."
                        + " height/width should be a positive integer");
      } else {
        throw e;
      }
    }
  }

  /**
   * Stores the layer names, pixel contnet and content of the project after reading from a collage
   * file.
   * @param filename - name of the collage file.
   * @return - List of LinkedHashMaps which contains layers, pixel content and project height and
   *         width from the loaded project.
   * @throws IllegalArgumentException - the file has not been found, or if the collage file is
   *                                  invalid.
   */
  private LinkedHashMap[] loadProjectFile(String filename) throws IllegalArgumentException {
    LinkedHashMap<String, Integer> canvasContent = new LinkedHashMap<>();
    LinkedHashMap<String, ILayer> projectContentMap = new LinkedHashMap<>();
    Scanner sc;

    try {
      sc = new Scanner(new FileInputStream(filename));
    }
    catch (FileNotFoundException e) {
      throw new IllegalArgumentException("File " + filename + " not found!");
    }

    String token;

    token = sc.next();
    if (!token.equals("C1")) {
      throw new IllegalArgumentException("Invalid Collage file: plain RAW file should begin with " +
              "C1");
    }

    int width = sc.nextInt();
    int height = sc.nextInt();
    int maxValue = sc.nextInt();

    canvasContent.put("width", width);
    canvasContent.put("height", height);
    canvasContent.put("maxValue", maxValue);


    while (sc.hasNext()) {
      String layerName = sc.next();
      Filter filterOption = stringToFilterOption(sc.next());
      ArrayList pixels = new ArrayList();

      for (int h = 0; h < height; h++) {
        ArrayList pixelList = new ArrayList();
        for (int w = 0; w < width; w++) {
          if (!sc.hasNextInt()) {
            break;
          }
          int r = sc.nextInt();
          int g = sc.nextInt();
          int b = sc.nextInt();
          int a = sc.nextInt();
          pixelList.add(new Pixels(new Coord(h, w), r, g, b, a));
        }
        pixels.add(pixelList);
      }

      projectContentMap.put(layerName, new Layer(filterOption, pixels));
    }

    LinkedHashMap[] projectContent = {canvasContent, projectContentMap};

    return projectContent;

  }


  /**
   * Reads the image PPM.
   * @param filename - name of file that is being parsed.
   * @param canvasWidth - project width.
   * @param canvasHeight - project height.
   * @param maxValue - project max value.
   * @return - An ArrayList containing the image's content.
   * @throws IllegalArgumentException - if the file is not of proper format.
   */
  private ArrayList readImageFile(String filename, int canvasWidth, int canvasHeight, int maxValue)
          throws IllegalArgumentException {
    Scanner sc;

    try {
      sc = new Scanner(new FileInputStream(filename));
    }
    catch (FileNotFoundException e) {
      throw new IllegalArgumentException("File " + filename + " not found!");
    }

    String token;

    token = sc.next();
    if (!token.equals("P3")) {
      throw new IllegalArgumentException("Invalid Collage file: plain RAW file should begin with "
              + "P3");
    }

    int width = sc.nextInt();
    int height =  sc.nextInt();
    sc.nextInt();
    ArrayList pixels = new ArrayList();



    for (int h = 0; h < canvasHeight; h++) {
      ArrayList pixelList = new ArrayList();
      for (int w = 0; w < canvasWidth; w++) {

        if (w < width && h < height) {
          int r = sc.nextInt();
          int g = sc.nextInt();
          int b = sc.nextInt();
          pixelList.add(new Pixels(new Coord(h,w), r, g, b, maxValue));
        } else {
          pixelList.add(new Pixels(new Coord(h,w), maxValue, maxValue, maxValue, maxValue));
        }
      }
      pixels.add(pixelList);
    }
    return pixels;
  }

  /**
   * Saves an image file of a formath that is different from a ppm file.
   * @param projectContent - ArrayList of pixels representing the content of the project.
   * @param fileName - file name for the image to be saved on.
   * @throws IOException - if unable to write to file.
   */
  private void saveOtherImage(ArrayList<ArrayList<Pixels>> projectContent,
                              String fileName) throws IOException {

    int width = model.getWidth();
    int height = model.getHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    // Set the color of each pixel in the image using its RGB value
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int r = projectContent.get(x).get(y).getRedComponent();
        int g = projectContent.get(x).get(y).getGreenComponent();
        int b = projectContent.get(x).get(y).getBlueComponent();
        int rgb = (r << 16) | (g << 8) | b;
        image.setRGB(x, y, rgb);
      }
    }

    // Save the image to a file
    File file = new File(fileName);
    try {
      ImageIO.write(image, model.getFileExtension(), file);
    } catch (IOException e) {
      throw new IOException("Unable to write to file");
    }

  }

  /**
   * represents the StringToFiterOption method.
   * @param filterCommand string for filter command.
   * @return Filter.
   */
  private static Filter stringToFilterOption(String filterCommand) {
    for (Filter fil : Filter.values()) {
      if (fil.getValue().equals(filterCommand)) {
        return fil;
      }
    }
    throw new IllegalArgumentException("Filter does not exist");
  }

  /**
   * represents the readImage file method.
   * @param canvasWidth the desired width of the canvas.
   * @param canvasHeight the desired heigh of the canvas.
   * @param maxValue the maximum value.
   * @return ArrayList.
   * @throws IllegalArgumentException throws exception when file is not found.
   */
  private static ArrayList readPPMImageFile(FileInputStream fileInputStream,
                                            int canvasWidth, int canvasHeight, int maxValue) throws
          IllegalArgumentException {
    try (Scanner sc = new Scanner(fileInputStream)) {
      String token;
      token = sc.next();
      if (!token.equals("P3")) {
        throw new IllegalArgumentException("Invalid Collage file:" +
                " plain RAW file should begin with " + "P3");
      }
      int width = sc.nextInt();
      int height = sc.nextInt();
      sc.nextInt();
      ArrayList pixels = new ArrayList();

      for (int h = 0; h < canvasHeight; h++) {
        ArrayList pixelList = new ArrayList();
        for (int w = 0; w < canvasWidth; w++) {

          if (w < width && h < height) {
            int r = sc.nextInt();
            int g = sc.nextInt();
            int b = sc.nextInt();
            pixelList.add(new Pixels(new Coord(h, w), r, g, b, maxValue));
          } else {
            pixelList.add(new Pixels(new Coord(h, w), maxValue, maxValue, maxValue, maxValue));
          }
        }
        pixels.add(pixelList);
      }
      return pixels;
    }
  }

  /**
   * represents the readImage file method.
   * @param canvasWidth the desired width of the canvas.
   * @param canvasHeight the desired height of the canvas.
   * @param maxValue the maximum value.
   * @return ArrayList.
   * @throws IllegalArgumentException throws exception when file is not found.
   */
  private static ArrayList readOtherImageFile(FileInputStream fileInputStream, int canvasWidth,
                                             int canvasHeight, int maxValue) throws
          IllegalArgumentException, IOException {
    BufferedImage image = ImageIO.read(fileInputStream);
    // Get the width and height of the image
    int width = image.getWidth();
    int height = image.getHeight();
    ArrayList pixels = new ArrayList();

    for (int h = 0; h < canvasHeight; h++) {
      ArrayList pixelList = new ArrayList();
      for (int w = 0; w < canvasWidth; w++) {

        if (w < width && h < height) {
          int rgb = image.getRGB(h, w);
          Color color = new Color(rgb);
          int r = color.getRed();
          int g = color.getGreen();
          int b = color.getBlue();
          pixelList.add(new Pixels(new Coord(h, w), r, g, b, maxValue));
        } else {
          pixelList.add(new Pixels(new Coord(h, w), maxValue, maxValue, maxValue, maxValue));
        }
      }
      pixels.add(pixelList);
    }
    return pixels;
  }

  /**
   * Write a string formatted ppm image file.
   * @param pixelList - pixel content.
   * @param height - height of the project.
   * @param width - width of the project.
   * @param maxValue - max value of the project.
   * @return - a String in ppm format with all the rgb values.
   */
  private static String getPixelContent(ArrayList<ArrayList<IPixels>> pixelList, int height,
                                       int width, int maxValue) {
    String pixelListToString = "";
    pixelListToString += "P3\n";
    pixelListToString += width + " " + height + "\n";
    pixelListToString += maxValue + "\n";
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        pixelListToString += pixelList.get(h).get(w).pixelToStringPPM();
      }
    }

    return pixelListToString;
  }
}









