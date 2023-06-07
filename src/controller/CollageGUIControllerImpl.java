package controller;

import model.CollageModel;
import model.Coord;
import model.Filter;
import model.ILayer;
import model.IPixels;
import model.Layer;
import model.Pixels;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * Represents the implementation of te GUIController. includes all
 *  * method implementations that can be performed through the graphical user interface.
 */
public class CollageGUIControllerImpl implements CollageGUIController {
  private CollageModel model;

  /**
   * Constructor for CollageGUIControllerImpl. initializes the model.
   * @param model represents the model.
   */
  public CollageGUIControllerImpl(CollageModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Invalid Parameters. Given model is null.");
    }
    this.model = model;
  }

  @Override
  public void newProject(int height, int width) throws IllegalArgumentException {
    if (height < 0 || width < 0) {
      throw new IllegalArgumentException("Invalid Height and Width Arguments.");
    }
    model.newProject(height, width);
  }

  @Override
  public void saveProject(File projectFileToSave) throws IOException {
    String projectContent = model.saveProject();
    try (FileWriter writeNewFile = new FileWriter(projectFileToSave)) {
      writeNewFile.write(projectContent);
    } catch (IOException e) {
      throw new IOException("Unable to write to file.");
    }
  }

  @Override
  public void saveImage(File imageFileToSave) throws IOException {
    ArrayList projectContent = model.saveImage();
    if (this.model.getFileExtension().equals("ppm")) {
      try {
        String writeContent = getPixelContent(projectContent,
                model.getHeight(), model.getWidth(), model.maxValue());
        FileWriter writeNewFile = new FileWriter(imageFileToSave);
        writeNewFile.write(writeContent);
        writeNewFile.close();
      } catch (IOException e) {
        throw new IOException("Unable to write to file.");
      }
    } else {
      saveOtherImage(projectContent, imageFileToSave.getName());
    }
  }

  @Override
  public void loadProject(File projectFile) throws IllegalStateException,
          IllegalArgumentException {
    LinkedHashMap[] projectContent = loadProjectContent(projectFile);
    model.loadProject(projectContent);
  }

  @Override
  public void addLayer(String layerName) throws IllegalStateException, IllegalArgumentException {
    model.addLayer(layerName);
  }

  @Override
  public void addImageToLayerPPM(File imageToAdd, String layerName, int xPos, int yPos, String
                                 fileExt)
          throws IllegalArgumentException {
    if (xPos < 0 || yPos < 0) {
      throw new IllegalArgumentException("Invalid x/y coords.");
    }
    int canvasHeight = model.getHeight();
    int canvasWidth = model.getWidth();
    int maxValue = model.maxValue();

    ArrayList imageContent =
            readImageFile(imageToAdd, canvasWidth, canvasHeight, maxValue);
    model.addImageToLayer(layerName, imageContent, xPos, yPos, "ppm");
  }


  @Override
  public void setFilterToLayer(String filterName, String layerName)
          throws IllegalArgumentException {
    model.setFilter(layerName, filterName);
  }

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
   * represents the load project file method. loads the project content.
   * @param file File to be laoded.
   * @throws IllegalArgumentException throws exception when file is not found.
   */
  private static LinkedHashMap[] loadProjectContent(File file) throws IllegalArgumentException {
    try (FileInputStream is = new FileInputStream(file)) {
      return loadProjectContent(is);
    } catch (IOException e) {
      throw new IllegalArgumentException("File " + file.getName() + " not found!");
    }
  }

  /**
   * represents the load project content method. loads the project content.
   * @param projectFileStream prject file stream.
   * @return LinkedHashMap[].
   * @throws IllegalArgumentException throws exception when file is not beginning with c1.
   */
  private static LinkedHashMap[] loadProjectContent(FileInputStream projectFileStream)
          throws IllegalArgumentException {
    LinkedHashMap<String, Integer> canvasContent = new LinkedHashMap<>();
    LinkedHashMap<String, ILayer> projectContentMap = new LinkedHashMap<>();
    Scanner sc = new Scanner(projectFileStream);
    String token;

    token = sc.next();
    if (!token.equals("C1")) {
      throw new IllegalArgumentException("Invalid Collage file: plain RAW file should begin with "
              + "C1");
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
  private static ArrayList readImageFile(File file, int canvasWidth
          , int canvasHeight, int maxValue) throws
          IllegalArgumentException {
    String fileName = file.getName();
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
    if (!(extension.equalsIgnoreCase("png") ||
            extension.equalsIgnoreCase("ppm") ||
            extension.equalsIgnoreCase("jpg") ||
            extension.equalsIgnoreCase("jpeg"))) {
      throw new IllegalArgumentException("File format not supported.");
    }
    try (FileInputStream is = new FileInputStream(file)) {
      return readImageFile(is, canvasWidth, canvasHeight, maxValue, extension);
    } catch (IOException e) {
      throw new IllegalArgumentException("File " + file.getName() + " not found!");
    }
  }

  /**
   * represents the readImage file method.
   * @param canvasWidth the desired width of the canvas.
   * @param canvasHeight the desired heigh of the canvas.
   * @param maxValue the maximum value.
   * @return ArrayList.
   * @throws IllegalArgumentException throws exception when file is not found.
   */
  private static ArrayList readImageFile(FileInputStream fileInputStream,
                                         int canvasWidth, int canvasHeight, int maxValue,
                                         String ext) throws IllegalArgumentException, IOException {
    if (ext.equalsIgnoreCase("ppm")) {
      return readPPMImageFile(fileInputStream, canvasWidth, canvasHeight, maxValue);
    }
    return readOtherImageFile(fileInputStream, canvasWidth, canvasHeight, maxValue);
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
  public static ArrayList readOtherImageFile(FileInputStream fileInputStream, int canvasWidth,
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
  public static String getPixelContent(ArrayList<ArrayList<IPixels>> pixelList, int height,
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
