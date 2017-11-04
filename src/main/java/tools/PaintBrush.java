package tools;

import customView.DragBox;
import customView.MainPane;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Paint;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Pair;

import java.util.ArrayList;

public class PaintBrush {

    Paint paint,paintFill;
    double strokeWidth;
    public void paint(Canvas canvas, ColorPicker colorPicker,
                      ColorPicker colorPickerFill, Slider slider, MainPane mainPane){
        Canvas mCanvas = new Canvas();
        mCanvas.setHeight(canvas.getHeight());
        mCanvas.setWidth(canvas.getWidth());

        double dx = canvas.getLayoutX();
        double dy = canvas.getLayoutY();

        mCanvas.setLayoutY(dy);
        mCanvas.setLayoutX(dx);

        GraphicsContext gc = mCanvas.getGraphicsContext2D();
        ArrayList<Pair<Number,Number>> points = new ArrayList<>();
        int AVG = 5;

        gc.setMiterLimit(5);
        mainPane.getChildren().add(mCanvas);

        mCanvas.setOnMousePressed(e -> {
            paint = colorPicker.getValue();
            paintFill = colorPickerFill.getValue();
            strokeWidth = slider.getValue();

            points.clear();
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.setStroke(paint);
            gc.setLineWidth(strokeWidth);
            gc.beginPath();
            e.consume();
        });
        mCanvas.setOnMouseDragged(e -> {
            points.add(new Pair<>(e.getX()+dx,e.getY()+dy));
            if(points.size() > AVG) {
                double x=0,y=0;
                for(int j=points.size()-AVG;j<points.size();j++){
                    x += points.get(j).getKey().doubleValue();
                    y += points.get(j).getValue().doubleValue();
                }
                gc.lineTo(x/AVG-dx, y/AVG-dy);
            }else{
                gc.lineTo(e.getX(),e.getY());
            }
            gc.stroke();
            e.consume();
        });


        mCanvas.setOnMouseReleased(e ->{
            mainPane.getChildren().remove(mCanvas);

            Path fx_path = new Path();
            fx_path.setSmooth(true);
            if(points.size() < AVG){
                e.consume();
                return;
            }
            StringBuilder path= new StringBuilder();

            fx_path.getElements().add(new MoveTo(points.get(0).getKey().doubleValue(),
                    points.get(0).getValue().doubleValue()));

            path.append(String.format("M%f,%f",points.get(0).getKey().doubleValue(),
                    points.get(0).getValue().doubleValue()));
            for(int i=0;i<points.size()-AVG;i++){
                double x = 0;
                double y = 0;
                for(int j=0+i;j<AVG+i;j++){
                    x += points.get(j).getKey().doubleValue();
                    y += points.get(j).getValue().doubleValue();
                }

                path.append(String.format("L%f,%f", x / AVG, y / AVG));
            }

            DragBox dragBox = new DragBox();
            dragBox.setSvgNode(path.toString(), colorPickerFill.getValue(), colorPicker.getValue(),
                    slider.getValue(),0);
            Bounds bounds = dragBox.node.boundsInLocalProperty().getValue();
            dragBox.X.set(bounds.getMinX()-10);
            dragBox.Y.set(bounds.getMinY()-10);
            mainPane.getChildren()
                    .add(dragBox);
            mainPane.setChooseListener(dragBox);
            mainPane.requestFocus();
            mainPane.setCursor(Cursor.DEFAULT);

            e.consume();
        });


    }


}
