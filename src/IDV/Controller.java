package IDV;

import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.RangeSlider;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
    StackPane stackview_center_pane;
    @FXML
    ToggleButton logScale;
    @FXML
    ToggleButton datatips;
    @FXML
    TextArea messeageBar;

    boolean holdon = false;
//    @FXML
//    CheckComboBox dataChoicer;
    @FXML
    ChoiceBox dataChoicer2;
    @FXML
    TabPane mainTab;
    @FXML
    GridPane grid_center;
    @FXML
    RadioButton rainbow;
    @FXML
    Slider lineWidth;
    @FXML
    Slider opacity;
    @FXML
    RadioButton xy;
    @FXML
    RadioButton yz;
    @FXML
    RadioButton xz;
    @FXML
    CheckBox init;
    @FXML
    CheckBox datacheck;
    @FXML
    CheckBox fitcheck;
    @FXML
    CheckBox rescheck;
    @FXML
    CheckBox re;
    @FXML
    CheckBox im;

    @FXML
    CheckBox mag;
    @FXML
    CheckBox ph;


    @FXML
    TextField from;
    @FXML
    TextField to;
    @FXML
    ListView signalList;
//    @FXML
//    CheckComboBox signalSelection;
//    @FXML
//    RangeSlider rangeSlider;
    @FXML
    ToolBar toolbar3d;

    private Color color;
    private ArrayList selectedSignals = new ArrayList();
    private int max_slider;
    private int min_slider;
    private ArrayList<CheckBox> checklist;

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
    double[][] datai = DataHolder.getInstance().dataFDi;
    double[] xArray = DataHolder.getInstance().xArrFD;
    double[] yArray = DataHolder.getInstance().yArrFD;
    private boolean freqdomain = true;
    private boolean ppmunit = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checklist = new ArrayList<CheckBox>();
        checklist.add(datacheck);
        checklist.add(fitcheck);
        checklist.add(rescheck);
        checklist.add(re);
        checklist.add(im);
        checklist.add(mag);
        checklist.add(ph);
        int version = getVersion();
        JHEAT heatmap = new JHEAT(800, 800);
        if(version>9) {
            heatmap.setImage_height(600);
            heatmap.setImage_width(600);
            chart.getLinechart().setPrefSize(600,400);
        }

        heatmap.sethLabel(new Text("frequency"));
        heatmap.plot(data);
        heatmap.setGrid(yArray,xArray);
        grid_center.add(heatmap.getFrame(),0,0);
        plotChart(heatmap.getRoot());

        chart.getChart().setOnContextMenuRequested(event -> chart.getContextMenu().show(anchorPane2,event.getScreenX(),event.getScreenY()));
        grid_center.add(chart.getChart(),1,0);
        HBox.setHgrow(chart.getChart(),Priority.ALWAYS);
//        grid_center.setGridLinesVisible(true);
//        heatmap.getFrame().setGridLinesVisible(true);

        j3D = new J3D(400,400,400);
        j3D.strokewidth = lineWidth.valueProperty();
        j3D.opacity = opacity.valueProperty();
        j3D.setWindow(window);
        heatmap.setWindow(window);
//        colorPicker.setValue(Color.RED);
//        j3D.setColorPicker(colorPicker);
        borderPane1.setCenter(j3D.getScene());
        toolbar3d.getItems().add(j3D.getMouseTip());
        j3D.getScene().heightProperty().bind(anchorPane1.heightProperty());
        j3D.getScene().widthProperty().bind(anchorPane1.widthProperty());
        j3D.getCube().requestFocus();
        final ObservableList<String> signalsList = FXCollections.observableArrayList();
        signalsList.add("All");
        for (int i = 1; i <= yArray.length; i++) {
            signalsList.add("Signal " + i);
        }
        signalList.getItems().setAll(signalsList);
        signalList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        signalSelection.getItems().addAll(signalsList);
        for (int i = 0; i < yArray.length; i++) {
            selectedSignals.add(i);
        }
//        signalSelection.getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
//            @Override
//            public void onChanged(Change c) {
//                if (signalSelection.getCheckModel().getCheckedIndices().size() == 0) {
//                    for (int i = 0; i < yArray.length; i++) {
//                        selectedSignals.add(i);
//                    }
//                    plotter();
//                } else {
//                    selectedSignals = (ArrayList) signalSelection.getCheckModel().getCheckedIndices().stream().collect(Collectors.toList());
//                    plotter();
//                }
//            }
//        });
        signalList.setOnMouseClicked(event -> {
                if (signalList.getSelectionModel().isSelected(0)) {
                    selectedSignals.clear();
                    for (int i = 0; i < yArray.length; i++) {
                        selectedSignals.add(i);
                    }
                    plotter();
                } else {
                    selectedSignals = (ArrayList) signalList.getSelectionModel().getSelectedIndices().stream().map(x -> x=(int)x-1).collect(Collectors.toList());
                    plotter();
                }

        });
        from.setText(String.valueOf(0));
        to.setText(String.valueOf(xArray.length-1));
        max_slider = Integer.valueOf(to.getText());
        min_slider = Integer.valueOf(from.getText());
        from.setOnAction(event ->
        {
            try {
                min_slider = Integer.valueOf(from.getText());
                if(max_slider>0 && max_slider > min_slider )
                plotter();
            } catch (NumberFormatException e) {
            }
        });
        to.setOnAction(event ->
        {
            try {
                max_slider = Integer.valueOf(to.getText());
                if(max_slider<xArray.length && max_slider > min_slider )
                   plotter();
            } catch (NumberFormatException e) {
            }
        });

//        j3D.getRoot().getChildren().get(j3D.getPolygrp())
//        j3D.getPolygrp().getChildren().forEach(e -> e.setOnMouseClicked(event -> {
//            System.out.println("hi");
//        }));

//
//                setOnMouseClicked(event -> {
//
//            System.out.println(event.getPickResult());
//        });

        xy.setOnAction(e -> {
            xz.setSelected(false);
            yz.setSelected(false);
            if (xy.isSelected()) {
                j3D.getRotateX().setAngle(89);
                j3D.getRotateY().setAngle(0);
            } else {
                j3D.getRotateX().setAngle(20);
                j3D.getRotateY().setAngle(-45);
            }
        });

        xz.setOnAction(e -> {
            xy.setSelected(false);
            yz.setSelected(false);
            if (xz.isSelected()) {
                j3D.getRotateX().setAngle(0);
                j3D.getRotateY().setAngle(0);
            } else {
                j3D.getRotateX().setAngle(20);
                j3D.getRotateY().setAngle(-45);
            }
        });

        yz.setOnAction(e -> {
            xz.setSelected(false);
            xy.setSelected(false);
            if (yz.isSelected()) {
                j3D.getRotateX().setAngle(0);
                j3D.getRotateY().setAngle(-89);
            } else {
                j3D.getRotateX().setAngle(20);
                j3D.getRotateY().setAngle(-45);
            }
        });

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
                plotter();
                j3D.setAxis_label_x(new Text(""));
                j3D.setAxis_label_z(new Text("frequency"));
                j3D.setAxis_label_y(new Text("amplitude"));
            }
            });

        ppm.setOnAction(event -> {
            if(!ppmunit) {
                ppmunit = true;
                freqdomain = false;
                xArray = DataHolder.getInstance().xArrPPM;
                yArray = DataHolder.getInstance().yArrFD;
                j3D.setAxis_label_x(new Text(""));
                j3D.setAxis_label_z(new Text("ppm"));
                j3D.setAxis_label_y(new Text("amplitude"));
                plotter();
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
                plotter();
//                if (dataChoicer.getSelectionModel().getSelectedIndex() == 0){
//                    data =  DataHolder.getInstance().dataTD;
//                    color = Color.RED;
//                } else if (dataChoicer.getSelectionModel().getSelectedIndex() == 1){
//                    data =  DataHolder.getInstance().dataTDFit;
//                    color = Color.BLUE;
//                } else {
//                    data =  DataHolder.getInstance().dataTDRes;
//                    color = Color.BLACK;
//                }
                j3D.setAxis_label_x(new Text(""));
                j3D.setAxis_label_z(new Text("time"));
                j3D.setAxis_label_y(new Text("amplitude"));



            }
        });
        j3D.getRightClickMenu().getItems().add(TDomain);

        rainbow.setOnAction(event -> {
            if (rainbow.isSelected()) {
                color = Color.TRANSPARENT;
                j3D.getPlottedElements().getChildren().removeIf(o -> o instanceof Polyline);
                j3D.plotSeries(xArray, yArray, data, false, color, selectedSignals, min_slider, max_slider);
            } else {
                plotter();
            }
        });

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
//        holdon.selectedProperty().addListener( (observable, oldValue, newValue) -> {
//                flagData.set(true);
//                flagFit.set(true);
//                flagResidue.set(true);
//        });
//        dataChoicer.getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
//                                                                      @Override
//                                                                      public void onChanged(Change c) {
//                                                                          plotter(); };});
//        dataChoicer.getCheckModel().check(0);
        datacheck.setOnAction(event -> plotter());
        fitcheck.setOnAction(event -> plotter());
        rescheck.setOnAction(event -> plotter());
        re.setSelected(true);
        datacheck.fire();
        re.setOnAction(event -> plotter());
        im.setOnAction(event -> plotter());
        mag.setOnAction(event -> plotter());
        ph.setOnAction(event -> plotter());

        dataChoicer2.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    switch ((String) newValue) {
                        case "Data":
                            if (flagData.get()) {
                                if (freqdomain||ppmunit ) {
                                    data = DataHolder.getInstance().dataFD;
                                    if (data != null) {
                                        heatmap.plot(data);
                                        heatmap.setGrid(yArray,xArray);
                                    } else {
                                        messeageBar.setText("Data is null");
                                    }
                                } else {
                                    data = DataHolder.getInstance().dataTD;
                                    if (data != null) {
                                        heatmap.plot(data);
                                        heatmap.setGrid(yArray,xArray);
                                    } else {
                                        messeageBar.setText("Data is null");
                                    }
                                }
                            }
                            break;
                        case "Fit":

                                if (flagFit.get()) {
                                    if (freqdomain||ppmunit) {
                                        data = DataHolder.getInstance().dataFDFit;
                                        if (data != null) {
                                            heatmap.plot(data);
                                            heatmap.setGrid(yArray,xArray);
                                        } else {
                                            messeageBar.setText("Fit is null");
                                        }
                                    }else {
                                        data = DataHolder.getInstance().dataTDFit;
                                        if (data != null) {
                                            heatmap.plot(data);
                                            heatmap.setGrid(yArray,xArray);
                                        } else {
                                            messeageBar.setText("Fit is null");
                                        }
                                    }
                                }

                            break;
                        case "Residue":
                            if (flagResidue.get()) {
                                if (freqdomain||ppmunit) {
                                    data = DataHolder.getInstance().dataFDRes;
                                    if (data != null) {
                                        heatmap.plot(data);
                                        heatmap.setGrid(yArray,xArray);
                                    } else {
                                        messeageBar.setText("Residue is null");
                                    }
                                }else {
                                    data = DataHolder.getInstance().dataTDRes;
                                    if (data != null) {
                                        heatmap.plot(data);
                                        heatmap.setGrid(yArray,xArray);
                                    } else {
                                        messeageBar.setText("Residue is null");
                                    }
                                }
                            }
                            break;
                    }
                });

        dataChoicer2.setValue("Data");

        signalList.setOnMouseExited(event -> {
//
            Transition animation = new Transition() {
                {
                    setCycleDuration(Duration.millis(200));
                }

                protected void interpolate(double frac) {
                    signalList.setPrefWidth(signalList.getPrefWidth() - frac * signalList.getPrefWidth() );
                }

            };

            animation.play();
        });
        signalList.setOnMouseEntered(event -> {
            Transition animation = new Transition() {
                {
                    setCycleDuration(Duration.millis(200));
                }

                protected void interpolate(double frac) {
                    signalList.setPrefWidth( frac * 200 );
                }

            };

            animation.play();
        });
        

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
                try {
                    chart.plot(xArray, data[Integer.valueOf(node.getId())], Integer.valueOf(node.getId()));
                } catch (NumberFormatException e) {

                }
                messeageBar.setText("Hold Ctrl For Selecting More Signals");
            }
        }
        });



    }

    public void plotter() {
        rainbow.setSelected(false);
        j3D.getPlottedElements().getChildren().removeIf(o -> o instanceof Polyline);
        int c = 0;
        for (CheckBox check: checklist) {
            if (check.isSelected())
                c++;
        }
        if (c>2)
            holdon = true;
        else {
            holdon = false;
        }
//        if (re.isSelected() && im.isSelected())
//            holdon = true;
//        else {
//            holdon = false;
//        }
        if (datacheck.isSelected()) {
            if (freqdomain || ppmunit) {
                if(re.isSelected()) {
                    color = Color.RED;
                    data = DataHolder.getInstance().dataFD;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(im.isSelected()) {
                    color = Color.GREEN;
                    data = DataHolder.getInstance().dataFDi;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(mag.isSelected()) {
                    color = Color.VIOLET;
                    data = new double[DataHolder.getInstance().dataFD.length][DataHolder.getInstance().dataFD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFD.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFD[i].length ; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataFD[i][j],2)
                                    + Math.pow(DataHolder.getInstance().dataFDi[i][j],2));
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(ph.isSelected()) {
                    color = Color.DARKGREEN;
                    data = new double[DataHolder.getInstance().dataFD.length][DataHolder.getInstance().dataFD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFD.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFD[i].length ; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataFDi[i][j]
                                    ,DataHolder.getInstance().dataFD[i][j]);
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
            } else {
                if(re.isSelected()) {
                    color = Color.RED;
                    data = DataHolder.getInstance().dataTD;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(im.isSelected()) {
                    color = Color.GREEN;
                    data = DataHolder.getInstance().dataTDi;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(mag.isSelected()) {
                    color = Color.VIOLET;
                    data = new double[DataHolder.getInstance().dataTD.length][DataHolder.getInstance().dataTD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTD.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTD[i].length ; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataTD[i][j],2)
                                    + Math.pow(DataHolder.getInstance().dataTDi[i][j],2));
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(ph.isSelected()) {
                    color = Color.DARKGREEN;
                    data = new double[DataHolder.getInstance().dataTD.length][DataHolder.getInstance().dataTD[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTD.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTD[i].length ; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataTDi[i][j]
                                    ,DataHolder.getInstance().dataTD[i][j]);
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
            }
        }
        if (fitcheck.isSelected()) {
            if (freqdomain || ppmunit) {
                if(re.isSelected()) {
                    color = Color.BLUE;
                    data = DataHolder.getInstance().dataFDFit;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(im.isSelected()) {
                    color = Color.ORANGE;
                    data = DataHolder.getInstance().dataFDFiti;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(mag.isSelected()) {
                    color = Color.SADDLEBROWN;
                    data = new double[DataHolder.getInstance().dataFDFit.length][DataHolder.getInstance().dataFDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDFit.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDFit[i].length ; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataFDFit[i][j],2)
                                    + Math.pow(DataHolder.getInstance().dataFDFiti[i][j],2));
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(ph.isSelected()) {
                    color = Color.DARKSALMON;
                    data = new double[DataHolder.getInstance().dataFDFit.length][DataHolder.getInstance().dataFDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDFit.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDFit[i].length ; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataFDFiti[i][j]
                                    ,DataHolder.getInstance().dataFDFit[i][j]);
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
            } else {
                if(re.isSelected()) {
                    color = Color.BLUE;
                    data = DataHolder.getInstance().dataTDFit;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(im.isSelected()) {
                    color = Color.ORANGE;
                    data = DataHolder.getInstance().dataTDFiti;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(mag.isSelected()) {
                    color = Color.SADDLEBROWN;
                    data = new double[DataHolder.getInstance().dataTDFit.length][DataHolder.getInstance().dataTDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDFit.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDFit[i].length ; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataTDFit[i][j],2)
                                    + Math.pow(DataHolder.getInstance().dataTDFiti[i][j],2));
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(ph.isSelected()) {
                    color = Color.DARKSALMON;
                    data = new double[DataHolder.getInstance().dataTDFit.length][DataHolder.getInstance().dataTDFit[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDFit.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDFit[i].length ; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataTDFiti[i][j]
                                    ,DataHolder.getInstance().dataTDFit[i][j]);
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
            }
        }
        if (rescheck.isSelected()) {

            if (freqdomain || ppmunit) {
                if(re.isSelected()) {
                    color = Color.BLACK;
                    data = DataHolder.getInstance().dataFDRes;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);
                }
                if(im.isSelected()) {
                    color = Color.YELLOW;
                    data = DataHolder.getInstance().dataFDResi;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);
                }
                if(mag.isSelected()) {
                    color = Color.BROWN;
                    data = new double[DataHolder.getInstance().dataFDRes.length][DataHolder.getInstance().dataFDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDRes.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDRes[i].length ; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataFDRes[i][j],2)
                                    + Math.pow(DataHolder.getInstance().dataFDResi[i][j],2));
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(ph.isSelected()) {
                    color = Color.CADETBLUE;
                    data = new double[DataHolder.getInstance().dataFDRes.length][DataHolder.getInstance().dataFDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataFDRes.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataFDRes[i].length ; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataFDResi[i][j]
                                    ,DataHolder.getInstance().dataFDRes[i][j]);
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
            } else {
                if(re.isSelected()) {
                    color = Color.BLACK;
                    data = DataHolder.getInstance().dataTDRes;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);
                }
                if(im.isSelected()) {
                    color = Color.YELLOW;
                    data = DataHolder.getInstance().dataTDResi;
                    j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);
                }
                if(mag.isSelected()) {
                    color = Color.BROWN;
                    data = new double[DataHolder.getInstance().dataTDRes.length][DataHolder.getInstance().dataTDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDRes.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDRes[i].length ; j++) {
                            data[i][j] = Math.sqrt(Math.pow(DataHolder.getInstance().dataTDRes[i][j],2)
                                    + Math.pow(DataHolder.getInstance().dataTDResi[i][j],2));
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
                if(ph.isSelected()) {
                    color = Color.CADETBLUE;
                    data = new double[DataHolder.getInstance().dataTDRes.length][DataHolder.getInstance().dataTDRes[0].length];
                    for (int i = 0; i < DataHolder.getInstance().dataTDRes.length ; i++) {
                        for (int j = 0; j < DataHolder.getInstance().dataTDRes[i].length ; j++) {
                            data[i][j] = Math.atan2(DataHolder.getInstance().dataTDResi[i][j]
                                    ,DataHolder.getInstance().dataTDRes[i][j]);
                        }
                    }
                    j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
                }
            }
        }
//        dataChoicer.getCheckModel().getCheckedItems().forEach(selected -> {
//            switch ((String) selected) {
//                case "Data":
//                        color = Color.RED;
//                        if (freqdomain || ppmunit) {
//                            data = DataHolder.getInstance().dataFD;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
//                        } else {
//                            data = DataHolder.getInstance().dataTD;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
//                        }
//                    break;
//                case "Fit":
//                        color = Color.BLUE;
//                        if (freqdomain || ppmunit) {
//                            data = DataHolder.getInstance().dataFDFit;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
//                        } else {
//                            data = DataHolder.getInstance().dataTDFit;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, selectedSignals, min_slider, max_slider);
//                        }
//                    break;
//                case "Residue":
//                        color = Color.BLACK;
//                        if (freqdomain || ppmunit) {
//                            data = DataHolder.getInstance().dataFDRes;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);
//                        } else {
//                            data = DataHolder.getInstance().dataTDRes;
//                            j3D.plotSeries(xArray, yArray, data, holdon, color, 's', selectedSignals, min_slider, max_slider);
//                        }
//                    break;
//            }
//            ;
//        });

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
    private static int getVersion() {
        String version = System.getProperty("java.version");
        if(version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if(dot != -1) { version = version.substring(0, dot); }
        } return Integer.parseInt(version);
    }
}
