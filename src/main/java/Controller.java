import custonView.DragBox;
import custonView.MainPane;
import custonView.shape.Ellipse;
import custonView.shape.OurImage;
import custonView.shape.OurLine;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
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
    public TextArea textArea;
    public Slider slider;
    public Slider slider_rotate;
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

        colorPicker.setValue(Color.TRANSPARENT);
        colorPicker_Line.setValue(Color.BLACK);
        Background background = new Background(new BackgroundFill(Paint.valueOf("#FFF"),null,null));
        mainPane.setBackground(background);
        mainPane.setNodeRequestChoose(node -> {
                for (Node i :
                        mainPane.getChildren()) {
                    if(i instanceof DragBox){
                        ((DragBox) i).setNotChoosen();
                    }
                }
                node.setChoosen();
                mainPane.setChoosenNode(node);

                if(node.paintFill != null) {
                    colorPicker.setValue(node.paintFill);
                }
                if(node.paintStroke != null) {
                    colorPicker_Line.setValue(node.paintStroke);
                }

                slider_rotate.setValue(node.rotate);
                slider.setValue(node.strokenWidth);
        });
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            DragBox choosenNode = mainPane.getChoosenNode();
            if(choosenNode != null){
                if(newValue !=null) {
                    choosenNode.setColor(newValue);
                }
            }else{
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(newValue);
                gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
            }
        });
        colorPicker_Line.valueProperty().addListener((observable, oldValue, newValue) -> {
            DragBox choosenNode = mainPane.getChoosenNode();
            if(choosenNode != null && newValue !=null){
                choosenNode.setLineColor(newValue);
            }
        });

        slider.setMax(20);
        slider.setMin(1);
        slider.setValue(5);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            mainPane.setLineWidth(newValue.doubleValue());
        });

        slider_rotate.setMax(180);
        slider_rotate.setMin(-180);
        slider_rotate.setValue(0);

        slider_rotate.valueProperty().addListener((observable, oldValue, newValue) -> {
            mainPane.setNodeRotate(newValue.doubleValue());
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

    private void writeObjectToFile(Object obj) {
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
            OurImage dragBox = new OurImage();
            dragBox.setContentNode(new ImageView(), (node, Parent) -> {
                try {
                    ((ImageView)node).setImage(new Image(new FileInputStream(file)));
                    ((ImageView)node).xProperty().set(10);
                    ((ImageView)node).yProperty().set(10);

                    ((ImageView)node).fitWidthProperty().bind(Parent.widthProperty().subtract(20));
                    ((ImageView)node).fitHeightProperty().bind(Parent.heightProperty().subtract(20));

                    mainPane.getChildren().add(Parent);
                    mainPane.setChooseListener(Parent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
        mainPane.requestFocus();
    }

    public void onButtonSaveClick(ActionEvent event) {
        mainPane.chooseNothing();
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
            ((Ellipse)node).setStroke(colorPicker_Line.getValue());
            ((Ellipse)node).setStrokeWidth(5);
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
            ((Rectangle)node).setStroke(colorPicker_Line.getValue());
            ((Rectangle)node).setStrokeWidth(5);
            ((Rectangle)node).setSmooth(true);
            root.setPrefWidth(200);
        });
        mainPane.getChildren()
                .add(dragBox);
        mainPane.setChooseListener(dragBox);
        dragBox.requestFocus();
    }

    public void onButtonArrowClick(ActionEvent actionEvent) {
        addSvg("M20,20 120,20 120,0 160,30 120,60 120,40 20,40 z");
    }

    public void onButtonStarClick(ActionEvent actionEvent) {
        addSvg("M50,10 75,90 10,40 90,40 25,90z");
    }

    public void onButonAutoCLick(ActionEvent actionEvent) {
        addSvg(textArea.getText());
    }

    public void onBoardClicked(MouseEvent mouseEvent) {
        mainPane.requestFocus();
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


    private void addSvg(String path){
        DragBox dragBox = new DragBox();
        dragBox.setSvgNode(path,colorPicker.getValue(),colorPicker_Line.getValue());
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

    public void onButtonLineClick(ActionEvent actionEvent) {
        OurLine dragBox = new OurLine();
        mainPane.getChildren()
                .add(dragBox);
//        mainPane.setChooseListener(dragBox);
        dragBox.requestFocus();
    }
}
