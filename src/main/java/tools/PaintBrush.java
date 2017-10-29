package tools;

import customView.DragBox;
import customView.MainPane;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Pair;

import java.util.ArrayList;

public class PaintBrush {

    Paint paint;
    double strokeWidth;
    public void paint(Canvas canvas, ColorPicker colorPicker, Slider slider, MainPane mainPane){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ArrayList<Pair<Number,Number>> points = new ArrayList<>();


        canvas.setOnMousePressed(e -> {
            paint = colorPicker.getValue();
            strokeWidth = slider.getValue();

            points.clear();
            gc.setStroke(paint);
            gc.setLineWidth(strokeWidth);
            gc.beginPath();
            e.consume();
        });
        canvas.setOnMouseDragged(e -> {
            points.add(new Pair<>(e.getX(),e.getY()));
            gc.lineTo(e.getX(),e.getY());
            gc.stroke();
            e.consume();

        });


        canvas.setOnMouseReleased(e ->{
            Path fx_path = new Path();
            fx_path.setSmooth(true);
            boolean first = true;
            for (Pair<Number, Number> i :
                    points) {
                if(first){
                    first = false;
                    fx_path.getElements().add(new MoveTo(i.getKey().doubleValue(),i.getValue().doubleValue()));
                }else {
                    fx_path.getElements().add(new LineTo(i.getKey().doubleValue(), i.getValue().doubleValue()));
                }
            }
            DragBox box = new DragBox();

            box.setContentNode(fx_path, (node, root) -> {
                Bounds bounds = ((Path)node).boundsInLocalProperty().getValue();
                final double height = bounds.getHeight();
                final double width = bounds.getWidth();
                double S = 20;


                root.X.set(bounds.getMinX()-10);
                root.Y.set(bounds.getMinY()-10);
                root.setPrefWidth(width+S);
                root.setPrefHeight(height+S);

                node.scaleYProperty().bind(root.heightProperty().
                        subtract(S).
                        divide(height));
                node.scaleXProperty().bind(root.widthProperty().
                        subtract(S).
                        divide(width));

                node.layoutXProperty().bind(root.widthProperty().
                        subtract(S).
                        divide(2).
                        subtract(width/2).
                        add(S/2).
                        subtract(bounds.getMinX()));
                node.layoutYProperty().bind(root.heightProperty().
                        subtract(S).
                        divide(2).
                        subtract(height/2).
                        add(S/2)
                        .subtract(bounds.getMinY()));
                root.setLineColor((Color)paint);
                root.setLineWidth(strokeWidth);

                mainPane.getChildren().add(box);
                mainPane.setChooseListener(box);
//                root.clearConner();
            });
            gc.clearRect(0,0,1000,700);

            canvas.setOnMousePressed(null);
            canvas.setOnMouseDragged(null);
            canvas.setOnMouseReleased(null);
            e.consume();
        });


    }


}
