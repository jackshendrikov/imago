package ua.jackshen.editor;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

/**
 * @author Jack Shendrikov
 */


public class Main extends Application {

    private GraphicsContext g;
    private ColorPicker strokeColor;
    private TextField strokeField;

    private final static SepiaTone sepiaEffect = new SepiaTone(0);
    private final static GaussianBlur gaussianEffect = new GaussianBlur(0);
    private final static Glow glowEffect = new Glow(0);
    private final static Bloom bloomEffect = new Bloom(1);

    private double imageOriginalWidth, imageOriginalHeight, fixedImageHeight;
    private final static int CANVAS_WIDTH = 900;
    private final static int CANVAS_HEIGHT = 750;
    private double fixedImageWidth = 900;

    private LinkedList<GraphicShape> shapeList = new LinkedList<>();

    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // =================================================== MENU BAR ====================================================
        MenuBar menuBar = new MenuBar();
        SeparatorMenuItem divider = new SeparatorMenuItem();

        // Create canvas
        StackPane drawingArea = new StackPane();
        drawingArea.setStyle("-fx-background-color: #eee");

        // Create menus
        Menu fileMenu = new Menu("File");

        // Create FileMenu items
        MenuItem newCanvasItem = new MenuItem("New Canvas");
        MenuItem clearBorder = new MenuItem("Delete Borders");
        MenuItem openImage = new MenuItem("Open Image");
        MenuItem saveImageItem = new MenuItem("Save Image");
        MenuItem exitItem = new MenuItem("Exit");

        // Add menu items to Menus
        fileMenu.getItems().addAll(newCanvasItem, openImage, saveImageItem, divider, clearBorder, exitItem);

        menuBar.getMenus().addAll(fileMenu);


        final BufferedImage[] bufferedImage = {null};
        try {
            URL url = new URL("https://i.imgur.com/3ZABkSo.jpg");
            bufferedImage[0] = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Image[] image = {SwingFXUtils.toFXImage(bufferedImage[0], null)};
        imageOriginalWidth = image[0].getWidth();
        imageOriginalHeight = image[0].getHeight();
        ImageView chosenImage = new ImageView();

        chosenImage.setImage(image[0]);
        chosenImage.preserveRatioProperty().set(true);
        chosenImage.setFitWidth(fixedImageWidth);
        fixedImageHeight = Utils.ComputeRatio(imageOriginalWidth, imageOriginalHeight, fixedImageWidth);
        chosenImage.setFitHeight(fixedImageHeight);

        // ==================================================FUNCTIONS======================================================
        // new canvas
        newCanvasItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newCanvasItem.setOnAction(e -> {
            g.clearRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());
            shapeList.clear();
            strokeField.clear();
            strokeColor.setValue(Color.BLACK);
            chosenImage.setImage(null);
        });

        // open image
        openImage.setAccelerator(KeyCombination.keyCombination("Ctrl+T"));
        openImage.setOnAction(e -> {
            chosenImage.setImage(null);
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("GIF", "*.gif"),
                    new FileChooser.ExtensionFilter("BMP", "*.bmp"));

            //Show open file dialog
            File file = fileChooser.showOpenDialog(null);
            try {
                bufferedImage[0] = ImageIO.read(file);
                image[0] = SwingFXUtils.toFXImage(bufferedImage[0], null);
                imageOriginalWidth = image[0].getWidth();
                imageOriginalHeight = image[0].getHeight();

                chosenImage.setImage(image[0]);
                chosenImage.preserveRatioProperty().set(true);
                chosenImage.setFitWidth(fixedImageWidth);
                fixedImageHeight = Utils.ComputeRatio(imageOriginalWidth, imageOriginalHeight, fixedImageWidth);
                chosenImage.setFitHeight(fixedImageHeight);
                glowEffect.setInput(bloomEffect);
                gaussianEffect.setInput(glowEffect);
                sepiaEffect.setInput(gaussianEffect);
                chosenImage.setEffect(sepiaEffect);

            } catch (MalformedURLException ignored) {
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        // save image
        saveImageItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Result Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"));
        saveImageItem.setOnAction(e -> {
            File chosenFilePath = fileChooser.showSaveDialog(new Stage());
            if (chosenFilePath != null) {
                String savedFileName = chosenFilePath.getName();
                String savedFileExtension = savedFileName.substring(savedFileName.indexOf(".") + 1, savedFileName.length());
                BufferedImage bImage = SwingFXUtils.fromFXImage(chosenImage.snapshot(null, null), null);
                try {
                    ImageIO.write(bImage, savedFileExtension, chosenFilePath);
                } catch (IOException ex) {
                    AlertBox.warning("Error", "Could not save", "Error. Could not save image to desired location.");
                }
            }
        });

        // clear borders
        clearBorder.setAccelerator(KeyCombination.keyCombination("Ctrl+Y"));
        clearBorder.setOnAction(e -> {
            g.clearRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());
            AlertBox.confirm("Delete Borders", "Clear Borders", "Borders successfully deleted!");
        });

        // exit
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
        exitItem.setOnAction(e -> {
            AlertBox.confirm("Exit Graphic Editor", "Exit Graphic Editor", "Are you sure you want to quit Graphic Editor?");
            System.exit(0);
        });

        // ================================================= END MENU BAR ==================================================
        // ==================================================== PANELS =====================================================
        GridPane shapeChooser = new GridPane();
        shapeChooser.setPadding(new Insets(30, 0, 0, 20));
        shapeChooser.setPrefWidth(180);

        // border buttons
        VBox shapeButtons = new VBox();

        Button allBorderButton = new Button("All Border");
        GridPane.setConstraints(allBorderButton, 0, 1);
        allBorderButton.setPrefWidth(140);

        Button horizontalBorderButton = new Button("Horizontal");
        GridPane.setConstraints(horizontalBorderButton, 0, 2);
        horizontalBorderButton.setPrefWidth(140);

        Button verticalBorderButton = new Button("   Vertical   ");
        GridPane.setConstraints(horizontalBorderButton, 0, 3);
        horizontalBorderButton.setPrefWidth(140);

        shapeButtons.setSpacing(15);
        shapeButtons.getChildren().addAll(allBorderButton, horizontalBorderButton, verticalBorderButton);
        GridPane.setConstraints(shapeButtons, 0, 0);

        shapeChooser.getChildren().addAll(shapeButtons);

        GridPane shapeProperties = new GridPane();
        shapeProperties.setPadding(new Insets(30, 20, 10, 20));
        shapeProperties.setVgap(20);
        shapeProperties.setPrefWidth(200);

        // image panel
        VBox imageProperties = new VBox();
        imageProperties.setSpacing(5);

        Slider opacityLevel = new Slider(0, 1, 1);
        Label opacityCaption = new Label("SetOpacity Level:");
        Label opacityValue = new Label(Double.toString(opacityLevel.getValue()));
        GridPane.setConstraints(opacityCaption, 0, 1);

        Slider sepiaTone = new Slider(0, 1, 0);
        Label sepiaCaption = new Label("Sepia Tone:");
        Label sepiaValue = new Label(Double.toString(sepiaTone.getValue()));

        Slider gaussianBlur = new Slider(0, 30, 0);
        Label gaussianCaption = new Label("Gaussian Blur:");
        Label gaussianValue = new Label(Double.toString(gaussianBlur.getValue()));

        Slider glowTone = new Slider(0, 1, 0);
        Label glowCaption = new Label("Glow Tone:");
        Label glowValue = new Label(Double.toString(glowTone.getValue()));

        Slider bloom = new Slider(0, 1, 1);
        Label bloomCaption = new Label("Bloom:");
        Label bloomValue = new Label(Double.toString(bloom.getValue()));

        Slider scaling = new Slider(0.5, 1, 1);
        Label scalingCaption = new Label("Scaling Factor:");
        Label scalingValue = new Label(Double.toString(scaling.getValue()));

        Label strokeFieldLabel = new Label("Enter border width:");
        strokeField = new TextField();

        imageProperties.getChildren().addAll(opacityCaption, opacityValue, opacityLevel, sepiaCaption, sepiaValue, sepiaTone,
                gaussianCaption, gaussianValue, gaussianBlur, glowCaption, glowValue, glowTone, bloomCaption, bloomValue, bloom,
                scalingCaption, scalingValue, scaling, strokeFieldLabel, strokeField);
        GridPane.setConstraints(imageProperties, 0, 1);

        // color panel
        VBox colorProperties = new VBox();

        Label strokeColorLabel = new Label("Choose border color:");
        strokeColor = new ColorPicker(Color.TRANSPARENT);
        strokeColor.setPrefWidth(200);

        strokeColor.setOnAction(e -> g.setStroke(strokeColor.getValue()));

        colorProperties.setSpacing(5);
        colorProperties.getChildren().addAll(strokeColorLabel, strokeColor);
        GridPane.setConstraints(colorProperties, 0, 2);

        // effects
        glowEffect.setInput(bloomEffect);
        gaussianEffect.setInput(glowEffect);
        sepiaEffect.setInput(gaussianEffect);
        chosenImage.setEffect(sepiaEffect);
        drawingArea.getChildren().add(chosenImage);

        new SetOpacity(chosenImage, opacityLevel, opacityValue);

        sepiaTone.valueProperty().addListener((ov, old_val, new_val) -> {
            sepiaEffect.setLevel(new_val.doubleValue());
            sepiaValue.setText(String.format("%.2f", (double) new_val));
        });

        gaussianBlur.valueProperty().addListener((ov, old_val, new_val) -> {
            gaussianEffect.setRadius(new_val.doubleValue());
            gaussianValue.setText(String.format("%.2f", (double) new_val));
        });

        glowTone.valueProperty().addListener((ov, old_val, new_val) -> {
            glowEffect.setLevel(new_val.doubleValue());
            glowValue.setText(String.format("%.2f", (double) new_val));
        });

        bloom.valueProperty().addListener((ov, old_val, new_val) -> {
            bloomEffect.setThreshold(new_val.doubleValue());
            bloomValue.setText(String.format("%.2f", (double) new_val));
        });

        scaling.valueProperty().addListener((ov, old_val, new_val) -> {
            chosenImage.setScaleX(new_val.doubleValue());
            chosenImage.setScaleY(new_val.doubleValue());
            scalingValue.setText(String.format("%.2f", new_val));
        });

        // buttons on action
        allBorderButton.setOnAction(e -> {
            int stroke;
            double topRight = (CANVAS_HEIGHT - fixedImageHeight) / 2;
            double bottomRight = ((CANVAS_HEIGHT - fixedImageHeight) / 2) + fixedImageHeight;

            try {
                stroke = Integer.parseInt(strokeField.getText());
                shapeList.add(new Line(1, topRight, fixedImageWidth, topRight, stroke, strokeColor.getValue()));
                shapeList.add(new Line(1, topRight, 1, bottomRight, stroke, strokeColor.getValue()));
                shapeList.add(new Line(CANVAS_WIDTH - 1, topRight, CANVAS_WIDTH - 1, bottomRight, stroke, strokeColor.getValue()));
                shapeList.add(new Line(1, bottomRight, fixedImageWidth, bottomRight, stroke, strokeColor.getValue()));
            } catch (Exception exc) {
                AlertBox.warning("Empty Fields", "Empty Fields", "To continue please fill Position and Size fields!");
            }
            drawShape(g);
        });

        horizontalBorderButton.setOnAction(e -> {
            int stroke;
            double topRight = (CANVAS_HEIGHT - fixedImageHeight) / 2;
            double bottomRight = ((CANVAS_HEIGHT - fixedImageHeight) / 2) + fixedImageHeight;

            try {
                stroke = Integer.parseInt(strokeField.getText());
                shapeList.add(new Line(1, topRight, 1, bottomRight, stroke, strokeColor.getValue()));
                shapeList.add(new Line(CANVAS_WIDTH - 1, topRight, CANVAS_WIDTH - 1, bottomRight, stroke, strokeColor.getValue()));
            } catch (Exception exc) {
                AlertBox.warning("Empty Fields", "Empty Fields", "To continue please fill Position and Size fields!");
            }
            drawShape(g);
        });

        verticalBorderButton.setOnAction(e -> {
            int stroke;
            double topRight = (CANVAS_HEIGHT - fixedImageHeight) / 2;
            double bottomRight = ((CANVAS_HEIGHT - fixedImageHeight) / 2) + fixedImageHeight;

            try {
                stroke = Integer.parseInt(strokeField.getText());
                shapeList.add(new Line(1, topRight, fixedImageWidth, topRight, stroke, strokeColor.getValue()));
                shapeList.add(new Line(1, bottomRight, fixedImageWidth, bottomRight, stroke, strokeColor.getValue()));
            } catch (Exception exc) {
                AlertBox.warning("Empty Fields", "Empty Fields", "To continue please fill Position and Size fields!");
            }
            drawShape(g);
        });

        // ================================================== END PANELS ===================================================
        // ================================================ Layout Settings ================================================

        shapeProperties.getChildren().addAll(imageProperties, colorProperties);

        Canvas area = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        g = area.getGraphicsContext2D();
        drawingArea.getChildren().add(area);

        root.setTop(menuBar);
        root.setLeft(shapeChooser);
        root.setRight(shapeProperties);
        root.setCenter(drawingArea);

        Scene scene = new Scene(root, 1300, 800);
        scene.getStylesheets().addAll(this.getClass().getResource("Main.css").toExternalForm());
        primaryStage.getIcons().add(new Image("https://i.imgur.com/9OsVkvB.png"));
        primaryStage.setTitle("Image Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawShape(GraphicsContext g) {
        for (GraphicShape aShapeList : shapeList) {
            aShapeList.drawShape(g);
        }
    }

    public static void main(String args[]) {
        launch(args);
    }
}