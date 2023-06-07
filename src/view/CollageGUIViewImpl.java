package view;

import controller.CollageGUIController;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.FlowLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.BorderLayout;

/**
 * Represents the implementation of the Collage Graphical User Interface view.
 * Allows the user to see a graphical
 * representation of the collage program.
 */
public class CollageGUIViewImpl extends JFrame implements ActionListener, CollageGUIView {

  private CollageGUIController guiController;

  JPanel imagePanel = null;
  JLabel imageLabel = null;
  private File tempImage = null;

  /**
   * Represents the CollageGUIViewImpl constructor.
   * @param guiController represents the gui controller.
   */
  public CollageGUIViewImpl(CollageGUIController guiController) {
    this.guiController = guiController;
    try {
      tempImage = File.createTempFile("temp-image", ".ppm");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    setTitle("Collage GUI");
    setSize(800, 600);
    setResizable(false);

    // Create the menu bar
    JMenuBar menuBar = new JMenuBar();
    createFileMenu(menuBar);
    createLayerMenu(menuBar);

    // Set the menu bar
    setJMenuBar(menuBar);

    Container container = getContentPane();
    //show an image with a scrollbar
    imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    //a border around the panel with a caption
    imagePanel.setBorder(BorderFactory.createEmptyBorder());

    imageLabel = new JLabel();
    imageLabel.setHorizontalAlignment(JLabel.CENTER);
    imageLabel.setVerticalAlignment(JLabel.CENTER);
    JScrollPane imageScrollPane = new JScrollPane(imageLabel);
    imageScrollPane.setPreferredSize(new Dimension(775, 530));
    imagePanel.add(imageScrollPane);
    container.add(imagePanel, BorderLayout.CENTER);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  /**
   * Represents the actionPerformed method which handles all actions.
   * @param e the event to be processed
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    System.out.println("actionCommand: " + actionCommand);
    try {
      switch (actionCommand) {
        case "New Project":
          int height = 0;
          int width = 0;
          JTextField heightField = new JTextField(5);
          JTextField widthField = new JTextField(5);

          JPanel heightWidthPanel = new JPanel();
          heightWidthPanel.add(new JLabel("Height:"));
          heightWidthPanel.add(heightField);
          heightWidthPanel.add(Box.createHorizontalStrut(15)); // a spacer
          heightWidthPanel.add(new JLabel("Width:"));
          heightWidthPanel.add(widthField);

          int heightWidthResult = JOptionPane.showConfirmDialog(null, heightWidthPanel,
                  "Please Enter Height and Width values", JOptionPane.OK_CANCEL_OPTION);
          if (heightWidthResult == JOptionPane.OK_OPTION) {
            height = Integer.parseInt(heightField.getText());
            width = Integer.parseInt(widthField.getText());
          }
          guiController.newProject(height, width);
          renderMessage("New Project created successfully!");
          break;
        case "Open Project":
          JFileChooser loadFileChooser = new JFileChooser();
          loadFileChooser.setDialogTitle("Load Project");
          int loadResult = loadFileChooser.showOpenDialog(this);
          if (loadResult == JFileChooser.APPROVE_OPTION) {
            // Get the selected file and load it as an image
            File loadedFile = loadFileChooser.getSelectedFile();
            guiController.loadProject(loadedFile);
            renderMessage("Project loaded successfully!");
          }
          break;
        case "Save Project":
          JFileChooser saveProjectFileChooser = new JFileChooser();
          saveProjectFileChooser.setDialogTitle("Save Project");
          int saveProjectResult = saveProjectFileChooser.showSaveDialog(this);
          if (saveProjectResult == JFileChooser.APPROVE_OPTION) {
            // Get the selected file and load it as an image
            File fileToSave = saveProjectFileChooser.getSelectedFile();
            guiController.saveProject(fileToSave);
            renderMessage("Project saved successfully!");
          }
          break;
        case "Save Image":
          JFileChooser saveImageFileChooser = new JFileChooser();
          saveImageFileChooser.setDialogTitle("Save Image");
          int saveImageResult = saveImageFileChooser.showSaveDialog(this);
          if (saveImageResult == JFileChooser.APPROVE_OPTION) {
            // Get the selected file and load it as an image
            File imageToSave = saveImageFileChooser.getSelectedFile();
            guiController.saveImage(imageToSave);
            renderMessage("Image saved successfully @ " + imageToSave.getName());
          }
          break;
        case "Add Layer":
          JTextField layerNameField = new JTextField(10);
          JPanel layerNamePanel = new JPanel();
          layerNamePanel.add(new JLabel("Layer Name:"));
          layerNamePanel.add(layerNameField);

          int layerNameResult = JOptionPane.showConfirmDialog(null, layerNamePanel,
                  "Please Enter Layer Name", JOptionPane.OK_CANCEL_OPTION);
          if (layerNameResult == JOptionPane.OK_OPTION) {
            String layerName = layerNameField.getText();
            guiController.addLayer(layerName);
            renderMessage("A new layer with name: '" + layerName + "' was added successfully!");
          }
          break;
        case "Add Image to Layer":
          JFileChooser imageFileChooser = new JFileChooser();
          imageFileChooser.setDialogTitle("Select Image to Add");
          int imageFileResult = imageFileChooser.showOpenDialog(this);
          if (imageFileResult == JFileChooser.APPROVE_OPTION) {
            // Get the selected file and load it as an image
            File image = imageFileChooser.getSelectedFile();

            JPanel addImageToLayerPanel = new JPanel();

            addImageToLayerPanel.add(new JLabel("Image: " + image.getName()));
            addImageToLayerPanel.add(Box.createHorizontalStrut(10));

            JTextField layerNField = new JTextField(10);
            addImageToLayerPanel.add(new JLabel("Layer Name: "));
            addImageToLayerPanel.add(layerNField);
            addImageToLayerPanel.add(Box.createHorizontalStrut(15)); // a spacer


            JTextField fileExtensionField = new JTextField(10);
            addImageToLayerPanel.add(new JLabel("File Extension: "));
            addImageToLayerPanel.add(fileExtensionField);
            addImageToLayerPanel.add(Box.createHorizontalStrut(15)); // a spacer


            JTextField xField = new JTextField(5);
            addImageToLayerPanel.add(new JLabel("X Coordinate: "));
            addImageToLayerPanel.add(xField);
            addImageToLayerPanel.add(Box.createHorizontalStrut(15)); // a spacer

            JTextField yField = new JTextField(5);
            addImageToLayerPanel.add(new JLabel("Y Coordinate: "));
            addImageToLayerPanel.add(yField);
            addImageToLayerPanel.add(Box.createHorizontalStrut(15)); // a spacer

            int addImageToLayerResult = JOptionPane.showConfirmDialog(null,
                    addImageToLayerPanel,
                    "Please Enter Layer Name", JOptionPane.OK_CANCEL_OPTION);
            if (addImageToLayerResult == JOptionPane.OK_OPTION) {
              String layerName = layerNField.getText();
              int x = Integer.parseInt(xField.getText());
              int y = Integer.parseInt(yField.getText());
              //String fileExt = fileExtensionField.getText();
              guiController.addImageToLayerPPM(image, layerName, x, y, "ppm");
              renderMessage("Image has been successfully added to layer!");
            }
          }
          break;
        case "Set Filter to Layer":
          String[] filters = {"normal", "red-component", "green-component", "blue-component"
                              , "brighten-value", "darken-value", "brighten-intensity",
                              "darken-intensity", "brighten-luma", "darken-luma",
                              "blending-difference", "blending-multiply", "blending-screen"};

          JPanel setFilterToLayerPanel = new JPanel();

          JComboBox<String> filtersCB = new JComboBox<String>(filters);
          setFilterToLayerPanel.add(new JLabel("Filter: "));
          setFilterToLayerPanel.add(filtersCB);
          setFilterToLayerPanel.add(Box.createHorizontalStrut(15)); // a spacer

          JTextField layerNField = new JTextField(10);
          setFilterToLayerPanel.add(new JLabel("Layer Name: "));
          setFilterToLayerPanel.add(layerNField);
          setFilterToLayerPanel.add(Box.createHorizontalStrut(15)); // a spacer

          int setFilterToLayerResult = JOptionPane.showConfirmDialog(null
                  , setFilterToLayerPanel,
                  "Please Enter Layer Name", JOptionPane.OK_CANCEL_OPTION);
          if (setFilterToLayerResult == JOptionPane.OK_OPTION) {
            String filterName = (String) filtersCB.getSelectedItem();
            String layerName = layerNField.getText();
            guiController.setFilterToLayer(filterName, layerName);
            renderMessage("Filter has been successfully set!");
          }
          break;
        default:
          break;
      }
      this.guiController.saveImage(this.tempImage);
      String ppmContent = Files.readString(Path.of(tempImage.getPath()));
      BufferedImage image = readPPM(ppmContent);
      imageLabel.setIcon(new ImageIcon(image));
      imageLabel.setLayout(new FlowLayout(FlowLayout.CENTER));
      imagePanel.repaint();
    } catch (IllegalArgumentException | IllegalStateException | IOException ex) {
      try {
        renderMessage(ex.getMessage());
      } catch (IOException exc) {
        throw new RuntimeException(exc);
      }
    }
  }

  /**
   * Represents the createFileMenu in the gui.
   * @param menuBar the menuBar element.
   */
  private void createFileMenu(JMenuBar menuBar) {
    // Create the file menu
    JMenu fileMenu = new JMenu("File");
    // Create menu items
    JMenuItem newProject = new JMenuItem("New Project");
    newProject.addActionListener(this);
    fileMenu.add(newProject);

    JMenuItem loadProject = new JMenuItem("Open Project");
    loadProject.addActionListener(this);
    fileMenu.add(loadProject);

    JMenuItem saveProject = new JMenuItem("Save Project");
    saveProject.addActionListener(this);
    fileMenu.add(saveProject);

    JMenuItem saveImage = new JMenuItem("Save Image");
    saveImage.addActionListener(this);
    fileMenu.add(saveImage);

    // Add the file menu to the menu bar
    menuBar.add(fileMenu);
  }

  /**
   * Represents the createLayerMenu in the gui.
   * @param menuBar menuBar element.
   */
  private void createLayerMenu(JMenuBar menuBar) {
    // Create the file menu
    JMenu layerMenu = new JMenu("Layer");
    // Create menu items
    JMenuItem addLayer = new JMenuItem("Add Layer");
    addLayer.addActionListener(this);
    layerMenu.add(addLayer);

    JMenuItem addImageToLayer = new JMenuItem("Add Image to Layer");
    addImageToLayer.addActionListener(this);
    layerMenu.add(addImageToLayer);

    JMenuItem setFilterToLayer = new JMenuItem("Set Filter to Layer");
    setFilterToLayer.addActionListener(this);
    layerMenu.add(setFilterToLayer);

    // Add the file menu to the menu bar
    menuBar.add(layerMenu);
  }

  /**
   * Represents the renderMessage method.
   * @param message the message to be printed.
   * @throws IOException throws exception.
   */
  @Override
  public void renderMessage(String message) throws IOException {
    System.out.println("Message to render: " + message);
    JOptionPane.showMessageDialog(this, message);
  }

  /**
   * represents the readPPM Method.
   * @param ppmContent the string for ppm content.
   * @return BufferedIamge.
   * @throws IOException throws exception when the ppm format is invalid.
   */
  private static BufferedImage readPPM(String ppmContent) throws IOException {
    BufferedReader reader = new BufferedReader(new StringReader(ppmContent));
    String magicNumber = reader.readLine();
    if (!"P3".equals(magicNumber)) {
      throw new IOException("Invalid PPM file format");
    }
    String dimensionsLine = reader.readLine();
    String[] dimensions = dimensionsLine.split(" ");
    int width = Integer.parseInt(dimensions[0]);
    int height = Integer.parseInt(dimensions[1]);

    int maxValue = Integer.parseInt(reader.readLine());

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int y = 0; y < height; y++) {
      String[] rows = reader.readLine() != null ? reader.readLine().split(" ") : new String[0];
      if (rows.length > 0) {
        int rowVal = 0;
        for (int x = 0; x < width; x++) {
          int r = Integer.parseInt(rows[rowVal]);
          rowVal++;
          int g = Integer.parseInt(rows[rowVal]);
          rowVal++;
          int b = Integer.parseInt(rows[rowVal]);
          rowVal++;
          int rgb = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
          image.setRGB(x, y, rgb);
        }
      }
    }
    return image;
  }
}
