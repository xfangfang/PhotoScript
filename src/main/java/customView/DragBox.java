package customView;
/**
 * Created by FANGs on 2017/9/19.
 */


import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import tools.ProjectSaver;
import tools.SHAPE;

import java.io.Serializable;


public class DragBox extends Pane implements Serializable {

    private POINT cursor = POINT.OT;
    public Node node;
    public Canvas canvas;
    private boolean isShitDown = false;
    private double mouseDragX, mouseDragY;
    protected boolean isMousePress = false;
    private double miniLen = 21;
    public Color paintFill, paintStroke;
    public double rotate = 0, strokeWidth = 0;
    protected MainPane.requestChoose chooseListener;
    private boolean isLoadFromfile, isCopy;
    protected boolean isConnerShow;
    private onDoubleClickListener onDoubleClickListener;


    //leftTop,top,rightTop......Bottom,rightBottom,others
    private enum POINT {
        LT, T, RT, L, R, LB, B, RB, DEFAUT, HAND, OT
    }

    //this view's position
    public DoubleProperty X = new SimpleDoubleProperty(400);
    public DoubleProperty Y = new SimpleDoubleProperty(250);
    protected double deltaX, deltaY;

    //this view's height and width
    public DoubleProperty Width = new SimpleDoubleProperty(0);
    public DoubleProperty Height = new SimpleDoubleProperty(0);

    public DragBox(DragBox dragBox) {
        isCopy = true;
        Width.set(dragBox.getPrefWidth());
        Height.set(dragBox.getPrefHeight());
        X = new SimpleDoubleProperty(dragBox.getLayoutX() + 10);
        Y = new SimpleDoubleProperty(dragBox.getLayoutY() + 10);
        this.rotate = dragBox.rotate;
        this.strokeWidth = dragBox.strokeWidth;
        this.type = dragBox.type;
        this.paintStroke = dragBox.paintStroke;
        this.paintFill = dragBox.paintFill;
        init();
    }

    public DragBox() {
        Width.set(100);
        Height.set(100);
        isCopy = false;
        isLoadFromfile = false;
        init();
    }

    public DragBox(double x, double y, double w, double h, SHAPE.TYPE type, double rotate, double strokenWidth,
                   double a, double r, double g, double b, double fa, double fr, double fg, double fb) {
        Width.set(w);
        Height.set(h);
        X = new SimpleDoubleProperty(x);
        Y = new SimpleDoubleProperty(y);
        this.rotate = rotate;
        this.strokeWidth = strokenWidth;
        this.type = type;
        paintFill = Color.color(fr, fg, fb, fa);
        paintStroke = Color.color(r, g, b, a);
        isLoadFromfile = true;
        isCopy = false;
        init();
    }

    private SHAPE.TYPE type;

    private POINT switchPoint(MouseEvent event) {
        if (!isConnerShow) {
            cursor = POINT.DEFAUT;
            return cursor;
        }
        double x = event.getX();
        double y = event.getY();
        double height = getPrefHeight();
        double width = getPrefWidth();

        cursor = POINT.OT;
        if (y < 10) {
            if (x < 10) cursor = POINT.LT;
            else if (x > width - 10) cursor = POINT.RT;
            else cursor = POINT.T;
            return cursor;
        } else if (y > height - 10) {
            if (x < 10) cursor = POINT.LB;
            else if (x > width - 10) cursor = POINT.RB;
            else cursor = POINT.B;
            return cursor;
        } else if (x < 10) cursor = POINT.L;
        else if (x > width - 10) cursor = POINT.R;
        return cursor;
    }

    private void updateConner() {
        if (!isMousePress) return;
        double min;
        switch (cursor) {
            case LT:
                double tempLTX = getLayoutX() - mouseDragX + getParent().getLayoutX();
                double tempLTY = getLayoutY() - mouseDragY + getParent().getLayoutY();
                if (isShitDown) {
                    isShitDown = true;
                    if (getParent().getLayoutX() + tempLTX < tempLTY) {
                        X.set(X.getValue() - tempLTX - getParent().getLayoutX());
                        Y.set(Y.getValue() - tempLTX - getParent().getLayoutX());
                        setPrefHeight(tempLTX + getPrefWidth());
                        setPrefWidth(tempLTX + getPrefWidth());
                    } else {
                        X.set(X.getValue() - tempLTY);
                        Y.set(Y.getValue() - tempLTY);
                        setPrefHeight(getPrefHeight() + tempLTY);
                        setPrefWidth(getPrefHeight() + tempLTY);
                    }

                } else {
                    X.set(X.getValue() - tempLTX - getParent().getLayoutX());
                    Y.set(Y.getValue() - tempLTY);
                    setPrefHeight(getPrefHeight() + tempLTY);
                    setPrefWidth(tempLTX + getPrefWidth());
                }
                setCursor(Cursor.NW_RESIZE);
                break;
            case T:
                double tempTY = getLayoutY() - mouseDragY + getParent().getLayoutY();
                Y.set(Y.getValue() - tempTY);
                setPrefHeight(getPrefHeight() + tempTY);

                if (isShitDown) {
                    isShitDown = true;
                    setPrefWidth(getPrefHeight() + tempTY);
                }
                setCursor(Cursor.N_RESIZE);
                break;
            case RT:
                double tempRTX = mouseDragX - getLayoutX() - getParent().getLayoutX();
                double tempRTY = getLayoutY() - mouseDragY + getParent().getLayoutY();

                Y.set(Y.getValue() - tempRTY);

                if (isShitDown) {
                    isShitDown = true;
                    setPrefHeight(getPrefHeight() + tempRTY);
                    setPrefWidth(getPrefHeight() + tempRTY);
                } else {
                    setPrefWidth(tempRTX);
                    setPrefHeight(getPrefHeight() + tempRTY);
                }
                setCursor(Cursor.NE_RESIZE);
                break;
            case L:
                double tempLX = getLayoutX() - mouseDragX + getParent().getLayoutX();


                if (isShitDown) {
                    isShitDown = true;
                    X.set(X.getValue() - tempLX);
                    setPrefWidth(tempLX + getPrefWidth());
                    setPrefHeight(tempLX + getPrefWidth());
                } else {
                    X.set(X.getValue() - tempLX);
                    setPrefWidth(tempLX + getPrefWidth());
                }
                setCursor(Cursor.W_RESIZE);
                break;
            case R:
                double tempRX = mouseDragX - getLayoutX() - getParent().getLayoutX();
                setPrefWidth(tempRX);

                if (isShitDown) {
                    isShitDown = true;
                    setPrefHeight(tempRX);
                }
                setCursor(Cursor.E_RESIZE);
                break;
            case LB:
                double tempLBY = mouseDragY - getLayoutY() - getParent().getLayoutY();

                double tempLBX = getLayoutX() - mouseDragX - getParent().getLayoutX();
                X.set(X.getValue() - tempLBX);

                if (isShitDown) {
                    isShitDown = true;
                    setPrefWidth(tempLBX + getPrefWidth());
                    setPrefHeight(tempLBX + getPrefWidth());
                } else {
                    setPrefHeight(tempLBY);
                    setPrefWidth(tempLBX + getPrefWidth());
                }

                setCursor(Cursor.SW_RESIZE);
                break;
            case B:
                double tempBY = mouseDragY - getLayoutY() - getParent().getLayoutY();
                setPrefHeight(tempBY);

                if (isShitDown) {
                    isShitDown = true;
                    setPrefWidth(tempBY);
                }
                setCursor(Cursor.S_RESIZE);
                break;
            case RB:
                double tempX = mouseDragX - getLayoutX() - getParent().getLayoutX();
                double tempY = mouseDragY - getLayoutY() - getParent().getLayoutY();

                if (isShitDown) {
                    min = Math.min(tempX, tempY);
                    isShitDown = true;
                    setPrefWidth(min);
                    setPrefHeight(min);
                } else {
                    setPrefWidth(tempX);
                    setPrefHeight(tempY);
                }
                setCursor(Cursor.SE_RESIZE);
                break;
            default:
                X.set(mouseDragX - deltaX - getParent().getLayoutX());
                Y.set(mouseDragY - deltaY - getParent().getLayoutY());
                break;
        }
    }

    private void switchDrag(MouseEvent event) {
        if (!isConnerShow) return;
        mouseDragX = event.getSceneX();
        mouseDragY = event.getSceneY();
        double min;
        switch (cursor) {
            case LT:
                double tempLTX = getLayoutX() - event.getSceneX() + getParent().getParent().getLayoutX();
                double tempLTY = getLayoutY() - event.getSceneY() + getParent().getLayoutY() +
                        getParent().getParent().getParent().getLayoutY();

                if (event.isShiftDown()) {
                    isShitDown = true;
                    if (getParent().getLayoutX() + tempLTX < tempLTY) {
                        if (tempLTX + getPrefWidth() < miniLen) {
                            X.set(X.getValue() + getPrefWidth() - miniLen);
                            Y.set(Y.getValue() + getPrefWidth() - miniLen);
                            setPrefHeight(miniLen);
                            setPrefWidth(miniLen);
                        } else {
                            X.set(X.getValue() - tempLTX);
                            Y.set(Y.getValue() - tempLTX);
                            setPrefHeight(tempLTX + getPrefWidth());
                            setPrefWidth(tempLTX + getPrefWidth());
                        }
                    } else {
                        if (getPrefHeight() + tempLTY < miniLen) {
                            X.set(X.getValue() + getPrefHeight() - miniLen);
                            Y.set(Y.getValue() + getPrefHeight() - miniLen);
                            setPrefHeight(miniLen);
                            setPrefWidth(miniLen);
                        } else {
                            setPrefHeight(getPrefHeight() + tempLTY);
                            Y.set(Y.getValue() - tempLTY);
                            setPrefWidth(getPrefWidth() + tempLTY);
                            X.set(X.getValue() - tempLTY);
                        }
                    }

                } else {
                    if (tempLTX + getPrefWidth() < miniLen) {
                        X.set(X.getValue() + getPrefWidth() - miniLen);
                        setPrefWidth(miniLen);
                    } else {
                        X.set(X.getValue() - tempLTX);
                        setPrefWidth(tempLTX + getPrefWidth());
                    }
                    if (getPrefHeight() + tempLTY < miniLen) {
                        Y.set(Y.getValue() + getPrefHeight() - miniLen);
                        setPrefHeight(miniLen);
                    } else {
                        Y.set(Y.getValue() - tempLTY);
                        setPrefHeight(getPrefHeight() + tempLTY);
                    }
                }
                setCursor(Cursor.NW_RESIZE);
                break;
            case T:
                double tempTY = getLayoutY() - event.getSceneY() + getParent().getLayoutY() +
                        getParent().getParent().getParent().getLayoutY();

                if (getPrefHeight() + tempTY < miniLen) {
                    Y.set(Y.getValue() + getPrefHeight() - miniLen);
                    setPrefHeight(miniLen);
                } else {
                    Y.set(Y.getValue() - tempTY);
                    setPrefHeight(getPrefHeight() + tempTY);
                }

                if (event.isShiftDown()) {
                    isShitDown = true;
                    setPrefWidth(keepMiniLen(getPrefHeight() + tempTY));
                }

                setCursor(Cursor.N_RESIZE);
                break;
            case RT:
                double tempRTX = event.getSceneX() - getLayoutX() - getParent().getParent().getLayoutX();
                double tempRTY = getLayoutY() - event.getSceneY() + getParent().getLayoutY() +
                        getParent().getParent().getParent().getLayoutY();


                if (event.isShiftDown()) {
                    isShitDown = true;
                    if (getPrefHeight() + tempRTY < miniLen) {
                        Y.set(Y.getValue() + (getPrefHeight() - miniLen));
                        setPrefHeight(miniLen);
                        setPrefWidth(miniLen);
                    } else {
                        Y.set(Y.getValue() - tempRTY);
                        setPrefHeight(getPrefHeight() + tempRTY);
                        setPrefWidth(getPrefHeight() + tempRTY);
                    }
                } else {
                    setPrefWidth(keepMiniLen(tempRTX));
                    if (getPrefHeight() + tempRTY < miniLen) {
                        Y.set(Y.getValue() + (getPrefHeight() - miniLen));
                        setPrefHeight(miniLen);
                    } else {
                        Y.set(Y.getValue() - tempRTY);
                        setPrefHeight(getPrefHeight() + tempRTY);
                    }
                }
                setCursor(Cursor.NE_RESIZE);
                break;
            case L:
                double tempLX = getLayoutX() - event.getSceneX() + getParent().getParent().getLayoutX();


                if (event.isShiftDown()) {
                    isShitDown = true;

                    if (tempLX + getPrefWidth() < miniLen) {
                        X.set(X.getValue() + (getPrefWidth() - miniLen));
                        setPrefWidth(miniLen);
                        setPrefHeight(miniLen);

                    } else {
                        X.set(X.getValue() - tempLX);
                        setPrefWidth(tempLX + getPrefWidth());
                        setPrefHeight(tempLX + getPrefWidth());
                    }
                } else {
                    if (tempLX + getPrefWidth() < miniLen) {
                        X.set(X.getValue() + (getPrefWidth() - miniLen));
                        setPrefWidth(miniLen);
                    } else {
                        X.set(X.getValue() - tempLX);
                        setPrefWidth(tempLX + getPrefWidth());
                    }
                }
                setCursor(Cursor.W_RESIZE);
                break;
            case R:
                double tempRX = event.getSceneX() - getLayoutX() - getParent().getParent().getLayoutX();
                setPrefWidth(keepMiniLen(tempRX));

                if (event.isShiftDown()) {
                    isShitDown = true;
                    setPrefHeight(keepMiniLen(tempRX));
                }
                setCursor(Cursor.E_RESIZE);
                break;
            case LB:
                double tempLBY = event.getY();
                double tempLBX = getLayoutX() - event.getSceneX() + getParent().getParent().getLayoutX();

                if (event.isShiftDown()) {
                    isShitDown = true;

                    if (tempLBX + getPrefWidth() < miniLen) {
                        X.set(X.getValue() + (getPrefWidth() - miniLen));
                        setPrefWidth(miniLen);
                        setPrefWidth(miniLen);
                    } else {
                        X.set(X.getValue() - tempLBX);
                        setPrefWidth(tempLBX + getPrefWidth());
                        setPrefHeight(tempLBX + getPrefWidth());
                    }
                } else {
                    setPrefHeight(keepMiniLen(tempLBY));
                    if (tempLBX + getPrefWidth() < miniLen) {
                        X.set(X.getValue() + (getPrefWidth() - miniLen));
                        setPrefWidth(miniLen);
                    } else {
                        X.set(X.getValue() - tempLBX);
                        setPrefWidth(tempLBX + getPrefWidth());
                    }
                }

                setCursor(Cursor.SW_RESIZE);
                break;
            case B:
                double tempBY = event.getY();
                setPrefHeight(keepMiniLen(tempBY));

                if (event.isShiftDown()) {
                    isShitDown = true;
                    setPrefWidth(keepMiniLen(tempBY));
                }
                setCursor(Cursor.S_RESIZE);
                break;
            case RB:
                double tempX = event.getX();
                double tempY = event.getY();

                if (event.isShiftDown()) {
                    min = Math.min(tempX, tempY);
                    isShitDown = true;
                    setPrefWidth(keepMiniLen(min));
                    setPrefHeight(keepMiniLen(min));
                } else {
                    setPrefWidth(keepMiniLen(tempX));
                    setPrefHeight(keepMiniLen(tempY));
                }
                setCursor(Cursor.SE_RESIZE);
                break;
            default:
                X.set(event.getSceneX() - deltaX - getParent().getParent().getLayoutX());
                Y.set(event.getSceneY() - deltaY - getParent().getParent().getParent().getLayoutY() - getParent().getLayoutY());
                setCursor(Cursor.CLOSED_HAND);
                break;
        }
    }

    private double keepMiniLen(double l) {
        if (l < miniLen) {
            l = miniLen;
        }
        return l;
    }

    private void setCursor(MouseEvent event) {
        switch (switchPoint(event)) {
            case LT:
                setCursor(Cursor.NW_RESIZE);
                break;
            case T:
                setCursor(Cursor.N_RESIZE);
                break;
            case RT:
                setCursor(Cursor.NE_RESIZE);
                break;
            case L:
                setCursor(Cursor.W_RESIZE);
                break;
            case R:
                setCursor(Cursor.E_RESIZE);
                break;
            case LB:
                setCursor(Cursor.SW_RESIZE);
                break;
            case B:
                setCursor(Cursor.S_RESIZE);
                break;
            case RB:
                setCursor(Cursor.SE_RESIZE);
                break;
            default:
                setCursor(Cursor.DEFAULT);
                break;
        }
    }


    /**
     * 向DragBox添加一般性节点时调用
     *
     * @param node            一般节点
     * @param onBuildListener 在构建节点时使用监听器可以使代码更加简洁
     */
    public void setContentNode(Node node, OnBuildListener onBuildListener) {
        this.node = node;
        getChildren().add(0, node);

        if (onBuildListener != null) {
            onBuildListener.onBuild(node, this);
        }
        if (this.node instanceof Ellipse) {
            this.type = SHAPE.TYPE.ELLIPSE;
        } else if (this.node instanceof Rectangle) {
            this.type = SHAPE.TYPE.RECT;
        } else if (this.node instanceof Text) {
            this.type = SHAPE.TYPE.TEXT;
        } else if (this.node instanceof SVGPath) {
            this.type = SHAPE.TYPE.SVG;
        } else if (this.node instanceof Line) {
            this.type = SHAPE.TYPE.LINE;
        }
        initNode();
    }

    private String svgPath;

    /**
     * 在添加svg型节点时调用
     *
     * @param path   svg节点的字符信息
     * @param fill   svg节点的填充信息
     * @param stroke svg节点的边框填充信息
     */
    public void setSvgNode(String path, Paint fill, Paint stroke, double strokeWidth, double rotate) {
        this.node = new SVGPath();
        svgPath = path;
        ((SVGPath) node).setContent(path);
        ((SVGPath) node).setSmooth(true);
        ((SVGPath) node).setStrokeLineCap(StrokeLineCap.ROUND);


        Bounds bounds = node.boundsInLocalProperty().getValue();

        final double height = bounds.getHeight();
        final double width = bounds.getWidth();
        double S = 20;


        if (isLoadFromfile || isCopy) {
            setPrefWidth(Width.doubleValue());
            setPrefHeight(Height.doubleValue());
        } else {
            setPrefWidth(100 * width / height);
            this.type = SHAPE.TYPE.SVG;
            setPrefWidth(width + S);
            setPrefHeight(height + S);
        }

        node.scaleYProperty().bind(heightProperty().
                subtract(S).
                divide(height));
        node.scaleXProperty().bind(widthProperty().
                subtract(S).
                divide(width));

        node.layoutXProperty().bind(widthProperty().
                subtract(S).
                divide(2).
                subtract(width / 2).
                add(S / 2).
                subtract(bounds.getMinX()));
        node.layoutYProperty().bind(heightProperty().
                subtract(S).
                divide(2).
                subtract(height / 2).
                add(S / 2)
                .subtract(bounds.getMinY()));
        getChildren().add(0, node);
        setColor((Color) fill);
        setLineColor((Color) stroke);
        setLineWidth(strokeWidth);
        setNodeRotate(rotate);
        initNode();
    }

    /**
     * 在向DragBox添加子节点时调用
     * 使代码简洁易懂
     */
    public interface OnBuildListener {
        void onBuild(Node node, DragBox Parent);
    }

    /**
     * 添加鼠标键盘相关的监听器
     */
    protected void setKeyAndMouseListener() {
        setOnMouseMoved(this::setCursor);


        setOnMousePressed(event -> {
            if (!isConnerShow) return;
            if (chooseListener != null) {
                chooseListener.request(this);
            }
            drawConner();
            deltaX = event.getX();
            deltaY = event.getY();
            isMousePress = true;
            event.consume();
        });


        setOnMouseDragged(this::switchDrag);

        setOnMouseReleased(event -> {
            isMousePress = false;
            event.consume();
            setCursor(event);
        });


        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                isShitDown = true;
                updateConner();
            }
        });

        setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                isShitDown = false;
                updateConner();
            }
        });
    }

    /**
     * 初始化DragBox的基础参数和设置各种监听函数
     */
    protected void init() {

        setPrefWidth(Width.get());
        setPrefHeight(Height.get());
        layoutXProperty().bind(X);
        layoutYProperty().bind(Y);


        layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double h = heightProperty().getValue();
            double w = widthProperty().getValue();

            Width.set(w);
            Height.set(h);
        });

        setKeyAndMouseListener();
        initCanvas();
    }

    /**
     * 初始化背景绘画节点
     */
    protected void initCanvas() {
        canvas = new Canvas();
        //在Canvas高宽改变的时候，重新绘制
        canvas.heightProperty().addListener(observable -> {
            clearConner();
            drawConner();
        });
        canvas.widthProperty().addListener(observable -> {
            clearConner();
            drawConner();
        });

        //canvas的高宽于box的高宽绑定在一起
        canvas.widthProperty().bind(Width);
        canvas.heightProperty().bind(Height);
        getChildren().add(canvas);

        //无论Canvas接收到什么鼠标事件都传递给Box处理
        canvas.setMouseTransparent(true);
    }


    /**
     * 双击监听
     */
    public interface onDoubleClickListener {
        void onDoubleClick(DragBox root, Node node);
    }

    public void setOnDoubleClickListener(onDoubleClickListener listener) {
        this.onDoubleClickListener = listener;
    }

    /**
     * 初始化节点的鼠标事件监听函数
     */
    protected void initNode() {

        node.setOnMouseMoved(event -> {
            setCursor(Cursor.HAND);
            cursor = POINT.OT;
            event.consume();
        });
        node.setOnMousePressed(event -> {
            if (chooseListener != null) {
                chooseListener.request(this);
            }
            drawConner();
            deltaX = event.getX();
            deltaY = event.getY();
            isMousePress = true;

        });
        canvas.setOnMouseClicked((MouseEvent event) -> {
            System.out.println(type + " node click");
            if (event.getClickCount() == 2) {
                System.out.println(type + " node click twice");
                if (null != onDoubleClickListener) {
                    onDoubleClickListener.onDoubleClick(this, node);
                }
            }
        });

    }


    /**
     * 设置图形节点为选中样式
     */
    public void drawConner() {
        canvas.setMouseTransparent(false);
        if (node != null) {
            node.setMouseTransparent(true);
        }
        isConnerShow = true;
//        System.out.println("draw conner");
        double height = getPrefHeight();
        double width = getPrefWidth();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double whiteR = 12;
        double blueR = 10;
        double delta = (whiteR - blueR) / 2;
        double lineStart = whiteR / 2;

        gc.setStroke(Color.GREEN);
        gc.setLineWidth(2);

        gc.strokeLine(lineStart, lineStart, width - lineStart, lineStart);
        gc.strokeLine(lineStart, height - lineStart, width - lineStart, height - lineStart);
        gc.strokeLine(lineStart, lineStart, lineStart, height - lineStart);
        gc.strokeLine(width - lineStart, lineStart, width - lineStart, height - lineStart);


        gc.setFill(Color.WHITE);
        gc.fillOval(0, 0, whiteR, whiteR);
        gc.fillOval(width - whiteR, 0, whiteR, whiteR);
        gc.fillOval(0, height - whiteR, whiteR, whiteR);
        gc.fillOval(width - whiteR, height - whiteR, whiteR, whiteR);

        gc.setFill(Color.color(0.03, 0.43, 0.81));
        gc.fillOval(delta, delta, blueR, blueR);
        gc.fillOval(width - blueR - delta, delta, blueR, blueR);
        gc.fillOval(delta, height - blueR - delta, blueR, blueR);
        gc.fillOval(width - blueR - delta, height - blueR - delta, blueR, blueR);
    }

    /**
     * 设置图形节点为未选中样式
     */
    public void clearConner() {
        canvas.setMouseTransparent(true);
        if (node != null) {
            node.setMouseTransparent(false);
        }
        isConnerShow = false;
        double height = getPrefHeight();
        double width = getPrefWidth();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
    }


    /**
     * 添加选中监听器，并默认添加选中
     *
     * @param listener
     */
    public void setChooseListener(MainPane.requestChoose listener) {
        this.chooseListener = listener;
        this.chooseListener.request(this);
    }

    /**
     * 设置图形节点填充颜色
     *
     * @param value
     */
    public void setColor(Color value) {
        this.paintFill = value;
        if (this.node instanceof Ellipse) {
            ((Ellipse) this.node).setFill(value);
        } else if (this.node instanceof Rectangle) {
            ((Rectangle) this.node).setFill(value);
        } else if (this.node instanceof SVGPath) {
            ((SVGPath) this.node).setFill(value);
        } else if (this.node instanceof Text) {
            ((Text) this.node).setFill(value);
        } else if (this.node instanceof Line) {
            ((Line) this.node).setFill(value);
        }
    }

    /**
     * 设置图形节点外框颜色
     *
     * @param value
     */
    public void setLineColor(Color value) {
        this.paintStroke = value;
        if (this.node instanceof Ellipse) {
            ((Ellipse) this.node).setStroke(value);
        } else if (this.node instanceof Rectangle) {
            ((Rectangle) this.node).setStroke(value);
        } else if (this.node instanceof SVGPath) {
            ((SVGPath) this.node).setStroke(value);
        } else if (this.node instanceof Text) {
            ((Text) this.node).setStroke(value);
        } else if (this.node instanceof Line) {
            ((Line) this.node).setStroke(value);
        }
    }

    /**
     * 设置图形节点外框宽度
     *
     * @param lineWidth
     */
    public void setLineWidth(double lineWidth) {
        this.strokeWidth = lineWidth;
        if (this.node instanceof Ellipse) {
            ((Ellipse) this.node).setStrokeWidth(lineWidth);
        } else if (this.node instanceof Rectangle) {
            ((Rectangle) this.node).setStrokeWidth(lineWidth);
        } else if (this.node instanceof SVGPath) {
            ((SVGPath) this.node).setStrokeWidth(lineWidth);
        } else if (this.node instanceof Text) {
            ((Text) this.node).setStrokeWidth(lineWidth);
        } else if (this.node instanceof Line) {
            ((Line) this.node).setStrokeWidth(lineWidth);
        }
    }

    /**
     * 设置图形节点旋转角度
     *
     * @param rotate
     */
    public void setNodeRotate(double rotate) {
        this.rotate = rotate;
        if (node instanceof Line) {
            return;
        }
        this.node.setRotate(rotate);
    }

    /**
     * 用于序列化储存
     *
     * @return 返回内部节点的所有参数
     */
    public ProjectSaver getData() {
        double a = 0, r = 0, g = 0, b = 0, fa = 0, fr = 0, fg = 0, fb = 0;
        if (paintStroke != null) {
            a = paintStroke.getOpacity();
            r = paintStroke.getRed();
            g = paintStroke.getGreen();
            b = paintStroke.getBlue();
        }
        if (paintFill != null) {
            fa = paintFill.getOpacity();
            fr = paintFill.getRed();
            fg = paintFill.getGreen();
            fb = paintFill.getBlue();
        }

        ProjectSaver projectSaver = new ProjectSaver(getLayoutX(), getLayoutY(), getPrefWidth(), getPrefHeight(),
                this.type, a, r, g, b, fa, fr, fg, fb, this.rotate, this.strokeWidth);

        if (node instanceof SVGPath) {
            projectSaver.setSvgPath(svgPath);
        } else if (node instanceof Text) {
            projectSaver.setTextAndFont(((Text) node).getText(), ((Text) node).getFont().getSize(), ((Text) node).getFont().getName());
        }

        return projectSaver;
    }

    /**
     * 重写自带方法实现鼠标事件的顺序传递
     *
     * @param pickRay
     * @param result
     */
    @Override
    protected void impl_pickNodeLocal(PickRay pickRay, PickResultChooser result) {
        double boundsDistance = impl_intersectsBounds(pickRay);

        if (!Double.isNaN(boundsDistance)) {
            ObservableList<Node> children = getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                children.get(i).impl_pickNode(pickRay, result);
                if (result.isClosed()) {
                    return;
                }
            }

//            impl_intersects(pickRay, result);
        }
    }
}
