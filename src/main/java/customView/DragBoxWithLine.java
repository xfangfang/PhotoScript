package customView;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import tools.ProjectSaver;
import tools.SHAPE;

public class DragBoxWithLine extends DragBox {
    //直线的初始坐标

    private DoubleProperty x = new SimpleDoubleProperty(400);
    private DoubleProperty y = new SimpleDoubleProperty(250);
    private DoubleProperty x1 = new SimpleDoubleProperty(0);
    private DoubleProperty y1 = new SimpleDoubleProperty(0);
    private DoubleProperty x2 = new SimpleDoubleProperty(100);
    private DoubleProperty y2 = new SimpleDoubleProperty(100);


    private enum POINT {L, R, C}
    private POINT cursor = POINT.C;

    public DragBoxWithLine(DragBox dragBox) {
        super(dragBox);
    }

    public DragBoxWithLine() {
        super();
    }

    public DragBoxWithLine(double x, double y, double w, double h, SHAPE.TYPE type, double rotate, double strokenWidth, double a, double r, double g, double b, double fa, double fr, double fg, double fb) {
        super(x, y, w, h, type, rotate, strokenWidth, a, r, g, b, fa, fr, fg, fb);
    }

    @Override
    protected void setKeyAndMouseListener() {

    }

    @Override
    protected void initNode() {
        node.setMouseTransparent(true);
        Line line = (Line) node;
        line.setMouseTransparent(true);
        line.endXProperty().bind(x2.add(x));
        line.endYProperty().bind(y2.add(y));
        line.startXProperty().bind(x1.add(x));
        line.startYProperty().bind(y1.add(y));

        line.setOnMouseDragged(this::switchDrag);

        line.setOnMouseMoved(this::switchPoint);

        line.setOnMousePressed(event -> {
            if (chooseListener != null) {
                chooseListener.request(this);
            }
            deltaX = event.getX()-x.get();
            deltaY = event.getY()-y.get();
            chooseListener.request(this);
            isMousePress = true;
            drawConner();
            event.consume();
        });


        line.setOnMouseReleased(event -> {
            isMousePress = false;
            clearConner();
            drawConner();
            switchPoint(event);
            event.consume();
        });


        X.set(0);
        Y.set(0);
        setPrefHeight(600);
        setPrefWidth(900);
    }

    private void switchDrag(MouseEvent event) {
        clearConner();
        drawConner();
        switch (cursor) {
            case C:
                x.set(event.getSceneX() - deltaX - getParent().getParent().getLayoutX());
                y.set(event.getSceneY() - deltaY -
                        getParent().getParent().getParent().getLayoutY()-getParent().getLayoutY());
                break;
            case L:
                x1.set(event.getX()-x.get());
                y1.set(event.getY()-y.get());
                break;
            case R:
                x2.set(event.getX()-x.get());
                y2.set(event.getY()-y.get());
                break;
        }
        event.consume();

    }

    private void switchPoint(MouseEvent event) {
        double tempX = event.getX();
        double tempY = event.getY();
        if (Math.abs(x1.get() + x.get() - tempX) < 10 && Math.abs(y1.get() + y.get() - tempY) < 10) {
            cursor = POINT.L;
            setCursor(Cursor.HAND);
        } else if (Math.abs(x2.get() + x.get() - tempX) < 10 && Math.abs(y2.get() + y.get() - tempY) < 10) {
            setCursor(Cursor.HAND);
            cursor = POINT.R;
        } else {
            cursor = POINT.C;
            setCursor(Cursor.CROSSHAIR);
        }
        event.consume();
    }

    public void setLineParam(double x,double y,double x1,double y1,double x2,double y2){
        this.x1.set(x1);
        this.x2.set(x2);
        this.y1.set(y1);
        this.y2.set(y2);
        this.x.set(x);
        this.y.set(y);
    }

    /**
     * 设置图形节点为选中样式
     */
    @Override
    public void drawConner() {
        canvas.setMouseTransparent(true);
        isConnerShow = true;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double whiteR = 12;
        double blueR = 10;

        if (x1 != null) {
            double X = x.doubleValue();
            double Y = y.doubleValue();

            gc.setFill(Color.WHITE);
            gc.fillOval(x1.doubleValue() -6+ X, y1.doubleValue() -6+ Y, whiteR, whiteR);
            gc.fillOval(x2.doubleValue() - 6+ X, y2.doubleValue() - 6 + Y, whiteR, whiteR);

            gc.setFill(Color.color(0.03, 0.43, 0.81));
            gc.fillOval(x1.doubleValue() -5 + X, y1.doubleValue() -5 + Y, blueR, blueR);
            gc.fillOval(x2.doubleValue()-5+ X, y2.doubleValue() - 5 + Y, blueR, blueR);
        }


    }

    @Override
    public ProjectSaver getData() {
        ProjectSaver projectSaver = super.getData();
        projectSaver.xStart = x1.doubleValue();
        projectSaver.xEnd = x2.doubleValue();
        projectSaver.yStart = y1.doubleValue();
        projectSaver.yEnd = y2.doubleValue();
        projectSaver.x = x.doubleValue();
        projectSaver.y = y.doubleValue();
        return projectSaver;
    }
}
