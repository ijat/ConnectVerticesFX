package ecc4206.assignment3;
/**
 * Created by ijat on 16-Oct-16.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

// Main class
public class connectVertices extends Application {

    // JavaFX parameters
    private Stage primaryStage;
    private BorderPane root;
    private GraphView canvas;
    private final int circleRadius = 3;

    // Class to create the pane and vertices
    private class GraphView extends Pane {

        // Variables
        private int[][] position;
        private ArrayList<Integer[]> edge;
        private int index;                      // To record index of lines
        private int tVertices;                  // Total vertices

        // Constrcutor with default values
        GraphView(int total_vertices) {
            tVertices = total_vertices;
            position = new int[total_vertices][2];
            edge = new ArrayList<>();
            index = 0;
        }

        // Method to draw lines with circle at both ends
        private void drawLine(double x, double y, double x2, double y2) {
            Line line = new Line(x,y,x2,y2);
            line.setStrokeWidth(2);
            line.setSmooth(true);

            Circle circle = new Circle(x,y,circleRadius);
            circle.setFill(Color.BLUE);
            circle.setStroke(Color.BLUE);

            Circle circle2 = new Circle(x2,y2,circleRadius);
            circle2.setFill(Color.BLUE);
            circle2.setStroke(Color.BLUE);

            this.getChildren().addAll(line);
            this.getChildren().addAll(circle);
            this.getChildren().addAll(circle2);
        }

        // Connect single edge/node with another based on the edge array
        public void addLine(int x, int y, Integer[] edges) {
            if (index < tVertices) {
                position[index][0] = x;
                position[index][1] = y;
                edge.add(edges);
                ++index;
            }
        }

        // Draw all the lines and connect the edges in one go
        public void drawAll() {
            for (int i=0; i<position.length; i++) {
                if (edge.get(i).length > 0 && edge.get(i) != null) {

                    for (int eIndex : edge.get(i)) {
                        this.drawLine(position[i][0], position[i][1], position[eIndex][0], position[eIndex][1]);
                    }

                }
            }
        }
    }

    // main function of this code
    public static void main(String[] args) {launch(args);}

    // The GUI part with some events
    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.root = new BorderPane();

        VBox topContainer = new VBox();  //Creates a container to hold all Menu Objects.
        MenuBar mainMenu = new MenuBar();  //Creates our main menu to hold our Sub-Menus.

        topContainer.getChildren().add(mainMenu);

        // Create menu bar
        Menu file = new Menu("File");
        MenuItem openFile = new MenuItem("Open...");
        SeparatorMenuItem sep = new SeparatorMenuItem();
        MenuItem exitApp = new MenuItem("Exit");
        Menu More = new Menu("More");
        MenuItem drawEx = new MenuItem("Draw Example Line");
        MenuItem clearCanvas = new MenuItem("Clear");
        Menu Help = new Menu("Help");
        MenuItem About = new MenuItem("About");

        // Assign menu items to menu
        file.getItems().addAll(openFile,sep,exitApp);
        More.getItems().addAll(drawEx, clearCanvas);
        Help.getItems().addAll(About);
        mainMenu.getMenus().addAll(file, More, Help);

        // Actions
        exitApp.setOnAction(event -> System.exit(0));
        openFile.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Connect Vertices File","*.cv.txt"),
                    new FileChooser.ExtensionFilter("All Files","*.*t")
            );
            File opened_file = fileChooser.showOpenDialog(primaryStage);
            if (opened_file != null) {
                cvFileParser(opened_file);
            }
        });

        drawEx.setOnAction(event -> {
            this.canvas = new GraphView(6);
            canvas.addLine(30,30,new Integer[]{1,2});
            canvas.addLine(90,30,new Integer[]{0,3});
            canvas.addLine(30,90,new Integer[]{0,3,4});
            canvas.addLine(90,90,new Integer[]{1,2,4,5});
            canvas.addLine(30,150,new Integer[]{2,3,5});
            canvas.addLine(90,150,new Integer[]{3,4});
            canvas.drawAll();
            root.setCenter(this.canvas);
        });

        clearCanvas.setOnAction(event -> {
            canvas.getChildren().clear();
        });

        About.setOnAction(event -> {
            System.out.println("Pressed OK.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText("Created by...");
            alert.setContentText("ijat.my");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        });

        // root scene/layout properties
        root.setTop(topContainer);
        root.setPrefSize(600,600);

        // create new screen
        Scene scene = new Scene(root);

        // Set scene to stage and its propertie
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Connect Vertices v0.1");
        primaryStage.show();
    }

    // Parser to read the vertices formatting. No error detection implemented. Wrong syntax can cause fatal error.
    public void cvFileParser(File f) {
        // Clear canvas if there are any drawings
        if (this.canvas != null) this.canvas.getChildren().clear();

        // Declare the max vertices
        Integer maxVertices = 0;

        // Print out file path to the CMD
        System.out.println("Open: " + f.getAbsolutePath());

        // Convert IO File path to INO file path
        Path file = f.toPath();

        // UTF-8 charset for better text support
        Charset charset = Charset.forName("UTF-8");
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {

            String line = null;
            int index = 0;

            // Read text line by line
            while ((line = reader.readLine()) != null) {

                // Delete all whitespaces after and before
                line = line.trim();

                // This line to make sure that the first line is the total vertices value
                if (index == 0) {
                    maxVertices = Integer.parseInt(line);
                    this.canvas = new GraphView(maxVertices);

                } else {

                    // Split all values in current line and declare some variables
                    String[] array = line.split(" ", -1);               // String that holds the splitted values
                    Integer xx = 0, yy = 0;                             // x and y coord
                    Integer[] edgez = new Integer[(array.length-3)];    // Variables for edge (temporary)
                    int edgezIndex = 0;

                    // Split the values from coordinate and the edges.
                    for (int i=0;i<array.length;i++) {
                        if (i>0) {
                            if (i==1) { // x coord
                                xx = Integer.parseInt(array[i].trim());
                            } else if (i==2) {  // y coord
                                yy = Integer.parseInt(array[i].trim());
                            } else {    // other edges
                                edgez[edgezIndex] = Integer.parseInt(array[i].trim());
                                ++edgezIndex;
                            }
                        }
                    }

                    // Add the data obtained to the GraphView class
                    this.canvas.addLine(xx,yy,edgez);
                }
                ++index;
            }

            this.canvas.drawAll();
            this.root.setCenter(canvas);

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }
}

// ijat.my 17:20 19/10/2016
