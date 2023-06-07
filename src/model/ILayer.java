package model;

import java.util.ArrayList;

/**
 * This interface represents the different operations that can be applied to a layer.
 * Offers the ability to change a filter option and a layer's pixel content.
 * It also allows for the retrieval of a layer's filter option and it's pixel content.
 */
public interface ILayer {

  /**
   * Changes the filter option of a layer to the specified filter.
   * @param filter - user specified filter option.
   * @return the layer after its filter option has been changed.
   */
  ILayer changeFilterOption(Filter filter);

  /**
   * Changes the pixel content of the layer.
   * @param pixels - ArrayList of pixel content.
   * @return the layer after its pixel content has been changed.
   */
  ILayer changePixels(ArrayList pixels);

  Layer addNewLayer(Filter filter, int height, int width, int maxValue, int value);

  /**
   * Returns the filter option of a layer.
   * @return the filter of the layer.
   */
  Filter getFilterOption();

  /**
   * Return the pixel content of the layer.
   * @return Arraylist of pixels corresponding to a layer.
   */
  ArrayList getPixels();

}
