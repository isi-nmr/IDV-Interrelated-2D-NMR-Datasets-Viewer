package IDV;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Window;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

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
    BorderPane borderPane1;
    @FXML
    Pane stackview_center_pane;
    @FXML
    ToggleButton logScale;
    @FXML
    ToggleButton datatips;
    @FXML
    TextArea messeageBar;
    @FXML
    RadioButton holdon;
    @FXML
    ChoiceBox dataChoicer;
    @FXML
    ChoiceBox dataChoicer2;
    @FXML
    TabPane mainTab;

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    private Window window;



    Chart chart = new Chart();
    ArrayList<PickResult> pickResults = new ArrayList<>();
    private int item_meta;
    private int item_param;
    private Integer colIndex = 0;
    private Integer rowIndex = 0;
    double[][] data = DataHolder.getInstance().dataFD;
    double[] xArray = DataHolder.getInstance().xArrFD;
    double[] yArray = DataHolder.getInstance().yArrFD;
    private boolean freqdomain = true;
    private boolean ppmunit = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JHEAT heatmap = new JHEAT(800,600);
        heatmap.sethLabel(new Text("frequency"));
        heatmap.plot(data);
        heatmap.setGrid(yArray,xArray);

        stackview_center_pane.getChildren().add(heatmap.getFrame());
        plotChart(heatmap.getRoot());
        chart.getChart().setOnContextMenuRequested(event -> chart.getContextMenu().show(anchorPane2,event.getScreenX(),event.getScreenY()));
        chart.getChart().setTranslateX(1050);
        chart.getChart().setTranslateY(50);
        stackview_center_pane.getChildren().add(chart.getChart());


        j3D = new J3D(400,400,400);
        j3D.setWindow(window);
        heatmap.setWindow(window);
//        colorPicker.setValue(Color.RED);
//        j3D.setColorPicker(colorPicker);
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
                heatmap.plot(data);
                heatmap.setGrid(yArray,xArray);
            }
        });
        j3D.setAxis_label_x(new Text(""));
        j3D.setAxis_label_z(new Text("frequency"));
        j3D.setAxis_label_y(new Text("amplitude"));
//        j3D.plotSeries(xArray, yArray, data);

        Menu FDomain = new Menu("frequency domain");
        MenuItem hertz = new MenuItem("Hz");
        MenuItem ppm = new MenuItem("PPM");
        FDomain.getItems().addAll(hertz,ppm);
        hertz.setOnAction(event -> {
            if(!freqdomain) {
                freqdomain = true;
                ppmunit = false;
                xArray = DataHolder.getInstance().xArrFD;
                yArray = DataHolder.getInstance().yArrFD;
                data =  DataHolder.getInstance().dataFD;
                j3D.setAxis_label_x(new Text(""));
                j3D.setAxis_label_z(new Text("frequency"));
                j3D.setAxis_label_y(new Text("amplitude"));
                j3D.plotSeries(xArray, yArray, data,false, Color.RED);
            }
            });

        ppm.setOnAction(event -> {
            if(!ppmunit) {
                ppmunit = true;
                freqdomain = false;
                xArray = DataHolder.getInstance().xArrPPM;
                yArray = DataHolder.getInstance().yArrFD;
                data =  DataHolder.getInstance().dataFD;
                j3D.setAxis_label_x(new Text(""));
                j3D.setAxis_label_z(new Text("ppm"));
                j3D.setAxis_label_y(new Text("amplitude"));
                j3D.plotSeries(xArray, yArray, data,false, Color.RED);
            }
        });
        j3D.getRightClickMenu().getItems().add(FDomain);
        MenuItem TDomain = new MenuItem("time domain");
        TDomain.setOnAction(event -> {
            if(freqdomain || ppmunit) {
                freqdomain = false;
                ppmunit = false;
                xArray = DataHolder.getInstance().xArrTD;
                yArray = DataHolder.getInstance().yArrTD;
                data =  DataHolder.getInstance().dataTD;
                j3D.setAxis_label_x(new Text(""));
                j3D.setAxis_label_z(new Text("time"));
                j3D.setAxis_label_y(new Text("amplitude"));
                j3D.plotSeries(xArray, yArray, data,false, Color.RED);

            }
        });
        j3D.getRightClickMenu().getItems().add(TDomain);



        Menu FDomain2 = new Menu("frequency domain");
        MenuItem hertz2 = new MenuItem("Hz");
        MenuItem ppm2 = new MenuItem("PPM");
        FDomain2.getItems().addAll(hertz2,ppm2);
        hertz2.setOnAction(event -> {
            if(!freqdomain) {
                freqdomain = true;
                ppmunit = false;
                xArray = DataHolder.getInstance().xArrFD;
                yArray = DataHolder.getInstance().yArrFD;
                if (dataChoicer2.getSelectionModel().getSelectedIndex() == 0){
                    data =  DataHolder.getInstance().dataFD;
                } else if (dataChoicer2.getSelectionModel().getSelectedIndex() == 1){
                    data =  DataHolder.getInstance().dataFDFit;
                } else {
                    data =  DataHolder.getInstance().dataFDRes;
                }


                heatmap.plot(data);
                heatmap.setGrid(yArray,xArray);
            }
        });
        MenuItem PPMDomain2 = new MenuItem("ppm domain");
        ppm2.setOnAction(event -> {
            if(!ppmunit) {
                ppmunit = true;
                freqdomain = false;
                xArray = DataHolder.getInstance().xArrPPM;
                yArray = DataHolder.getInstance().yArrFD;
                if (dataChoicer2.getSelectionModel().getSelectedIndex() == 0){
                    data =  DataHolder.getInstance().dataFD;
                } else if (dataChoicer2.getSelectionModel().getSelectedIndex() == 1){
                    data =  DataHolder.getInstance().dataFDFit;
                } else {
                    data =  DataHolder.getInstance().dataFDRes;
                }
                heatmap.plot(data);
                heatmap.setGrid(yArray,xArray);
            }
        });
        MenuItem TDomain2 = new MenuItem("time domain");
        TDomain2.setOnAction(event -> {
            if(freqdomain || ppmunit) {
                freqdomain = false;
                ppmunit = false;
                xArray = DataHolder.getInstance().xArrTD;
                yArray = DataHolder.getInstance().yArrTD;
                if (dataChoicer2.getSelectionModel().getSelectedIndex() == 0){
                    data =  DataHolder.getInstance().dataTD;
                } else if (dataChoicer2.getSelectionModel().getSelectedIndex() == 1){
                    data =  DataHolder.getInstance().dataTDFit;
                } else {
                    data =  DataHolder.getInstance().dataTDRes;
                }
                heatmap.plot(data);
                heatmap.setGrid(yArray,xArray);

            }
        });



        heatmap.getRightClickMenu().getItems().add(FDomain2);
        heatmap.getRightClickMenu().getItems().add(TDomain2);



        AtomicBoolean flagData = new AtomicBoolean(true);
        AtomicBoolean flagFit = new AtomicBoolean(true);
        AtomicBoolean flagResidue = new AtomicBoolean(true);
        holdon.selectedProperty().addListener( (observable, oldValue, newValue) -> {
                flagData.set(true);
                flagFit.set(true);
                flagResidue.set(true);
        });

        dataChoicer.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                        switch ((String) newValue) {
                        case "Data":
                            if (flagData.get()) {
                                if (freqdomain||ppmunit) {
                                    data = DataHolder.getInstance().dataFD;
                                    j3D.plotSeries(xArray, yArray, DataHolder.getInstance().dataFD, holdon.isSelected(), Color.RED);
                                }else {
                                    data = DataHolder.getInstance().dataTD;
                                    j3D.plotSeries(xArray, yArray, DataHolder.getInstance().dataTD, holdon.isSelected(), Color.RED);
                                }
                                if (holdon.isSelected())
                                flagData.set(false);
                            }
                            break;
                        case "Fit":
                            if (flagFit.get()) {
                                if (freqdomain||ppmunit) {
                                    data = DataHolder.getInstance().dataFDFit;
                                    j3D.plotSeries(xArray, yArray, DataHolder.getInstance().dataFDFit, holdon.isSelected(), Color.BLUE);
                                }else {
                                    data = DataHolder.getInstance().dataTDFit;
                                    j3D.plotSeries(xArray, yArray, DataHolder.getInstance().dataTDFit, holdon.isSelected(), Color.BLUE);
                                }
                                if (holdon.isSelected())
                                    flagFit.set(false);
                            }
                            break;
                        case "Residue":
                            if (flagResidue.get()) {
                                if (freqdomain||ppmunit) {
                                    data = DataHolder.getInstance().dataFDRes;
                                    j3D.plotSeries(xArray, yArray, DataHolder.getInstance().dataFDRes, holdon.isSelected(), Color.BLACK,'s');
                                }else {
                                    data = DataHolder.getInstance().dataTDRes;
                                    j3D.plotSeries(xArray, yArray, DataHolder.getInstance().dataTDRes, holdon.isSelected(), Color.BLACK,'s');
                                }
                                if (holdon.isSelected())
                                    flagResidue.set(false);
                            }
                            break;



                    }
                });

        dataChoicer.setValue("Data");


        dataChoicer2.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    switch ((String) newValue) {
                        case "Data":
                            if (flagData.get()) {
                                if (freqdomain||ppmunit ) {
                                    data = DataHolder.getInstance().dataFD;
                                    heatmap.plot(data);
                                    heatmap.setGrid(yArray,xArray);
                                } else {
                                    data = DataHolder.getInstance().dataTD;
                                    heatmap.plot(data);
                                    heatmap.setGrid(yArray,xArray);
                                }
                            }
                            break;
                        case "Fit":
                            if (flagFit.get()) {
                                if (freqdomain||ppmunit) {
                                    data = DataHolder.getInstance().dataFDFit;
                                    heatmap.plot(data);
                                    heatmap.setGrid(yArray,xArray);
                                }else {
                                    data = DataHolder.getInstance().dataTDFit;
                                    heatmap.plot(data);
                                    heatmap.setGrid(yArray,xArray);
                                }
                            }
                            break;
                        case "Residue":
                            if (flagResidue.get()) {
                                if (freqdomain||ppmunit) {
                                    data = DataHolder.getInstance().dataFDRes;
                                    heatmap.plot(data);
                                    heatmap.setGrid(yArray,xArray);
                                }else {
                                    data = DataHolder.getInstance().dataTDRes;
                                    heatmap.plot(data);
                                    heatmap.setGrid(yArray,xArray);
                                }
                            }
                            break;
                    }
                });

        dataChoicer2.setValue("Data");




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
                noiseArray[y][x] = (float) ( ((40-y)* Math.sin(Math.PI * 0.1 * y) + (80-x)* Math.sin(Math.PI * 0.1 * x))) ;
            }
        }

        return noiseArray;

    }

    public void selectVoxel(JHEAT root_voxel, JHEAT root_meta, double[][][][] data){
            root_voxel.getRootBox().setOnMouseClicked(event -> {
                Node source = event.getPickResult().getIntersectedNode();
                    colIndex = GridPane.getColumnIndex(source);
                    rowIndex = GridPane.getRowIndex(source);
                if((colIndex!= null) && (rowIndex != null)) {
                    root_meta.plotDiscreteData(data[colIndex][rowIndex]);
                }
            });
    }



    public void plotChart(VBox root) {

        root.setOnMousePressed(event -> { if (!event.isSecondaryButtonDown()) {
            if (event.isControlDown()) {
                PickResult a = event.getPickResult();
                Node node = a.getIntersectedNode();
                chart.plotHoldOn(xArray, data[Integer.valueOf(node.getId())], Integer.valueOf(node.getId()));
            } else {
                PickResult a = event.getPickResult();
                Node node = a.getIntersectedNode();
                chart.plot(xArray, data[Integer.valueOf(node.getId())], Integer.valueOf(node.getId()));
                messeageBar.setText("Hold Ctrl For Selecting More Signals");
            }
        }
        });



    }

    public double[][] getData() {
        return data;
    }

    public void setData(double[][] data) {
        this.data = data;
    }

    public double[] getxArray() {
        return xArray;
    }

    public void setxArray(double[] xArray) {
        this.xArray = xArray;
    }

    public double[] getyArray() {
        return yArray;
    }

    public void setyArray(double[] yArray) {
        this.yArray = yArray;
    }
}
