package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This interface represents the operations that can be applied to a given project
 * and the creation of images. Includes methods which represent different operations that can be
 * applied to an image.
 */
public interface CollageModel {

  /**
   * Allows the user to create a new project.
   * @param height - height of the canvas.
   * @param width - width of the canvas.
   */
  void newProject(int height, int width) throws IllegalArgumentException;

  /**
   * Loads a project to the program.
   * @param projectContent - Contains the height, the width, and the max value, the layers and the
   *                       content of each layer.
   * @throws IllegalStateException - if a project has already been loaded.
   * @throws IllegalArgumentException - if project is not of proper format.
   */
  void loadProject(LinkedHashMap[] projectContent) throws IllegalStateException,
          IllegalArgumentException;

  /**
   * Allows the user to save a project.
   */
  String saveProject() throws IOException;

  /**
   * Allows a user to add a new layer to the project.
   * @param layerName - user specified layer name.
   */
  void addLayer(String layerName);

  /**
   * Adds a new image to the layer.
   * @param layerName - user specified layer name.
   * @param imageContent - list of image content.
   * @param xPos - x-coordinate of where the user would like the image to be placed.
   * @param yPos - y-coordinate of where the user would like the image to be placed.
   */
  void addImageToLayer(String layerName, ArrayList<ArrayList<IPixels>> imageContent, int xPos,
                       int yPos, String fileForm);

  /**
   * Applies a filter to an existing layer.
   * @param layerName - user specified layer name.
   * @param filterOption - the type of filter the user would like to apply to the image.
   */
  void setFilter(String layerName, String filterOption);

  /**
   * Save the filtered image to the project file.
   */
  ArrayList saveImage() throws IOException;

  /**
   * Returns the height of the canvas.
   */
  int getHeight();

  /**
   * Returns the width of the canvas.
   */
  int getWidth();

  /**
   * Returns the maxValue of the canvas.
   */
  int maxValue();

  /**
   * Gets the intended file extension.
   * @return
   */
  String getFileExtension();





}
