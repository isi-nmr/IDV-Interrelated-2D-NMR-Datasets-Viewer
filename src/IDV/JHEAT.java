package IDV;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;

public class JHEAT {
    private final GridPane frame ;
    private double[][] data;
    private double[] vGrid;
    private double[] hGrid;
    private ArrayList<ImageView> imageViewArrayList;
    private VBox root = new VBox();
    private GridPane rootBox = new GridPane();
    private int roll_width;
    private int roll_height;
    private int box_width;
    private int box_height;
    private double image_width;
    private double image_height;
    private int numbeOfResponses;
    private int numberOfPoints;
    private double max;
    private double min;
    private Text hLabel;
    private Text vLabel;
    private Node sideBar;
    private boolean reStackPlot = false;
    private boolean reStackDisPlot = false;

    public JHEAT(double image_width, double image_height) {
        this.image_width = image_width;
        this.image_height = image_height;
        frame = new GridPane();
        frame.setHgap(5);
        frame.setVgap(5);
        frame.setPadding(new Insets(20,20,20,20));
    }

    public void plot (double[][] data) {
        if(reStackPlot){
            this.getRoot().getChildren().removeAll(imageViewArrayList);
            frame.getChildren().removeAll(sideBar);

        }
        this.data = data;
        this.numbeOfResponses = data.length;
        this.numberOfPoints = data[0].length;
        this.roll_height = (int) (image_height / numbeOfResponses);
        this.roll_width = numberOfPoints;
        this.max = Arrays.stream(data).flatMapToDouble(Arrays::stream).max().getAsDouble();
        this.min = Arrays.stream(data).flatMapToDouble(Arrays::stream).min().getAsDouble();
        imageViewArrayList = new ArrayList();
        for(int i=0;i<numbeOfResponses;i++){
            Image colorScale = createRollImages(i);
            ImageView imageView = new ImageView(colorScale);
            imageView.setFitWidth(image_width);
            imageView.setId(String.valueOf(i));
            imageViewArrayList.add(imageView);
        }
        root.getChildren().addAll(imageViewArrayList);

        hLabel = new Text("h label");
        vLabel = new Text("v label");
        vLabel.setRotate(-90);
        if(!reStackPlot){
            frame.add(this.getRoot(),2,0,1,1);
            frame.add(hLabel,2,2,1,1);
            frame.add(vLabel,0,0,1,1);
            reStackPlot = false;
        }

        frame.setHalignment(hLabel, HPos.CENTER);
        sideBar = this.getColorBar();
        frame.add(sideBar,3,0);
        reStackPlot = true;
    }
    public void setGrid(double[] vGrid, double[] hGrid) {
        Group vGrids = createVGrid(vGrid);
        Group hGrids = createHGrid(hGrid);
        frame.setValignment(vGrids, VPos.CENTER);
        frame.setHalignment(hGrids, HPos.CENTER);
        frame.add(vGrids, 1,0,1,1);
        frame.add(hGrids, 2,1,1,1);
    }
    private Group createHGrid(double[] hGrid) {
        Group grp = new Group();
        for(double value : hGrid) {
            Text text = new Text(String.format("%.1f", value));
            text.setRotate(90);
            double transX = value*(((image_width)/hGrid.length));
            text.setTranslateX(transX);
            grp.getChildren().add(text);
        }
        return grp;
    }

    private Group createVGrid(double[] vGrid) {
        Group grp = new Group();
        for(double value : vGrid) {
            Text text = new Text(String.format("%.1f", value));
            double transY = value*((image_height/vGrid.length));
            text.setTranslateY(transY);
            grp.getChildren().add(text);

        }
        return grp;
    }
    public void plotDiscreteData (double[][] data) {
        if(reStackDisPlot){
            this.getRootBox().getChildren().removeAll(imageViewArrayList);
            frame.getChildren().removeAll(sideBar);

        }
        this.data = data;
        this.numbeOfResponses = data.length;
        this.numberOfPoints = data[0].length;
        this.box_height = (int) (image_height / numbeOfResponses);
        this.box_width = (int) (image_width / numberOfPoints);
        this.max = Arrays.stream(data).flatMapToDouble(Arrays::stream).max().getAsDouble();
        this.min = Arrays.stream(data).flatMapToDouble(Arrays::stream).min().getAsDouble();
        imageViewArrayList = new ArrayList();
        for(int i=0;i<numbeOfResponses;i++){
            for (int j=0; j<numberOfPoints; j++) {
                Image colorScale = createBoxImages(i, j);
                ImageView imageView = new ImageView(colorScale);
//                imageView.setFitWidth(image_width);
                imageView.setId(String.valueOf(i * (numbeOfResponses+1) + j + 1));
                imageViewArrayList.add(imageView);
                rootBox.add(imageView,i,j);
            }
        }


        hLabel = new Text("h label");
        vLabel = new Text("v label");
        vLabel.setRotate(-90);
        frame.setHalignment(hLabel, HPos.CENTER);
        if(!reStackDisPlot){
            frame.add(rootBox,2,0,1,1);
            frame.add(hLabel,2,2,1,1);
            frame.add(vLabel,0,0,1,1);
            reStackPlot = false;
        }
        sideBar = this.getColorBar();

        frame.add(sideBar,3,0);
        reStackDisPlot = true;
    }
    private Image createBoxImages(int i, int j) {
        WritableImage image = new WritableImage(box_width, box_height);
        PixelWriter pixelWriter = image.getPixelWriter();

            double dataOfPoint = data[i][j] ;
            double value = (dataOfPoint-min)/(max-min);
            Color color = getColorForValue(value);
        for (int x=0; x<box_width; x++) {
            for (int y=0; y<box_height; y++) {
                pixelWriter.setColor(x, y, color);
            }
        }
        return image;
    }
    public void setDiscreteGrid(String[] vGrid, String[] hGrid) {
        Group vGrids = createDisVGrid(vGrid);
        Group hGrids = createDisHGrid(hGrid);
        frame.setValignment(vGrids, VPos.CENTER);
//        frame.setHalignment(hGrids, HPos.CENTER);
        frame.add(vGrids, 1,0,1,1);
        frame.add(hGrids, 2,1,1,1);
    }

    private Group createDisHGrid(String[] hGrid) {
        Group grp = new Group();

        for(int i = 0; i<hGrid.length; i++) {
            Text text = new Text(hGrid[i]);
            double transX = i*box_width;
            text.setTranslateX(transX);
            grp.getChildren().add(text);
        }
        return grp;
    }

    private Group createDisVGrid(String[] vGrid) {
        Group grp = new Group();
        for(int i = 0; i<vGrid.length; i++) {
            Text text = new Text(vGrid[i]);
            double transY = i*(box_height);
            text.setTranslateY(transY);
            grp.getChildren().add(text);

        }
        return grp;
    }

    private Image createRollImages(int i) {
        WritableImage image = new WritableImage(roll_width, roll_height);
        PixelWriter pixelWriter = image.getPixelWriter();
        for (int x=0; x<roll_width; x++) {
            double dataOfPoint = data[i][x] ;
            double value = (dataOfPoint-min)/(max-min);
            Color color = getColorForValue(value);
            for (int y=0; y<roll_height; y++) {
                pixelWriter.setColor(x, y, color);
            }
        }
        return image;
    }
    private Color getColorForValue(double value) {
        double hueValue = 255 * (1 - Mapper.getInstance().mapValue(value));
        if(Mapper.getInstance().isLogScale()) {
            hueValue = 255 * (1 - Math.log10(9 * Mapper.getInstance().mapValue(value) + 1));
        }
        return Color.hsb(hueValue, 1, 1);
    }
    public Node getColorBar() {
        return getColorBar(30,400);
    }
    public Node getColorBar(int colorbar_width, int colorbar_height) {
        WritableImage image = new WritableImage(colorbar_width, colorbar_height );
        PixelWriter pixelWriter = image.getPixelWriter();
        for (int y=0; y<colorbar_height; y++) {
            double value = 1-(((double) y)/colorbar_height);
            Color color = getColorForValue(value);
            for (int x=0; x<colorbar_width; x++) {
                pixelWriter.setColor(x, y, color);
            }
        }
        ImageView imageView = new ImageView(image);
        HBox colorBarFrame = new HBox();
        colorBarFrame.setAlignment(Pos.CENTER);
        colorBarFrame.getChildren().add(imageView);
        Group vGrid = colorBarGrid(colorbar_height);
        colorBarFrame.getChildren().add(vGrid);
        return colorBarFrame;
    }

    private Group colorBarGrid(double colorbar_height) {
        Group grp = new Group();
        double[] values = new double[]{min, max};
        for(double value : values) {
            Text text = new Text("  " + String.format("%5.1f", value));
            double transY =  (1- ((value-min)/(max-min))) * colorbar_height;
            text.setTranslateY(transY);
            grp.getChildren().add(text);
        }
        return grp;
    }

    public void setDisText(String string) {
         for (int i = 0 ; i < numbeOfResponses ; i++) {
            for (int j = 0; j < numberOfPoints; j++) {
                Text text = new Text(string + (i * numbeOfResponses + j));
                getRootBox().add(text, i, j);
                text.setWrappingWidth(box_width);
                text.setTextAlignment(TextAlignment.CENTER);

            }
        }
    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public ArrayList<ImageView> getImageViewArrayList() {
        return imageViewArrayList;
    }

    public void setImageViewArrayList(ArrayList<ImageView> imageViewArrayList) {
        this.imageViewArrayList = imageViewArrayList;
    }

    public VBox getRoot() {
        return root;
    }

    public int getRoll_width() {
        return roll_width;
    }

    public void setRoll_width(int roll_width) {
        this.roll_width = roll_width;
    }

    public int getRoll_height() {
        return roll_height;
    }

    public void setRoll_height(int roll_height) {
        this.roll_height = roll_height;
    }

    public double getImage_width() {
        return image_width;
    }

    public void setImage_width(double image_width) {
        this.image_width = image_width;
    }

    public double getImage_height() {
        return image_height;
    }

    public void setImage_height(double image_height) {
        this.image_height = image_height;
    }

    public double getMax() {
        return max;
    }

    public Node getSideBar() {
        return sideBar;
    }

    public double getMin() {
        return min;
    }

    public GridPane getFrame() {
        return frame;
    }

    public GridPane getRootBox() {
        return rootBox;
    }
}
