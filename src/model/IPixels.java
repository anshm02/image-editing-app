package model;

/**
 * This interface allows for the retrival of the different components within pixel. The interface
 * offers the ability to retrive the RGB and HSL values. It also allows for changing of the
 * different RGB components.
 */
public interface IPixels {

  /**
   * Returns the coordinate the Pixel.
   * @return - the coordinate of the pixel.
   */
  Coord getCoord();

  /**
   * Returns the red component of the Pixel.
   * @return - the red component of the pixel.
   */
  int getRedComponent();

  /**
   * Returns the green component of the Pixel.
   * @return - the green component of the pixel.
   */
  int getGreenComponent();

  /**
   * Returns the blue component of the Pixel.
   * @return - the blue component of the pixel.
   */
  int getBlueComponent();

  /**
   * Returns the alpha component of the Pixel.
   * @return - the alpha component of the pixel.
   */
  int getAlphaComponent();

  /**
   * Returns the hue component of the Pixel.
   * @return - the hue component of the pixel.
   */
  double getHueComponent();

  /**
   * Returns the saturation component of the Pixel.
   * @return - the saturation component of the pixel.
   */
  double getSaturationComponent();

  /**
   * Returns the lightness component of the Pixel.
   * @return - the lightness component of the pixel.
   */
  double getLightnessComponent();

  /**
   * * Changes the red component of the Pixel.
   * @param component - red component of the pixel.
   */
  void changeRedComponent(int component);

  /**
   * * Changes the green component of the Pixel.
   * @param component - green component of the pixel.
   */
  void changeGreenComponent(int component);

  /**
   * * Changes the blue component of the Pixel.
   * @param component - blue component of the pixel.
   */
  void changeBlueComponent(int component);

  /**
   * Represents the RGBA component in an appropriate collage format.
   * @return an String containing the RBGA components.
   */
  String pixelToString();

  /**
   * Represents the RGB component in an appropriate collage format.
   * @return a String containing the RBG components.
   */
  String pixelToStringPPM();


}
