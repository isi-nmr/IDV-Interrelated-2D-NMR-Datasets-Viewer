package IDV;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Chart {
    private final Rectangle zoomRect = new Rectangle();

    private final ContextMenu contextMenu;
    private NumberAxis xAxis = new NumberAxis(0,100,10);
    private NumberAxis yAxis = new NumberAxis(0,100,10);
    private Group chart = new Group();
    private LineChart linechart;

    private XYChart.Series series = new XYChart.Series();
    private double xAxislowerBound;
    private double xAxisUpperBound;
    private double pre_view_xL;
    private double pre_view_xU;
    private double pre_view_yL;
    private double pre_view_yU;
    private double ini_view_xL;
    private double ini_view_xU;
    private double ini_view_yL;
    private double ini_view_yU;
    private boolean dataTips;
    ArrayList<Label> labelsPosition = new ArrayList<>();


    public Chart() {
        zoomRect.setManaged(false);
        zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
        linechart = new LineChart(xAxis, yAxis);
        chart.getChildren().add(linechart);
        linechart.setCreateSymbols(false);
        linechart.getData().add(series);

        chart.getChildren().add(zoomRect);
        linechart.setPrefSize(800,600);
        setUpZooming(zoomRect, linechart);
        linechart.setAnimated(false);
        chart.onKeyPressedProperty().bind(linechart.onKeyPressedProperty());
        linechart.requestFocus();

        contextMenu = new ContextMenu();
        MenuItem privious_view = new MenuItem("Previous View");
        MenuItem initial_view = new MenuItem("Initial View");
        MenuItem ppmItem = new MenuItem("ppm");
        MenuItem hzItem = new MenuItem("Hz");
        privious_view.setOnAction(event -> restoreView());
        initial_view.setOnAction(event -> intialView());
        contextMenu.getItems().addAll(privious_view);
        contextMenu.getItems().addAll(initial_view);
        contextMenu.getItems().addAll(ppmItem);
        contextMenu.getItems().addAll(hzItem);





    }
    private void removeTips() {
        labelsPosition.forEach(o -> chart.getChildren().removeAll(o));
        labelsPosition.clear();
    }
    private void intialView() {
        removeTips();
        xAxis.setLowerBound(ini_view_xL);
        xAxis.setUpperBound(ini_view_xU);
        yAxis.setLowerBound(ini_view_yL);
        yAxis.setUpperBound(ini_view_yU);
    }

    private void restoreView() {
        removeTips();
        xAxis.setLowerBound(pre_view_xL);
        xAxis.setUpperBound(pre_view_xU);
        yAxis.setLowerBound(pre_view_yL);
        yAxis.setUpperBound(pre_view_yU);
    }

    public void plot(double[] xArray, double[] data, int selected) {
        xAxis.setLowerBound(Arrays.stream(xArray).min().getAsDouble());
        xAxis.setUpperBound(Arrays.stream(xArray).max().getAsDouble());
        yAxis.setLowerBound(Arrays.stream(data).min().getAsDouble() - Math.abs(Arrays.stream(data).min().getAsDouble()));
        yAxis.setUpperBound(Arrays.stream(data).max().getAsDouble() + Math.abs(Arrays.stream(data).max().getAsDouble()));
        ini_view_xL = xAxis.getLowerBound();
        ini_view_xU = xAxis.getUpperBound();
        ini_view_yL = yAxis.getLowerBound();
        ini_view_yU = yAxis.getUpperBound();
        linechart.getData().remove(0, linechart.getData().size());
        XYChart.Series series = new XYChart.Series();
        for (int i = 0; i < xArray.length; i = i + 1) {
            series.getData().add(new XYChart.Data(xArray[i], data[i]));
        }
        linechart.getData().add(series);
        series.setName("signal:  " + (selected+1) + "   ");
    }
    public void plotHoldOn(double[] xArray, double[] data, int selected) {

        XYChart.Series series = new XYChart.Series();
        for (int i = 0; i < xArray.length; i = i + 1) {
            series.getData().add(new XYChart.Data(xArray[i], data[i]));
        }
        linechart.getData().add(series);
        series.setName("signal:  " + (selected+1));
    }


    private void setUpZooming(final Rectangle rect, final Node zoomingNode) {
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();

        zoomingNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                    mouseAnchor.set(new Point2D(event.getX(), event.getY()));
                    rect.setWidth(0);
                    rect.setHeight(0);
                }
                 if (dataTips && event.isShiftDown() && (event.getButton() == MouseButton.PRIMARY) ) {
                        Label labelPosition = new Label();
                        Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                        double x = xAxis.sceneToLocal(mouseSceneCoords).getX() / xAxis.getScale() + xAxis.getLowerBound();
                        double y = yAxis.sceneToLocal(mouseSceneCoords).getY() / yAxis.getScale() + yAxis.getUpperBound();
                        labelPosition.setText(String.format("x: %.2f", x) + "\n" + String.format("y: %.2f", y));
                        labelPosition.setVisible(true);
                        labelPosition.setTranslateX(event.getX());
                        labelPosition.setTranslateY(event.getY());
                        labelsPosition.add(labelPosition);
                        chart.getChildren().addAll(labelPosition);
                    }
                if (dataTips && !event.isShiftDown() && (event.getButton() == MouseButton.PRIMARY) ) {
                    removeTips();
                    Label labelPosition = new Label();
                    Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
                    double x = xAxis.sceneToLocal(mouseSceneCoords).getX() / xAxis.getScale() + xAxis.getLowerBound();
                    double y = yAxis.sceneToLocal(mouseSceneCoords).getY() / yAxis.getScale() + yAxis.getUpperBound();
                    labelPosition.setText(String.format("x: %.2f", x) + "\n" + String.format("y: %.2f", y));
                    labelPosition.setVisible(true);
                    labelPosition.setTranslateX(event.getX());
                    labelPosition.setTranslateY(event.getY());
                    labelsPosition.add(labelPosition);
                    chart.getChildren().addAll(labelPosition);
                }
                }

        });
        zoomingNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    double x = event.getX();
                    double y = event.getY();
                    rect.setX(Math.min(x, mouseAnchor.get().getX()));
                    rect.setY(Math.min(y, mouseAnchor.get().getY()));
                    rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
                    rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));
                }
            }
        });
        zoomingNode.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                linechart.requestFocus();
                if ((event.getButton() == MouseButton.PRIMARY) && !event.isShiftDown()) {
                    if (rect.getWidth() > 5 && rect.getHeight() > 5) {
                        removeTips();
                        doZoom(rect, (LineChart<Number, Number>) zoomingNode);
                        zoomingNode.requestFocus();
                        removeTips();
                    }
                }
            }

        });
        zoomingNode.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                removeTips();
                switch (event.getCode()) {
                    case RIGHT:
                        doShift((LineChart<Number, Number>) zoomingNode, 'r');
                        event.consume();
                        break;
                    case LEFT:
                        doShift((LineChart<Number, Number>) zoomingNode, 'l');
                        event.consume();
                        break;
                }
            }
        });
        zoomingNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

    }
    private void doShift(LineChart<Number, Number> chart, char orin) {
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();

        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        if (orin == 'r') {
            xAxis.setLowerBound(xAxis.getLowerBound() + 5);
            xAxis.setUpperBound(xAxis.getUpperBound() + 5);
        } else {
            xAxis.setLowerBound(xAxis.getLowerBound() - 5);
            xAxis.setUpperBound(xAxis.getUpperBound() - 5);
        }
    }
    private void doZoom(Rectangle zoomRect, LineChart<Number, Number> chart) {
        Point2D zoomTopLeft = new Point2D(zoomRect.getX(), zoomRect.getY());
        Point2D zoomBottomRight = new Point2D(zoomRect.getX() + zoomRect.getWidth(), zoomRect.getY() + zoomRect.getHeight());
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        Point2D yAxisInScene = yAxis.localToScene(0, 0);
        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        Point2D xAxisInScene = xAxis.localToScene(0, 0);
        double xOffset = xAxisInScene.getX() - zoomRect.localToScene(0,0).getX();
        xOffset = zoomTopLeft.getX() - xOffset;
        double yOffset =  yAxisInScene.getY() - zoomRect.localToScene(0,0).getY();
        yOffset = zoomTopLeft.getY() - yOffset;
        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        Point2D x = zoomRect.localToScene(0,0);
        Point2D y = zoomRect.localToParent(0,0);
        Point2D z = chart.localToScene(0,0);
        pre_view_xL = xAxis.getLowerBound();
        pre_view_xU = xAxis.getUpperBound();
        pre_view_yL = yAxis.getLowerBound();
        pre_view_yU = yAxis.getUpperBound();

        xAxis.setLowerBound(xAxis.getLowerBound() + xOffset / xAxisScale);
        xAxis.setUpperBound(xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale);
        yAxis.setUpperBound(yAxis.getUpperBound() + yOffset / yAxisScale);
        yAxis.setLowerBound(yAxis.getUpperBound() + zoomRect.getHeight() / yAxisScale);
        xAxislowerBound = xAxis.getLowerBound();
        xAxisUpperBound = xAxis.getUpperBound();
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }
    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public void setDataTips(boolean dataTips) {
        this.dataTips = dataTips;
        removeTips();
    }

    public Group getChart() {
        return chart;
    }
}
