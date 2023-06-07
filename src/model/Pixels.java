package model;

/**
 * Represent a Pixel and it's components. Allows for the representation of pixels in RGB and HSL
 * representation. Allows for the ability to create new pixels given RGB and HSL values and also
 * allows for the retrieval and changing of these values.
 */
public class Pixels implements IPixels {

  private Coord coord;
  private int red;
  private int green;
  private int blue;
  private int alpha;
  private double hue;
  private double saturation;
  private double light;

  /**
   * Initializes a new pixel.
   * @param coord - coordinate of the pixel within the canvas.
   * @param red - red component of the pixel.
   * @param green - green component of the pixel.
   * @param blue - blue component of the pixel.
   * @param alpha - alpha component of the pixel.
   */
  public Pixels(Coord coord, int red, int green, int blue, int alpha) {
    this.coord = coord;
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
  }

  /**
   * Initializes a new pixel.
   * @param hue - hue component of the pixel.
   * @param saturation - saturation component of the pixel.
   * @param light - lightness component of the pixel.
   */
  public Pixels(double hue, double saturation, double light) {
    this.hue = hue;
    this.saturation = saturation;
    this.light = light;
  }

  @Override
  public Coord getCoord() {
    return this.coord;
  }

  @Override
  public int getRedComponent() {
    return this.red;
  }

  @Override
  public int getGreenComponent() {
    return this.green;
  }

  @Override
  public int getBlueComponent() {
    return this.blue;
  }

  @Override
  public int getAlphaComponent() {
    return this.alpha;
  }

  @Override
  public double getHueComponent() {
    return this.hue;
  }

  @Override
  public double getSaturationComponent() {
    return this.saturation;
  }

  @Override
  public double getLightnessComponent() {
    return this.light;
  }

  @Override
  public void changeRedComponent(int component) {
    this.red = component;
  }

  @Override
  public void changeGreenComponent(int component) {
    this.green = component;
  }

  @Override
  public void changeBlueComponent(int component) {
    this.blue = component;
  }

  @Override
  public String pixelToString() {
    return this.red + " " + this.green + " " + this.blue + " " + this.alpha + "\n";
  }

  @Override
  public String pixelToStringPPM() {
    return this.red + " " + this.green + " " + this.blue + " ";
  }
}
