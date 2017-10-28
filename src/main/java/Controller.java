import customView.DragBox;
import customView.ImageBox;
import customView.LineBox;
import customView.MainPane;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tools.PictureUploadUtil;
import tools.ProjectSaver;
import tools.SerLine;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Optional;

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
    public Button button_1,button_save;
    public ColorPicker colorPicker_Line;
    public TextArea textArea;
    public Slider slider;
    public Slider slider_rotate;
    public VBox vbox;
    private Stage stage;
    private boolean MousePressed = false;
    static final private int NORMAL = 2;
    static final private int IMAGE = 1;
    static final private int LINE = 3;




    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void start(){

        colorPicker.setValue(Color.GREEN);
        colorPicker_Line.setValue(Color.RED);
        Background background = new Background(new BackgroundFill(Paint.valueOf("#FFF"),null,null));
        mainPane = new MainPane();
        canvas = new Canvas();
        canvas.setHeight(700);
        canvas.setWidth(900);
        mainPane.getChildren().add(canvas);
        vbox.getChildren().add(mainPane);
        mainPane.setBackground(background);
        mainPane.setOnMouseReleased(event -> {
            mainPane.chooseNothing();
            if(mainPane.getBackGround() != null) {
                colorPicker.setValue(mainPane.getBackGround());
            }
        });
        mainPane.setOnMouseClicked(event -> {
            mainPane.requestFocus();
        });

        mainPane.setNodeRequestChoose(node -> {
            mainPane.chooseNothing();
            node.drawConner();
            mainPane.setChoosenNode(node);

            if (node.paintFill != null) {
                colorPicker.setValue(node.paintFill);
            }
            if (node.paintStroke != null) {
                colorPicker_Line.setValue(node.paintStroke);
            }

            slider_rotate.setValue(node.rotate);
            slider.setValue(node.strokeWidth);
        });
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            DragBox choosenNode = mainPane.getChoosenNode();
            if(choosenNode != null){
                if(newValue !=null) {
                    choosenNode.setColor(newValue);
                }
            }else{
//                GraphicsContext gc = canvas.getGraphicsContext2D();
//                gc.setFill(newValue);
//                mainPane.setBackGround(newValue);
//                gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
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
    }

//chenguang
    public void onButtonLoadProjClick(ActionEvent actionEvent) throws ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PhotoScrip file -- PSG (*.psg)","*.psg"));
        File file = fileChooser.showOpenDialog(this.stage);
        ProjectSaver tempObj;
        SerLine tempLine ;
        int tempNum;
        if(file != null) {
            try {
                ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(file));
                mainPane.getChildren().remove(1, mainPane.getChildren().size());
                for(int i = objIn.readInt(); i > 1; i--){
                    tempNum = objIn.readInt();
                    switch (tempNum){
                        case IMAGE:
                            tempObj = (ProjectSaver)objIn.readObject();
                            ImageBox imageBox = tempObj.constructImage();
                            int [] buffer = (int [])objIn.readObject();
                            int k = 0;
                            int tempW = buffer[k++];
                            int tempH = buffer[k++];
                            WritableImage wImage = new WritableImage(tempW,tempH);
                            PixelWriter pixelWriter = wImage.getPixelWriter();
                            for(int x = 0; x < tempW; x++){
                                for(int y = 0; y < tempH; y++){
                                    pixelWriter.setArgb(x, y, buffer[k++]);
                                }
                            }
                            imageBox.setContentNode(new ImageView(), (node, Parent) -> {
                                ((ImageView)node).setImage(wImage);
                                ((ImageView)node).xProperty().set(10);
                                ((ImageView)node).yProperty().set(10);
                                ((ImageView)node).fitWidthProperty().bind(Parent.widthProperty().subtract(20));
                                ((ImageView)node).fitHeightProperty().bind(Parent.heightProperty().subtract(20));
                                mainPane.getChildren().add(Parent);
                                mainPane.setChooseListener(Parent);
                            });
                            break;
                        case NORMAL:
                            tempObj = (ProjectSaver) objIn.readObject();
                            DragBox dragBox = tempObj.construtDragBox();
                            Color fill = new Color(tempObj.fr,tempObj.fg,tempObj.fb,tempObj.fa);
                            Color stroke = new Color(tempObj.r,tempObj.g,tempObj.b,tempObj.a);
                            switch (tempObj.getType()){
                                case ELLIPSE:
                                    circle(dragBox,tempObj.strokeWidth,tempObj.rotate,fill,stroke);
                                    break;
                                case RECT:
                                    rectangle(dragBox,tempObj.strokeWidth,tempObj.rotate,fill,stroke);
                                    break;
                                case SVG:
                                    svg(dragBox, tempObj.svgPath);
                                    break;
                                case TEXT:
                                    textFromFile(dragBox,tempObj.getFontSize(),tempObj.getText());
                                    break;
                            }
                            break;
                        case LINE:
                            tempLine = (SerLine)objIn.readObject();
                            LineBox line = tempLine.constuctLine();
                            mainPane.getChildren().add(line);
                            break;
                    }
                }
                mainPane.chooseNothing();
                objIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    //chenguang
    public void onButtonSaveProjClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PhotoScrip file -- PSG (*.psg)","*.psg"));
        File file = fileChooser.showSaveDialog(this.stage);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeInt(mainPane.getChildren().size());
            for (Node node :
                    mainPane.getChildren()) {
                if(node instanceof ImageBox){
                    objOut.writeInt(IMAGE);
                    objOut.writeObject(((ImageBox)node).getData());
                    objOut.writeObject(((ImageBox)node).getP());
                }else if(node instanceof DragBox){
                    objOut.writeInt(NORMAL);
                    objOut.writeObject(((DragBox)node).getData());
                }else if(node instanceof LineBox){
                    objOut.writeInt(LINE);
                    objOut.writeObject(((LineBox)node).getData());
                }
            }
            objOut.flush();
            objOut.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"文件成功保存在："+file.getAbsolutePath());
            alert.setTitle("保存成功");
            alert.setHeaderText(null);
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,e.getMessage());
            alert.setTitle("保存失败");
            alert.setHeaderText(null);
            alert.showAndWait();
            e.printStackTrace();
        }
    }


    /**
     * 打开一张图片
     * @param event
     */
    public void onButtonOpenImageClick(ActionEvent event) {

        button_1.setDisable(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ALL Images (*.*)","*.*"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG (*.jpg)","*.jpg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG (*.png)","*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP (*.bmp)","*.bmp"));

        File file = fileChooser.showOpenDialog(this.stage);
        button_1.setDisable(false);
        if(file != null) {
            ImageBox imageBox = new ImageBox();
            imageBox.setContentNode(new ImageView(), (node, Parent) -> {
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


    /**
     * 保存当前画板内容
     * @param event
     */
    public void onButtonSaveClick(ActionEvent event) {
        mainPane.chooseNothing();
        button_save.setDisable(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("picture"+System.currentTimeMillis());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG (*.png)","*.png"));
        File file = fileChooser.showSaveDialog(this.stage);
        button_save.setDisable(false);

        snapshot(mainPane,file);
    }

    /**
     * 画一个圆
     * @param event
     */
    public void onButtonCircleClick(ActionEvent event){
        circle(new DragBox(),10,0,colorPicker.getValue(),colorPicker_Line.getValue());
    }

    /**
     * 画一个矩形
     * @param event
     */
    public void onButtonRectangleClick(ActionEvent event) {
        DragBox dragBox = new DragBox();
        rectangle(dragBox,10,0,colorPicker.getValue(),colorPicker_Line.getValue());
    }

    /**
     * 画一个箭头
     * @param actionEvent
     */
    public void onButtonArrowClick(ActionEvent actionEvent) {
        svg("M20,20 120,20 120,0 160,30 120,60 120,40 20,40 z");
    }

    /**
     * 画一个星星
     * @param actionEvent
     */
    public void onButtonStarClick(ActionEvent actionEvent) {
        svg("M50,10 75,90 10,40 90,40 25,90z");
    }

    /**
     * 根据传入的路径信息自定义画贝塞尔曲线
     * @param actionEvent
     */
    public void onButonAutoCLick(ActionEvent actionEvent) {
        svg(textArea.getText());
    }

    // TODO: 2017/10/24 陈广同学不能蒙混过关
    /**
     * 陈广同学还需要再接再厉的画线
     * @param actionEvent
     */
    public void onButtonLineClick(ActionEvent actionEvent) {
        LineBox dragBox = new LineBox();
        mainPane.getChildren()
                .add(dragBox);
//        mainPane.setChooseListener(dragBox);
        dragBox.requestFocus();
    }


    // TODO: 2017/10/24 用户友好地添加文本 
    /**
     * 未完成的画文本
     * @param actionEvent
     */
    public void onButtonTextClick(ActionEvent actionEvent) {
        DragBox dragBox = new DragBox();
        text(dragBox,40,"万众一心aab%");

//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.show
        TextDialog.showAlertDialog("123");
        Path p = new Path();


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

    private void svg(String path){
        DragBox dragBox = new DragBox();
        dragBox.setSvgNode(path,colorPicker.getValue(),colorPicker_Line.getValue());
        mainPane.getChildren()
                .add(dragBox);
        mainPane.setChooseListener(dragBox);
        dragBox.requestFocus();
    }
    private void svg(DragBox dragBox, String path){
        dragBox.setSvgNode(path,null,null);
        mainPane.getChildren().add(dragBox);
        mainPane.setChooseListener(dragBox);
        dragBox.requestFocus();
    }
    private void circle(DragBox dragBox,double strokeWidth,double rotate,Color fill,Color stroke){
        dragBox.setContentNode(new Ellipse(), (node, root) -> {
            ((Ellipse)node).centerXProperty().bind(root.widthProperty().divide(2));
            ((Ellipse)node).centerYProperty().bind(root.heightProperty().divide(2));
            ((Ellipse)node).radiusXProperty().bind(root.widthProperty().divide(2).subtract(10));
            ((Ellipse)node).radiusYProperty().bind(root.heightProperty().divide(2).subtract(10));
            ((Ellipse)node).setSmooth(true);

            root.setLineWidth(strokeWidth);
            root.setNodeRotate(rotate);
            root.setColor(fill);
            root.setLineColor(stroke);
        });
        mainPane.getChildren()
                .add(dragBox);
        mainPane.setChooseListener(dragBox);

        dragBox.requestFocus();
    }
    private void rectangle(DragBox dragBox,double strokeWidth,double rotate,Color fill,Color stroke){
        dragBox.setContentNode(new Rectangle(), (node, root) -> {
            ((Rectangle)node).widthProperty().bind(root.widthProperty().subtract(20));
            ((Rectangle)node).heightProperty().bind(root.heightProperty().subtract(20));
            ((Rectangle)node).layoutXProperty().set(10);
            ((Rectangle)node).layoutYProperty().set(10);
            ((Rectangle)node).setSmooth(true);

            root.setLineWidth(strokeWidth);
            root.setNodeRotate(rotate);
            root.setColor(fill);
            root.setLineColor(stroke);
        });
        mainPane.getChildren()
                .add(dragBox);
        mainPane.setChooseListener(dragBox);
        dragBox.requestFocus();
    }
    private void text(DragBox dragBox,double fontSize,String text){
        dragBox.setContentNode(new Text(), (node, parent) -> {
            ((Text)node).setFont(new Font(fontSize));
            ((Text)node).setText(text);
            parent.setColor(colorPicker.getValue());
            parent.setLineColor(colorPicker_Line.getValue());
            double half=0,whole=0;
            for (char c:
                 text.toCharArray()) {
                if(c<128){
                    half++;
                }else{
                    whole++;
                }
            }

            parent.setPrefWidth(whole*fontSize+half*fontSize/2+20);
            parent.setPrefHeight(fontSize+20);
            parent.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
                double aHalf=0,aWhole=0;
                for (char c:
                        ((Text)node).getText().toCharArray()) {
                    if(c<128){
                        aHalf++;
                    }else{
                        aWhole++;
                    }
                }
                double sizeX = newValue.getWidth()-20;
                sizeX/=(aWhole+0.5*aHalf);
                double sizeY = newValue.getHeight()-20;
                double m = Math.min(sizeX,sizeY);
                ((Text)node).setFont(new Font(m));
                ((Text)node).setX(newValue.getWidth()*0.5-aWhole*m*0.5-aHalf*m*0.25-m*0.1);
                ((Text)node).setY(newValue.getHeight()*0.5 +m*0.5-m*0.1);
            });
        });
        mainPane.getChildren().add(dragBox);
        mainPane.setChooseListener(dragBox);
        dragBox.requestFocus();
    }

    private void textFromFile(DragBox dragBox,double fontSize,String text){
        dragBox.setContentNode(new Text(), (node, parent) -> {

            double half=0,whole=0;
            for (char c:
                    text.toCharArray()) {
                if(c<128){
                    half++;
                }else{
                    whole++;
                }
            }

            parent.setPrefWidth(whole*fontSize+half*fontSize/2+20);
            parent.setPrefHeight(fontSize+20);
            parent.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
                double aHalf=0,aWhole=0;
                for (char c:
                        ((Text)node).getText().toCharArray()) {
                    if(c<128){
                        aHalf++;
                    }else{
                        aWhole++;
                    }
                }
                double sizeX = newValue.getWidth()-20;
                sizeX/=(aWhole+0.5*aHalf);
                double sizeY = newValue.getHeight()-20;
                double m = Math.min(sizeX,sizeY);
                ((Text)node).setFont(new Font(m));
                ((Text)node).setX(newValue.getWidth()*0.5-aWhole*m*0.5-aHalf*m*0.25-m*0.1);
                ((Text)node).setY(newValue.getHeight()*0.5 +m*0.5-m*0.1);
            });
        });
        mainPane.getChildren().add(dragBox);
        mainPane.setChooseListener(dragBox);
    }

    private void snapshot(Node view, File file) {
        Image image = view.snapshot(null, null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "文件成功保存在："+file.getAbsolutePath(),
                    new ButtonType("分享",ButtonBar.ButtonData.LEFT),
                    new ButtonType("返回",ButtonBar.ButtonData.RIGHT)
            );
            alert.setTitle("保存成功");
            alert.setHeaderText(null);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if(buttonType.get().getButtonData().equals(ButtonBar.ButtonData.LEFT)){

                String url = PictureUploadUtil.getUrl(file.getAbsolutePath());
                // TODO: 2017/10/24 使用子线程访问网络，有加载提醒

                alert = new Alert(
                        Alert.AlertType.INFORMATION,
                        url,
                        new ButtonType("复制到剪切板",ButtonBar.ButtonData.YES)
                );
                alert.setTitle("保存成功");
                alert.setHeaderText(null);
                buttonType = alert.showAndWait();
                if(buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)){
                    // TODO: 2017/10/24 复制 url 到剪贴板
                }
            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,e.getMessage());
            alert.setTitle("保存失败");
            alert.setHeaderText(null);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

}
