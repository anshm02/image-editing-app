package model;

import java.util.ArrayList;

/**
 * Represents a Layer for a image creation app. Contains the pixel content as an ArrayList and
 * also the filter option of a layer. Allows for changing and retrieval of layer's content.
 */
public class Layer implements ILayer {
  private ArrayList pixels;
  private Filter filter;

  /**
   * Initializes the pixel field to an empty array of pixels.
   */
  public Layer() {
    this.pixels = new ArrayList(new ArrayList());
  }

  /**
   * Initializes the filter and pixel fields.
   * @param filter - specified filter option.
   * @param pixels - image content of the pixels.
   */
  public Layer(Filter filter, ArrayList pixels) {
    this.filter = filter;
    this.pixels = pixels;

  }

  @Override
  public Layer addNewLayer(Filter filter, int height, int width, int maxValue, int value) {
    this.filter = filter;

    for (int h = 0; h < height; h++) {
      ArrayList pixelList = new ArrayList();
      for (int w = 0; w < width; w++) {
        pixelList.add(new Pixels(new Coord(h, w), maxValue, maxValue, maxValue, value));
      }
      this.pixels.add(pixelList);
    }

    return this;

  }

  @Override
  public Filter getFilterOption() {
    return this.filter;
  }

  @Override
  public ILayer changeFilterOption(Filter filter) {
    this.filter = filter;
    return this;
  }

  @Override
  public ILayer changePixels(ArrayList pixels) {
    this.pixels = pixels;
    return this;
  }

  @Override
  public ArrayList getPixels() {
    return this.pixels;
  }

}
