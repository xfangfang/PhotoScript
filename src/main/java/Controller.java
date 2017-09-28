import custonView.DragBox;
import custonView.MainPane;
import custonView.shape.Ellipse;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;

public class Controller {

    public Canvas canvas;
    public Button button_circle;
    public MainPane mainPane;
    public ColorPicker colorPicker;
    public Button button_rectangle;
    public Button button_up;
    public Button button_down;
    public Button button_top;
    public Button button_bottom;
    public ColorPicker colorPicker_Line;
    private Stage stage;
    private boolean MousePressed = false;
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private FileChooser fileChooser = new FileChooser();



    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void start(){
        this.directoryChooser.setTitle("选择保存目录");
        this.fileChooser.setTitle("打开图片");

        colorPicker.setValue(Color.BLACK);
        Background background = new Background(new BackgroundFill(Paint.valueOf("#FFF"),null,null));
        mainPane.setBackground(background);
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            DragBox choosenNode = mainPane.getChoosenNode();
            if(choosenNode != null){
                choosenNode.setColor(newValue.saturate());
            }
        });
    }

    public void stop(){
//        ObservableList<Node> nodes = mainPane.getChildren();
//        for (Node i :
//                nodes) {
//            System.out.println(i.getLayoutX() +" "+i.getLayoutY() +" ");
//            writeObjectToFile(i);
//        }
    }

    private void writeObjectToFile(Object obj)
    {
        File file =new File("test.psg");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        } catch (IOException e) {
            System.out.println("write object failed");
            e.printStackTrace();
        }
    }

    public Button button_1,button_save;

    public void onButtonClick(ActionEvent event) {

        button_1.setDisable(true);
        File file = fileChooser.showOpenDialog(this.stage);
        button_1.setDisable(false);
        if(file != null) {
//            drawPicture(file);
            DragBox dragBox = new DragBox();
            dragBox.setContentNode(new ImageView(), (node, Parent) -> {
                try {
                    ((ImageView)node).setImage(new Image(new FileInputStream(file)));
                    ((ImageView)node).xProperty().set(10);
                    ((ImageView)node).yProperty().set(10);

                    ((ImageView)node).fitWidthProperty().bind(Parent.widthProperty().subtract(20));
                    ((ImageView)node).fitHeightProperty().bind(Parent.heightProperty().subtract(20));

                    mainPane.getChildren().add(dragBox);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void onButtonSaveClick(ActionEvent event) {
        button_save.setDisable(true);
        File file = directoryChooser.showDialog(this.stage);
        button_save.setDisable(false);
        if(file != null) {
            snapshot(mainPane,new File(file.getAbsolutePath() +File.separator + System.currentTimeMillis() + ".png"));
        }
    }


    public void onButtonCircleClick(ActionEvent event){
        DragBox dragBox = new DragBox();

        dragBox.setContentNode(new Ellipse(), (node, root) -> {
            ((Ellipse)node).centerXProperty().bind(root.widthProperty().divide(2));
            ((Ellipse)node).centerYProperty().bind(root.heightProperty().divide(2));
            ((Ellipse)node).radiusXProperty().bind(root.widthProperty().divide(2).subtract(10));
            ((Ellipse)node).radiusYProperty().bind(root.heightProperty().divide(2).subtract(10));
            ((Ellipse)node).setSmooth(true);
            ((Ellipse)node).setFill(colorPicker.getValue());
        });
        mainPane.getChildren()
                .add(dragBox);
        mainPane.setChooseListener(dragBox);

        dragBox.requestFocus();

    }

    public void onButtonRectangleClick(ActionEvent event) {
        DragBox dragBox = new DragBox();
        dragBox.setContentNode(new Rectangle(), (node, root) -> {
            ((Rectangle)node).widthProperty().bind(root.widthProperty().subtract(20));
            ((Rectangle)node).heightProperty().bind(root.heightProperty().subtract(20));
            ((Rectangle)node).layoutXProperty().set(10);
            ((Rectangle)node).layoutYProperty().set(10);
            ((Rectangle)node).setFill(colorPicker.getValue());
            ((Rectangle)node).setSmooth(true);
            root.setPrefWidth(200);
        });
        mainPane.getChildren()
                .add(dragBox);
        mainPane.setChooseListener(dragBox);
        dragBox.requestFocus();
    }

    private void snapshot(Node view, File file) {
        Image image = view.snapshot(null, null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("保存成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawPicture(File file){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            gc.drawImage(new Image(inputStream),0,0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void drawShapes() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(0, 0, 900, 0);
        gc.strokeLine(0, 500, 900, 500);
        gc.strokeLine(0, 0, 0, 500);
        gc.strokeLine(900, 0, 900, 500);


        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }

    public void onBoardClicked(MouseEvent mouseEvent) {
        mainPane.requestFocus();
    }


    public void onButtonArrowClick(ActionEvent actionEvent) {
        DragBox dragBox = new DragBox();
        dragBox.setContentNode(new SVGPath(), (node, root) -> {
            ((SVGPath)node).contentProperty().setValue("123");
            ((SVGPath)node).layoutXProperty().set(10);
            ((SVGPath)node).layoutYProperty().set(10);
            ((SVGPath)node).setFill(colorPicker.getValue());
            ((SVGPath)node).setSmooth(true);
            root.setPrefWidth(200);
        });
        mainPane.getChildren()
                .add(dragBox);
        mainPane.setChooseListener(dragBox);
        dragBox.requestFocus();
    }

    public void onButtonUpClick(ActionEvent actionEvent) {
        mainPane.up();
    }

    public void onButtonDowneClick(ActionEvent actionEvent) {
        mainPane.down();

    }

    public void onButtonTopClick(ActionEvent actionEvent) {
        mainPane.toTop();

    }

    public void onButtonBottomClick(ActionEvent actionEvent) {
        mainPane.toBottom();

    }
}
