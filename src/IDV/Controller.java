package IDV;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    public static J3D j3D;
    @FXML
    Tab tab1;
    @FXML
    Tab tab2;
    @FXML
    AnchorPane anchorPane1;
    @FXML
    AnchorPane anchorPane2;
    @FXML
    AnchorPane anchorPane3;
    @FXML
    BorderPane borderPane1;
    @FXML
    Pane stackview_center_pane;
    @FXML
    ToggleButton logScale;
    @FXML
    ToggleButton metabol_logScale;
    @FXML
    ToggleButton datatips;
    @FXML
    TextArea messeageBar;
    @FXML
    ToggleButton mesh;
    @FXML
    ToggleButton series;
    @FXML
    ColorPicker colorPicker;
    @FXML
    Pane mainPane_tab3;
    @FXML
    ListView meta_listview;
    @FXML
    ListView param_listview;
    @FXML
    ChoiceBox meshDrawMode;
    @FXML
    Slider SliderZoomX;

    Chart chart = new Chart();
    ArrayList<PickResult> pickResults = new ArrayList<>();
    private int item_meta;
    private int item_param;
    private Integer colIndex = 0;
    private Integer rowIndex = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JHEAT heatmap = new JHEAT(800,600);

        double[][] noise = createNoise();
        double[] xArray = new double[80];
        double[] yArray = new double[50];
        for (int x = 0; x < 80; x=x+1) {
            xArray[x] = x;
        }
        for (int y = 0; y < 50; y=y+1) {
            yArray[y] = y+50;
        }

        heatmap.plot(noise);
        heatmap.setGrid(yArray,xArray);

        stackview_center_pane.getChildren().add(heatmap.getFrame());
        plotChart(heatmap.getRoot(), noise, xArray);
        chart.getChart().setOnContextMenuRequested(event -> chart.getContextMenu().show(anchorPane2,event.getScreenX(),event.getScreenY()));
        chart.getChart().setTranslateX(1050);
        chart.getChart().setTranslateY(50);
        stackview_center_pane.getChildren().add(chart.getChart());
        j3D = new J3D(400,400,400);
        j3D.setColorPicker(colorPicker);
        colorPicker.disableProperty().bind(mesh.selectedProperty());
        mesh.setOnAction(event -> {

            j3D.plotSurface(xArray,yArray, noise);

        });
        series.setOnAction(event -> {

            j3D.plotSeries(xArray,yArray, noise);

        });
        borderPane1.setCenter(j3D.getScene());
        j3D.getScene().heightProperty().bind(anchorPane1.heightProperty());
        j3D.getScene().widthProperty().bind(anchorPane1.widthProperty());
        j3D.getCube().requestFocus();
        datatips.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                chart.setDataTips(datatips.isSelected());
                messeageBar.setText("Hold Shift For Labeling More Points");
            }
        });
        logScale.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Mapper.getInstance().setLogScale(logScale.isSelected());
                heatmap.plot(noise);
            }
        });
        mesh.fire();
        double[][][][] data = createParam();

        SliderZoomX.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                j3D.zoomX((Double) newValue);
            }
        });


        

        JHEAT heatmap_voxels = new JHEAT(450,450);
        mainPane_tab3.getChildren().add(heatmap_voxels.getFrame());
        heatmap_voxels.plotDiscreteData(getData(data,0,0));
        heatmap_voxels.setDisText("Voxel: ");
        heatmap_voxels.getFrame().translateYProperty().bind(mainPane_tab3.heightProperty().divide(6));

        JHEAT heatmap_metabolite = new JHEAT(800,600);
        heatmap_metabolite.plotDiscreteData(data[0][0]);
        String[] vLabels = new String[]{"Cr", "NAA", "Tau", "Cho", "Gln"};
        String[] hLabels = new String[]{"Amplitude", "Frequency Shift", "Damping", "Phase"};
        heatmap_metabolite.setDiscreteGrid(vLabels, hLabels);
        heatmap_metabolite.getFrame().setTranslateX(650);
        Line line = new Line(650, 0, 650, 0);
        line.endYProperty().bind(mainPane_tab3.heightProperty());
        mainPane_tab3.getChildren().add(line);
        mainPane_tab3.getChildren().add(heatmap_metabolite.getFrame());
        selectVoxel(heatmap_voxels, heatmap_metabolite, data);
        String[] drawModes = new String[] {"FILL", "LINE"};
        ObservableList<String> observableList_drawModes = FXCollections.observableArrayList(drawModes);
        meshDrawMode.setItems(observableList_drawModes);
        meshDrawMode.getSelectionModel().select(1);
        meshDrawMode.disableProperty().bind(series.selectedProperty());
        meshDrawMode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int selectedDrawMode = meshDrawMode.getSelectionModel().getSelectedIndex();
                switch (selectedDrawMode) {
                    case 0:
                        j3D.setDrawMode(DrawMode.FILL);
                        break;
                    case 1:
                        j3D.setDrawMode(DrawMode.LINE);
                        break;
                }
            }
        });
        ObservableList<String> observableList_param = FXCollections.observableArrayList(hLabels);
        ObservableList<String> observableList_meta =  FXCollections.observableArrayList(vLabels);
        meta_listview.setItems(observableList_meta);
        param_listview.setItems(observableList_param);
        meta_listview.getSelectionModel().select(0);
        param_listview.getSelectionModel().select(0);
        meta_listview.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                item_meta = meta_listview.getSelectionModel().getSelectedIndex();
                item_param = param_listview.getSelectionModel().getSelectedIndex();
                heatmap_voxels.plotDiscreteData(getData(data,item_param,item_meta));
                heatmap_voxels.setDisText("Voxel: ");
            }
        });

        param_listview.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                item_meta = meta_listview.getSelectionModel().getSelectedIndex();
                item_param = param_listview.getSelectionModel().getSelectedIndex();
                heatmap_voxels.plotDiscreteData(getData(data,item_param,item_meta));
                heatmap_voxels.setDisText("Voxel: ");
            }
        });
        metabol_logScale.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Mapper.getInstance().setLogScale(metabol_logScale.isSelected());
                heatmap_metabolite.plotDiscreteData(data[colIndex][rowIndex]);
                heatmap_voxels.plotDiscreteData(getData(data,item_param,item_meta));
                heatmap_voxels.setDisText("Voxel: ");
            }
        });

    }
    public double[][] getData(double[][][][] mat, int i, int j) {
        double[][] new_mat = new double[5][5];
        for(int vy = 0; vy<5;vy++) {
            for(int vx = 0; vx<5;vx++) {
                new_mat[vx][vy] = mat[vx][vy][i][j];
            }
        }
        return new_mat;
    }
    private double[][][][] createParam() {
        double[][][][] noiseArray = new double[5][5][4][5];
        for(int vy = 0; vy<5;vy++) {
            for(int vx = 0; vx<5;vx++) {
                for (int x = 0; x < 5; x = x + 1) {
                    for (int y = 0; y < 4; y = y + 1) {
                        noiseArray[vx][vy][y][x] = (float) (10*(vx+1)*(vy+1)) * (((40 - (y+100*vx)) * Math.sin(Math.PI * 0.1 * y) + (80 - x* (y+100*vx)) * Math.sin(Math.PI * 0.1 * x)) + 20);
                    }
                }
            }
        }
        return noiseArray;

    }
    private double[][] createNoise() {
        double[][] noiseArray = new double[50][80];

        for (int x = 0; x < 80; x=x+1) {
            for (int y = 0; y < 50; y = y+1) {
                noiseArray[y][x] = (float) ( ((40-y)* Math.sin(Math.PI * 0.1 * y) + (80-x)*Math.sin(Math.PI * 0.1 * x))) ;
            }
        }

        return noiseArray;

    }

    public void selectVoxel(JHEAT root_voxel,JHEAT root_meta, double[][][][] data){
            root_voxel.getRootBox().setOnMouseClicked(event -> {
                Node source = event.getPickResult().getIntersectedNode();
                    colIndex = GridPane.getColumnIndex(source);
                    rowIndex = GridPane.getRowIndex(source);
                if((colIndex!= null) && (rowIndex != null)) {
                    root_meta.plotDiscreteData(data[colIndex][rowIndex]);
                }
            });
    }



    public void plotChart(VBox root, double[][] data, double[] xArray) {

        root.setOnMousePressed(event -> {
            if (event.isControlDown()) {
                PickResult a = event.getPickResult();
                Node node = a.getIntersectedNode();
                chart.plotHoldOn(xArray,data[Integer.valueOf(node.getId())],Integer.valueOf(node.getId()));
            } else {
            PickResult a = event.getPickResult();
            Node node = a.getIntersectedNode();
            chart.plot(xArray,data[Integer.valueOf(node.getId())],Integer.valueOf(node.getId()));
                messeageBar.setText("Hold Ctrl For Selecting More Signals");
            }
        });


    }
}
