package controller;

import java.io.File;
import java.io.IOException;

/**
 * Represents the interface for the controller of the Graphical User
 * interface of the Collage Program. Includes all
 * methods that can be performed through the graphical user interface.
 */
public interface CollageGUIController {

  /**
   * Allows the user to create a new project.
   *
   * @param height - height of the canvas.
   * @param width  - width of the canvas.
   */
  void newProject(int height, int width) throws IllegalArgumentException;

  /**
   * Allows the user to save a project.
   */
  void saveProject(File projectFileToSave) throws IOException;

  /**
   * Save the filtered image to the project file.
   */
  void saveImage(File imageFileToSave) throws IOException;

  /**
   * Loads a project to the program.
   * @param projectFile - Contains the height, the width, and the max value, the layers and the
   *                       content of each layer.
   * @throws IllegalStateException throws illegal state exception;
   * @throws IllegalArgumentException throws illegal state exception;
   */
  void loadProject(File projectFile) throws IllegalStateException,
          IllegalArgumentException;

  /**
   * Allows a user to add a new layer to the project.
   *
   * @param layerName - user specified layer name.
   */
  void addLayer(String layerName) throws IllegalStateException,
          IllegalArgumentException;

  /**
   * Adds a new image to the layer.
   *
   * @param layerName    - user specified layer name.
   * @param imageToAdd   - file of image content.
   * @param xPos         - x-coordinate of where the user would like the image to be placed.
   * @param yPos         - y-coordinate of where the user would like the image to be placed.
   */
  void addImageToLayerPPM(File imageToAdd, String layerName, int xPos, int yPos, String fileExt)
          throws IllegalArgumentException;


  /**
   * Applies a filter to an existing layer.
   *
   * @param layerName    - user specified layer name.
   * @param filterName - the type of filter the user would like to apply to the image.
   */
  void setFilterToLayer(String filterName, String layerName) throws IllegalArgumentException;

}
