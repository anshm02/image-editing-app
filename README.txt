An overall description
Welcome to our image creation application based on layers! This program allows you to create a
unique collage of images, with each image filtered in specific ways to create the perfect finished
product.

Whether you are a professional designer or a beginner, you can create beautiful and creative
images with our application. You can choose to use either a text-based or interactive GUI-based
interface, depending on your personal preferences.

Features
Our program currently offers the following features for users:

- Add images to the collage: Users can select images from their computer and add them to the
collage. The program supports all common image file types, including JPG, PNG, and BMP. Users can
add as many images as they want to the collage, and each image can be resized and moved around on
the canvas to create the perfect layout.

- Apply filters: Users can apply different filters to each image in the collage. The program
currently supports three filters: grayscale, sepia, and saturation. Users can apply one or more
filters to each image to create a unique and customized look for the finished collage.

- Add layers to images: Users can add layers to each image in the collage, allowing for greater
customization of the filters applied to each image. Users can add multiple layers to each image
and apply different filters to each layer, giving users endless possibilities for creating unique
and personalized collages.

- Create new collage projects: Users can create new collage projects and work on them in multiple
sessions. The program allows users to save the project at any point and come back to it later to
continue working on it. This feature is particularly useful for more complex projects that may
require multiple sessions to complete.

- Save projects on to your device: Once the collage is complete, users can save the project onto
their device to be worked on later or to share with others. The program saves the project as a
file that can be opened and edited using the program.

- Load projects: Users can load previously saved projects to continue working on them or to make
additional changes. The program allows users to open saved projects directly from the file location
or by browsing through a list of previously saved projects within the program.

- Save images: Once the collage is complete, users can save the finished image onto their device.
The program saves the image as a JPG or PNG file that can be easily shared on social media or
printed out for personal use.

Requirements/dependencies
To run our program, you will need to have the following:

Java 11 or higher JRE
JUnit 4 for running the tests
How to use the program
Please refer to the USEME file provided separately for detailed instructions on how to use our
program. The USEME file is located in the root of this project folder.

Design Changes - 04/12/2023:
Recently, we made some important design changes to our application to make it possible to read and
write from popular file formats. In order to implement this feature successfully, we needed to
keep track of the file extensions within the model, so that we could produce the output as intended
by the user. To achieve this, we introduced a new field in our model that holds the file format
information.

By adding this new field to our model, we were able to ensure that the application correctly
identifies the file format of the input files, and that the output files are saved in the same
format as the input files. This means that users can now work with their preferred file formats
when creating collages, which makes the application much more user-friendly and versatile.

In addition, by keeping track of the file format information within the model, we have made it
easier to add support for new file formats in the future. All we need to do is add the necessary
code to handle the new format in the model, and the rest of the application will work seamlessly
with the new format.

Overall, these recent design changes have significantly improved the functionality and flexibility
of our application. By allowing users to work with their preferred file formats, we have made it
easier for them to create high-quality collages that meet their specific needs and requirements.


CollageController Interface and CollageControllerImpl Class
This repository contains an interface and its implementation to interact with an image manipulation
application.


Collage Controller Interface
The CollageController interface is located in the package controller and defines the following
method:

void executeCommand() throws IOException;

This method allows a user to interact with the image manipulation application by inputting a set of
commands that correspond to different operations. The operations available through the
executeCommand() method are described in the CollageControllerImpl class.

The CollageControllerImpl class is located in the package controller and represents the
implementation of the CollageController interface. This class allows the user to perform the
following operations:

Create new projects.
Load previously created projects.
Save projects.
Add layers to their images.
Add images to layers.
Filter images.
Save images.

The CollageControllerImpl constructor requires three arguments: a CollageModel instance, a
CollageView instance, and a Readable object.

The executeCommand() method allows the user to interact with the image manipulation application by
inputting a set of commands that correspond to the operations described above.


Usage

To use the CollageController and CollageControllerImpl classes, import them into your project and
create a CollageControllerImpl object with the required arguments. You can then call the
executeCommand() method to begin interacting with the image manipulation application.

To use the CollageController and CollageControllerImpl classes, import them into your project and
create a CollageControllerImpl object with the required arguments. You can then call the
executeCommand() method to begin interacting with the image manipulation application.

CollageController controller = new CollageControllerImpl(model, view, rd);
controller.executeCommand();


Collage Model Interface and Class

Interface

CollageModel

This interface has the following methods:

newProject(int height, int width): Allows the user to create a new project.
loadProject(String pathToFile): Loads a project to the program.
saveProject(String name): Allows the user to save a project.
addLayer(String layerName): Allows a user to add a new layer to the project.
addImageToLayer(String layerName, String imageName, int xPos, int yPos): Adds a new image to the
layer.
setFilter(String layerName, String filterOption): Applies a filter to an existing layer.
saveImage(String fileName): Save the filtered image to the project file.

Class
CollageModelImpl
This class implements the CollageModel interface and provides the actual functionality for the
 collage-making application. It has the following instance variables:

projectLoaded: A boolean flag indicating whether a project has been loaded.
layers: A HashMap containing the layers of the project.
maxValue: The maximum value of the image.
width: The width of the project.
height: The height of the project.
It has the following methods:

CollageModelImpl(): Constructor method for initializing instance variables.
newProject(int height, int width): Implementation of the newProject method in the interface.
loadProject(String pathToFile): Implementation of the loadProject method in the interface.
saveProject(String name): Implementation of the saveProject method in the interface.
addLayer(String layerName): Implementation of the addLayer method in the interface.
addImageToLayer(String layerName, String imageName, int xPos, int yPos): Implementation of the
addImageToLayer method in the interface.
setFilter(String layerName, String filterOption): Implementation of the setFilter method in the
interface.
saveImage(String fileName): Implementation of the saveImage method in the interface.
Usage
To use this collage-making application, create an instance of the CollageModelImpl class and call
its methods as needed. You can create a new project, load an existing project, add layers, add
images to layers, apply filters to layers, and save the final image to a file.



CollageView Interface and CollageViewImpl Class Readme
The CollageView interface and CollageViewImpl class are part of an image creation program, where
CollageView is responsible for displaying the status of the model to the user. The interface
defines a single method, transmitMessage(String message), which is used to display messages to the
user. The CollageViewImpl class implements the CollageView interface and displays messages to the
user through an Appendable object.

CollageView Interface
The CollageView interface provides a way to display messages to the user. The interface contains a
single method:

transmitMessage(String message)
This method is used to display a message to the user. The message parameter is a string that
contains the message to be displayed.

CollageViewImpl Class
The CollageViewImpl class implements the CollageView interface and displays messages to the user.
The class has two fields:

collageModel
This field is of type CollageModel and is used to store the model of the image creation program.

ap
This field is of type Appendable and is used to store all the messages transmitted from the model.

The CollageViewImpl class has a single constructor:

CollageViewImpl(CollageModel collageModel, Appendable ap) throws IllegalArgumentException
This constructor initializes the collageModel and ap fields. If either of these parameters is null,
an IllegalArgumentException is thrown.

The CollageViewImpl class also implements the transmitMessage(String message) method from the
CollageView interface:

transmitMessage(String message) throws IOException
This method appends the message parameter to the ap field, followed by a newline character. If the
append fails, an IOException is thrown.

The program can be run by running the main method in the CollageProgram class-
Here is a sample usage for this program:
# creates a new project:
- new-project 3 4
# creates layer
- add-layer image-layer
# adds image to layer
- add-image-to-layer image-layer sample.ppm 0 0
# adds new layer
- add-layer new-layer
# sets filter to layer
- set-filter new-layer red-component
# sets filter to image layer
- set-filter image-layer brighten-intensity
# saves the project
- save-project sample-save.collage
# save image
- save-image sample-image.ppm
# quit program
- quit


---------------------------------------------------------------------------------------------------
Decoupling of the Views:
The files that you need to send along for only the views to compile are:

1. CollageGUIView interface - This interface defines the contract for the Collage GUI View. It
contains the methods that are necessary for the GUI to interact with the user and display the
collage.
2. CollageView interface - This interface defines the contract for the Collage View. It contains
the methods that are necessary for the view to display the collage.
3. CollageGUIViewImpl class - This class provides the implementation for the CollageGUIView
interface. It contains the code that is necessary for the GUI to interact with the user and display
the collage.
4. CollageViewImpl class - This class provides the implementation for the CollageView interface.
It contains the code that is necessary for the view to display the collage.
5. CollageGUICOntroller class - This class provides the controller for the Collage GUI View. It
contains the code that is necessary for the GUI to interact with the user and update the view.
6. CollageController class - This class provides the controller for the Collage View. It contains
the code that is necessary for the view to update itself.
7. CollageModel interface - This interface defines the contract for the Collage Model. It contains
the methods that are necessary for the model to interact with the view and provide the data for
the collage.

Each of these files is necessary for the views to work because they provide the necessary
functionality for the GUI to interact with the user and display the collage, as well as the
functionality for the view to display the collage and update itself. The controller classes provide
the logic that connects the views to the model, while the model interface provides the necessary
methods for the model to interact with the view and provide the data for the collage.

In summary, sending along the CollageGUIView interface, CollageView interface, CollageGUIViewImpl
class, CollageViewImpl class, CollageGUICOntroller class, CollageController class, and CollageModel
interface will allow the views to compile independently without unnecessary dependencies.


In this project, I have included an image that has been sourced from online.
Here is the citation for PPM image sample.ppm:
Image Formats PPM PGM, http://people.uncw.edu/tompkinsj/112/texnh/assignments/imageFormat.html#top.


