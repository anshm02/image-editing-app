package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Contains the program instructions. Generates instructions that manipulate the created image.
 * Includes functionality which allow the creation of images and projects.
 */
public class CollageModelImpl implements CollageModel {

  private boolean projectLoaded;
  private LinkedHashMap<String, ILayer> layers;
  private int maxValue;
  private int width;
  private int height;
  private String fileExtension;


  /**
   * Initializes fields that represent different components of the image creation application.
   * Fields are initialized to default value respective to their representation.
   */
  public CollageModelImpl() {
    this.layers = new LinkedHashMap<String, ILayer>();
    this.projectLoaded = false;
    this.maxValue = 255;
    this.height = 0;
    this.width = 0;
    this.fileExtension = "ppm";

  }

  @Override
  public void newProject(int height, int width) throws IllegalArgumentException {
    if (height < 0 || width < 0) {
      throw new IllegalArgumentException("Height and width must be positive integer.");
    }

    this.height = height;
    this.width = width;
    this.layers.put("default-background", new Layer().addNewLayer(Filter.NORMAL, this.height,
            this.width, this.maxValue, this.maxValue));
    this.projectLoaded = true;

  }

  @Override
  public void loadProject(LinkedHashMap[] projectContent) throws IllegalStateException,
          IllegalArgumentException {

    if (projectLoaded) {
      throw new IllegalStateException("Currently working on a project. Quit this session and " +
              "load another project.");
    }
    LinkedHashMap<String, Integer> canvasContent = projectContent[0];
    LinkedHashMap<String, ILayer> layerContent = projectContent[1];

    this.height = canvasContent.get("height");
    this.width = canvasContent.get("width");
    this.maxValue = canvasContent.get("maxValue");

    for (Object o : layerContent.keySet()) {
      this.layers.put((String)o, layerContent.get(o));
    }

    this.projectLoaded = true;
  }

  @Override
  public String saveProject() throws IllegalArgumentException, IllegalStateException {

    if (!projectLoaded) {
      throw new IllegalStateException("No Project has been loaded");
    }

    if (!this.layers.containsKey("default-background")) {
      throw new IllegalArgumentException("Invalid Project. Unable to save");
    }

    String projectContent = "";
    projectContent += "C1\n";
    projectContent += width + " " + height + "\n";
    projectContent += maxValue + "\n";

    for (String layers : this.layers.keySet()) {
      projectContent += layers + " ";
      projectContent += this.layers.get(layers).getFilterOption().getValue() + "\n";
      projectContent += getPixelContent(this.layers.get(layers).getPixels());
    }

    return projectContent;

  }

  @Override
  public void addLayer(String layerName) {

    if (!projectLoaded) {
      throw new IllegalStateException("No Project has been loaded - load or create a project " +
              "to begin working.");
    }

    if (this.layers.containsKey(layerName)) {
      throw new IllegalArgumentException("Layer already exisits");
    }

    this.layers.put(layerName, new Layer().addNewLayer(Filter.NORMAL, this.height,
            this.width, this.maxValue, 0));

  }

  @Override
  public void addImageToLayer(String layerName, ArrayList<ArrayList<IPixels>> imageContent,
                              int xPos, int yPos, String fileForm) {

    if (!this.layers.containsKey(layerName)) {
      throw new IllegalArgumentException("Layer does not exists. Try Again");
    }

    this.fileExtension = fileForm;

    ArrayList backgroundLayer = this.layers.get(layerName).getPixels();
    this.layers.get(layerName).changePixels(computeTransparency(imageContent, backgroundLayer,
            xPos, yPos));

  }

  @Override
  public void setFilter(String layerName, String filterOption) throws IllegalArgumentException {

    if (!this.layers.containsKey(layerName)) {
      throw new IllegalArgumentException("Layer does not exist.");
    }

    if (filterOption.equals("normal") || filterOption.equals("red-component") ||
            filterOption.equals("green-component") || filterOption.equals("blue-component") ||
            filterOption.equals("brighten-value") || filterOption.equals("darken-value") ||
            filterOption.equals("brighten-intensity") || filterOption.equals("darken-intensity")
            || filterOption.equals("brighten-luma") || filterOption.equals("darken-luma")
            || filterOption.equals("blending-difference") ||
            filterOption.equals("blending-multiply") || filterOption.equals("blending-screen")) {


      Filter filter = stringToFilterOption(filterOption);
      this.layers.get(layerName).changeFilterOption(filter);

    } else {
      throw new IllegalArgumentException("Filter does not exist");
    }

  }

  /**
   * Makes a call to the respective function based on the filter operation provided by the user.
   * @param pixelContentList - an array list of the current layer of pixels.
   * @param backgroundPixels - an array list of the background layer of pixels.
   * @param filterOption - user specified filter operation.
   * @return - an array list of the computed layer after the filter operation has been applied to
   *         the layer.
   */
  private ArrayList filterOptionImpl(ArrayList<ArrayList<IPixels>> pixelContentList,
                                  ArrayList<ArrayList<IPixels>> backgroundPixels,
                                      String filterOption) {
    switch (filterOption) {
      case "normal":
        break;
      case "red-component":
        return filterComponent(pixelContentList, "red");
      case "green-component":
        return filterComponent(pixelContentList, "green");
      case "blue-component":
        return filterComponent(pixelContentList, "blue");
      case "brighten-value":
        return changeComponentValue(pixelContentList, "brighten");
      case "darken-value":
        return changeComponentValue(pixelContentList, "darken");
      case "brighten-intensity":
        return changeComponentIntensity(pixelContentList, "brighten");
      case "darken-intensity":
        return changeComponentIntensity(pixelContentList, "darken");
      case "brighten-luma":
        return changeComponentLuma(pixelContentList, "brighten");
      case "darken-luma":
        return changeComponentLuma(pixelContentList, "darken");
      case "blending-difference":
        return blendingFilterOption(pixelContentList, backgroundPixels);
      case "blending-screen":
        return blendingOption(pixelContentList, backgroundPixels, "screen");
      case "blending-multiply":
        return blendingOption(pixelContentList, backgroundPixels, "multiply");
      default:
        throw new IllegalArgumentException("filter option does not exist");
    }

    return pixelContentList;

  }

  @Override
  public ArrayList saveImage() {


    ArrayList<ArrayList<IPixels>> backgroundLayer = this.layers.get("default-background").
            getPixels();

    ArrayList<ArrayList<IPixels>> imageToPPM = new ArrayList<>();

    for (String layers : this.layers.keySet()) {

      if (layers.equals("default-background")) {
        continue;

      } else {
        ArrayList<ArrayList<IPixels>> computedLayer = computeTransparency(this.layers.get(layers)
                .getPixels(), backgroundLayer, 0, 0);
        ArrayList<ArrayList<IPixels>> filteredLayer = filterOptionImpl(computedLayer,
                backgroundLayer, this.layers.get(layers).getFilterOption().getValue());
        imageToPPM = writeToImage(filteredLayer);
        backgroundLayer = this.layers.get(layers).getPixels();
      }

    }

    return imageToPPM;

  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int maxValue() {
    return this.maxValue;
  }

  @Override
  public String getFileExtension() {
    return this.fileExtension;
  }

  /**
   * Creates a string of the pixel components formatted to be outputted in a collage project format.
   * @param pixelList - array list of pixels representing a layers pixel content.
   * @return - a string which is correctly formatted to be displayed in a project.
   */
  private String getPixelContent(ArrayList<ArrayList<IPixels>> pixelList) {
    String pixelListToString = "";
    for (int h = 0; h < this.height; h++) {
      for (int w = 0; w < this.width; w++) {
        pixelListToString += pixelList.get(h).get(w).pixelToString();
      }
    }

    return pixelListToString;
  }

  /**
   * Creates a string of the pixel components formatted to be outputted in a PPM format.
   * @param pixelList - array list of pixels representing a layers pixel content.
   * @return - a string which is correctly formatted to be displayed in a PPM file.
   */
  private String getPixelContentPPM(ArrayList<ArrayList<IPixels>> pixelList) {
    String pixelListToString = "";
    for (int h = 0; h < this.height; h++) {
      for (int w = 0; w < this.width; w++) {
        pixelListToString += pixelList.get(h).get(w).pixelToStringPPM();
      }
      pixelListToString += " \n";
    }

    return pixelListToString;
  }

  /**
   * Converts a layers content from (r, g, b, a) to (r, g, b).
   * @param imageData - array list of pixels representing a layers pixel content.
   * @return an arraylist of the image's content after it has been converted appropriately.
   *
   */
  private ArrayList writeToImage(ArrayList<ArrayList<IPixels>> imageData) {

    for (int h = 0; h < this.height; h++) {
      for (int w = 0; w < this.width; w++) {
        int redComponent = imageData.get(h).get(w).getRedComponent();
        int greenComponent = imageData.get(h).get(w).getGreenComponent();
        int blueComponent = imageData.get(h).get(w).getBlueComponent();
        int alphaComponent = imageData.get(h).get(w).getAlphaComponent();

        int newRedComponent = redComponent * alphaComponent / this.maxValue;
        int newGreenComponent = greenComponent * alphaComponent / this.maxValue;
        int newBlueComponent = blueComponent * alphaComponent / this.maxValue;

        imageData.get(h).get(w).changeRedComponent(newRedComponent);
        imageData.get(h).get(w).changeGreenComponent(newGreenComponent);
        imageData.get(h).get(w).changeBlueComponent(newBlueComponent);
      }
    }

    return imageData;
  }


  /**
   * Computes a new layer after applying transparency to the current layer using the background
   * layer.
   * @param currentLayer - arraylist storing the pixel content of the current layer's pixels.
   * @param backgroundLayer - arraylist storing the pixel content of the background layer's pixels.
   * @param xPos - places the current layer top-left corner at the x position.
   * @param yPos - places the current layer top-left corner at the y position.
   * @return - an ArrayList of the new layer after applying transparency to the current layer using
   *         the background layer.
   */
  private ArrayList computeTransparency(ArrayList<ArrayList<IPixels>> currentLayer,
                                     ArrayList<ArrayList<IPixels>> backgroundLayer, int xPos,
                                     int yPos) {

    ArrayList pixels = new ArrayList();
    for (int h = 0; h < this.height; h++) {
      ArrayList pixelList = new ArrayList();
      for (int w = 0; w < this.width; w++) {
        if (this.width >= xPos && this.height > yPos) {
          double currentComponentR = currentLayer.get(h).get(w).getRedComponent();
          double currentComponentG = currentLayer.get(h).get(w).getGreenComponent();
          double currentComponentB = currentLayer.get(h).get(w).getBlueComponent();
          double currentComponentA = currentLayer.get(h).get(w).getAlphaComponent();

          double backgroundComponentR = backgroundLayer.get(h).get(w).getRedComponent();
          double backgroundComponentG = backgroundLayer.get(h).get(w).getGreenComponent();
          double backgroundComponentB = backgroundLayer.get(h).get(w).getBlueComponent();
          double backgroundComponentA = backgroundLayer.get(h).get(w).getAlphaComponent();

          double alphaComponentHelper = (currentComponentA / 255 + backgroundComponentA / 255 *
                  (1 - (currentComponentA / 255)));
          int newComponentA = (int) (alphaComponentHelper * 255);
          int newComponentR = (int) ((currentComponentA / 255 * currentComponentR +
                  backgroundComponentR * (backgroundComponentA / 255) * (1 - currentComponentA
                          / 255))
                  * (1 / alphaComponentHelper));
          int newComponentG = (int) ((currentComponentA / 255 * currentComponentG +
                  backgroundComponentG * (backgroundComponentA / 255) * (1 - currentComponentA /
                          255)) * (1 / alphaComponentHelper));
          int newComponentB = (int) ((currentComponentA / 255 * currentComponentB +
                  backgroundComponentB * (backgroundComponentA / 255) * (1 - currentComponentA
                          / 255)) * (1 / alphaComponentHelper));

          pixelList.add(new Pixels(new Coord(h, w), newComponentR, newComponentG, newComponentB,
                  newComponentA));
        } else {
          int backgroundComponentR = backgroundLayer.get(h).get(w).getRedComponent();
          int backgroundComponentG = backgroundLayer.get(h).get(w).getGreenComponent();
          int backgroundComponentB = backgroundLayer.get(h).get(w).getBlueComponent();
          int backgroundComponentA = backgroundLayer.get(h).get(w).getAlphaComponent();

          pixelList.add(new Pixels(new Coord(h, w), backgroundComponentR, backgroundComponentG,
                  backgroundComponentB, backgroundComponentA));
        }
      }
      pixels.add(pixelList);
    }

    return pixels;
  }

  /**
   * Applies changes to pixel components based upon the specified filter option and operation.
   * @param redComponent - red component of the pixel.
   * @param greenComponent - green component of the pixel.
   * @param blueComponent - blue component of the pixel.
   * @param maxComponent - value to be added or subtracted to each component r, g, b components.
   *                     This value is computed based upon the type of filter operation being used.
   * @param operation - used specified filter operation. Will contain either, "brighten" or
   *                  "darken" based upon user preferences.
   * @return a list of integers containing the revised r, g, b component.
   */
  private int[] changeComponent(int redComponent, int greenComponent, int blueComponent,
                                    int maxComponent, String operation) {
    if (operation.equals("brighten")) {
      if (redComponent + maxComponent > this.maxValue) {
        redComponent = this.maxValue;
      } else {
        redComponent += maxComponent;
      }

      if (greenComponent + maxComponent > this.maxValue) {
        greenComponent = this.maxValue;
      } else {
        greenComponent += maxComponent;
      }

      if (blueComponent + maxComponent > this.maxValue) {
        blueComponent = this.maxValue;
      } else {
        blueComponent += maxComponent;
      }
    }

    if (operation.equals("darken")) {
      if (redComponent - maxComponent < 0) {
        redComponent = 0;
      } else {
        redComponent -= maxComponent;
      }

      if (greenComponent - maxComponent < 0) {
        greenComponent = 0;
      } else {
        greenComponent -= maxComponent;
      }

      if (blueComponent - maxComponent < 0) {
        blueComponent = 0;
      } else {
        blueComponent -= maxComponent;
      }
    }

    int[] components = {redComponent, greenComponent, blueComponent};
    return components;

  }


  /**
   * Changes the pixel components by intensity. Computes the average of the r, g, b component's
   * values and adds the average of the computed values to each component.
   * @param pixelData - array list of the pixels containing the component data.
   * @param operation - used specified filter operation. Will contain either, "brighten" or
   *                  "darken" based upon user preferences.
   * @return an arraylist of the components after the filter intensity has been applied.
   */
  private ArrayList changeComponentIntensity(ArrayList<ArrayList<IPixels>> pixelData,
                                                                 String operation) {
    for (int h = 0; h < this.height; h++) {
      for (int w = 0; w < this.width; w++) {
        int redComponent = pixelData.get(h).get(w).getRedComponent();
        int greenComponent = pixelData.get(h).get(w).getGreenComponent();
        int blueComponent = pixelData.get(h).get(w).getBlueComponent();

        int maxComponent = (redComponent + greenComponent + blueComponent) / 3;

        int[] newValues = changeComponent(redComponent, greenComponent, blueComponent,
                maxComponent, operation);

        pixelData.get(h).get(w).changeRedComponent(newValues[0]);
        pixelData.get(h).get(w).changeBlueComponent(newValues[1]);
        pixelData.get(h).get(w).changeGreenComponent(newValues[2]);
      }
    }

    return pixelData;

  }


  /**
   * Changes the pixel components by intensity. Computes the maximum of the r, g, b component's
   * values and adds the maximum of the computed values to each component.
   * @param pixelData - array list of the pixels containing the component data.
   * @param operation - used specified filter operation. Will contain either, "brighten" or
   *                  "darken" based upon user preferences.
   * @return an arraylist of the components after the filter component has been applied.
   */
  private ArrayList changeComponentValue(ArrayList<ArrayList<IPixels>> pixelData,
                                         String operation) {
    for (int h = 0; h < this.height; h++) {
      for (int w = 0; w < this.width; w++) {
        int redComponent = pixelData.get(h).get(w).getRedComponent();
        int greenComponent = pixelData.get(h).get(w).getGreenComponent();
        int blueComponent = pixelData.get(h).get(w).getBlueComponent();

        int maxVal = Math.max(redComponent, greenComponent);
        int maxComponent = Math.max(maxVal, blueComponent);

        int[] newValues = changeComponent(redComponent, greenComponent, blueComponent,
                maxComponent, operation);

        pixelData.get(h).get(w).changeRedComponent(newValues[0]);
        pixelData.get(h).get(w).changeBlueComponent(newValues[1]);
        pixelData.get(h).get(w).changeGreenComponent(newValues[2]);
      }
    }

    return pixelData;
  }

  /**
   * Changes the pixel components by luma. Computes the luma value of the r, g, b component's
   * values and adds the luma value of the computed values to each component.
   * @param pixelData - array list of the pixels containing the component data.
   * @param operation - used specified filter operation. Will contain either, "brighten" or
   *                  "darken" based upon user preferences.
   * @return an arraylist of the components after the filter luma has been applied.
   */
  private ArrayList changeComponentLuma(ArrayList<ArrayList<IPixels>> pixelData, String operation) {
    for (int h = 0; h < this.height; h++) {
      for (int w = 0; w < this.width; w++) {
        int redComponent = pixelData.get(h).get(w).getRedComponent();
        int greenComponent = pixelData.get(h).get(w).getGreenComponent();
        int blueComponent = pixelData.get(h).get(w).getBlueComponent();

        int maxComponent = (int) (0.2126 * redComponent + 0.7152 * greenComponent + 0.0722 *
                blueComponent);

        int[] newValues = changeComponent(redComponent, greenComponent, blueComponent,
                maxComponent, operation);

        pixelData.get(h).get(w).changeRedComponent(newValues[0]);
        pixelData.get(h).get(w).changeBlueComponent(newValues[1]);
        pixelData.get(h).get(w).changeGreenComponent(newValues[2]);
      }
    }

    return pixelData;

  }

  /**
   * Chnages the pixel components to only contain the pixel component specified by the user.
   * @param pixelData - array list of the pixels containing the component data.
   * @param color - r, g, b color that the user wants to keep. All other colors will be filtered
   *              out.
   * @return an arraylist of the components after the filter has been applied.
   */
  private ArrayList filterComponent(ArrayList<ArrayList<IPixels>> pixelData, String color) {

    for (int h = 0; h < this.height; h++) {
      for (int w = 0; w < this.width; w++) {
        if (color.equals("red")) {
          pixelData.get(h).get(w).changeGreenComponent(0);
          pixelData.get(h).get(w).changeBlueComponent(0);
        } else if (color.equals("blue")) {
          pixelData.get(h).get(w).changeRedComponent(0);
          pixelData.get(h).get(w).changeGreenComponent(0);
        } else if (color.equals("green")) {
          pixelData.get(h).get(w).changeRedComponent(0);
          pixelData.get(h).get(w).changeBlueComponent(0);
        }
      }
    }

    return pixelData;
  }

  /**
   * Filters the components based upon the currentLayer and backgroundLayer. Find the absolute
   * value of the difference between the current layer and background layer and computes
   * the filtered value.
   * @param currentLayer - ArrayList of pixels of the current layer.
   * @param backgroundLayer - ArrayList of pixels of the background layer. The layer below the
   *                        current layer.
   * @return an arraylist of the components after the blending filter has been applied.
   */
  private ArrayList blendingFilterOption(ArrayList<ArrayList<IPixels>> currentLayer,
                                        ArrayList<ArrayList<IPixels>> backgroundLayer) {

    for (int h = 0; h < this.height; h++) {
      for (int w = 0; w < this.width; w++) {
        int currentComponentR = currentLayer.get(h).get(w).getRedComponent();
        int currentComponentG = currentLayer.get(h).get(w).getGreenComponent();
        int currentComponentB = currentLayer.get(h).get(w).getBlueComponent();

        int backgroundComponentR = backgroundLayer.get(h).get(w).getRedComponent();
        int backgroundComponentG = backgroundLayer.get(h).get(w).getGreenComponent();
        int backgroundComponentB = backgroundLayer.get(h).get(w).getBlueComponent();

        int newComponentR = Math.abs(currentComponentR - backgroundComponentR);
        int newComponentG = Math.abs(currentComponentG - backgroundComponentG);
        int newComponentB = Math.abs(currentComponentB - backgroundComponentB);

        currentLayer.get(h).get(w).changeRedComponent(newComponentR);
        currentLayer.get(h).get(w).changeRedComponent(newComponentG);
        currentLayer.get(h).get(w).changeRedComponent(newComponentB);

      }
    }

    return currentLayer;
  }

  /**
   * Filters the components based upon the currentLayer and backgroundLayer. Converts the r, g, b
   * value to HSL representation and computes a new layer after the specified blending has been
   * applied. HSL representation is then converted back to r, g, b value so that it passed back
   * into the project.
   * @param currentLayer - ArrayList of pixels of the current layer.
   * @param backgroundLayer - ArrayList of pixels of the background layer. The layer below the
   *                        current layer.
   * @param operation - user specified operation. Should be one of "multiply" or "screen".
   * @return an arraylist of the components after the blending filter has been applied.
   */
  private ArrayList blendingOption(ArrayList<ArrayList<IPixels>> currentLayer,
                                           ArrayList<ArrayList<IPixels>> backgroundLayer,
                                           String operation) {
    for (int h = 0; h < this.height; h++) {
      for (int w = 0; w < this.width; w++) {
        double currentComponentR = currentLayer.get(h).get(w).getRedComponent();
        double currentComponentG = currentLayer.get(h).get(w).getGreenComponent();
        double currentComponentB = currentLayer.get(h).get(w).getBlueComponent();

        double backgroundComponentR = backgroundLayer.get(h).get(w).getRedComponent();
        double backgroundComponentG = backgroundLayer.get(h).get(w).getGreenComponent();
        double backgroundComponentB = backgroundLayer.get(h).get(w).getBlueComponent();

        Pixels currentHSLRep = convertRGBToHSL(currentComponentR / 255,
                currentComponentG / 255, currentComponentB / 255);
        Pixels backgroundHSLRep = convertRGBToHSL(backgroundComponentR / 255,
                backgroundComponentG / 225, backgroundComponentB / 255);

        double[] newRGBComponent;

        if (operation.equals("multiply")) {
          newRGBComponent = convertHSLtoRGB(currentHSLRep.getHueComponent(),
                  currentHSLRep.getSaturationComponent(),
                  currentHSLRep.getLightnessComponent() *
                          backgroundHSLRep.getLightnessComponent());
        } else if (operation.equals("screen")) {
          newRGBComponent = convertHSLtoRGB(currentHSLRep.getHueComponent(),
                  currentHSLRep.getSaturationComponent(),
                  (1 - ((1 - currentHSLRep.getLightnessComponent()) *
                          (1 - backgroundHSLRep.getLightnessComponent()))));
        } else {
          throw new IllegalArgumentException("Invalid operation argument");
        }

        int newComponentR = checkComponentOutOfBound((int)newRGBComponent[0]);
        int newComponentG = checkComponentOutOfBound((int)newRGBComponent[1]);
        int newComponentB = checkComponentOutOfBound((int)newRGBComponent[2]);

        currentLayer.get(h).get(w).changeRedComponent(newComponentR);
        currentLayer.get(h).get(w).changeRedComponent(newComponentG);
        currentLayer.get(h).get(w).changeRedComponent(newComponentB);

      }
    }

    return currentLayer;
  }

  /**
   * Restricts the component value, so it is not greater than the max value or less than the minimum
   * value.
   * @param component - value of the component.
   * @return integer value of the component. Applies restrictions if necessary.
   */
  private int checkComponentOutOfBound(int component) {
    if (component > this.maxValue) {
      return this.maxValue;
    } else if (component < 0) {
      return 0;
    } else {
      return component;
    }
  }

  /**
   * Converts from RGB representation to HSL representation.
   * @param r - red component value.
   * @param g - green component value.
   * @param b - green component value.
   * @return A pixel after it has been converted to HSL value.
   */
  private Pixels convertRGBToHSL(double r, double g, double b) {
    double componentMax = Math.max(r, Math.max(g, b));
    double componentMin = Math.min(r, Math.min(g, b));
    double delta = componentMax - componentMin;

    double lightness = (componentMax + componentMin) / 2;
    double hue;
    double saturation;
    if (delta == 0) {
      hue = 0;
      saturation = 0;
    } else {
      saturation = delta / (1 - Math.abs(2 * lightness - 1));
      hue = 0;
      if (componentMax == r) {
        hue = (g - b) / delta;
        while (hue < 0) {
          hue += 6; //hue must be positive to find the appropriate modulus
        }
        hue = hue % 6;
      } else if (componentMax == g) {
        hue = (b - r) / delta;
        hue += 2;
      } else if (componentMax == b) {
        hue = (r - g) / delta;
        hue += 4;
      }

      hue = hue * 60;
    }

    return new Pixels(hue, saturation, lightness);

  }

  /**
   * Converts from HSL representation to RGB representation.
   * @param hue - hue component of the HSL representation.
   * @param saturation - saturation component of the HSL representation.
   * @param lightness - lightness component of the HSL representation.
   * @return a list of double containing the converted RGB values.
   */
  private double[] convertHSLtoRGB(double hue, double saturation, double lightness) {
    double r = convertFn(hue, saturation, lightness, 0) * 255;
    double g = convertFn(hue, saturation, lightness, 8) * 255;
    double b = convertFn(hue, saturation, lightness, 4) * 255;

    double[] pixels = {r, g, b};

    return pixels;
  }

  /**
   * Computes the RGB components given the HSL representation.
   * @param hue - hue component of the HSL representation.
   * @param saturation - saturation component of the HSL representation.
   * @param lightness - lightness component of the HSL representation.
   * @param n - factor used to compute the respective HSL value.
   * @return - a double value containing the computed HSL value.
   */
  private double convertFn(double hue, double saturation, double lightness, int n) {
    double k = (n + (hue / 30)) % 12;
    double a  = saturation * Math.min(lightness, 1 - lightness);
    return lightness - a * Math.max(-1, Math.min(k - 3, Math.min(9 - k, 1)));
  }

  /**
   * Finds the given filter after looking at it's filter command as a string.
   * @param filterCommand - a string corresponding to a respective filter command.
   * @return - a Filter corresponding to the string provided.
   */
  private Filter stringToFilterOption(String filterCommand) {
    for (Filter fil : Filter.values()) {
      if (fil.getValue().equals(filterCommand)) {
        return fil;
      }
    }
    throw new IllegalArgumentException("Filter does not exist");
  }
}
