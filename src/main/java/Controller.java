import customView.DragBox;
import customView.DragBoxWithLine;
import customView.ImageBox;
import customView.MainPane;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import tools.*;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

import static javafx.stage.StageStyle.DECORATED;

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
    public Button button_paint;
    public Button button_text;
    public Button button_pic;
    public Button button_line;
    public Button button_save;
    public Button button_arrow;
    public ColorPicker colorPicker_Line;
    public Slider slider;
    public Slider slider_rotate;
    public ImageView pen_image;
    public VBox vbox;
    public FlowPane pane1;
    public Label labelHint;
    public Button button_mini;
    public FlowPane btnFlowPane;
    public FlowPane toolFlowPane;
    private Stage stage;
    public FontChooserController fontChooserController;
    ArrayList<Pair<String, SvgData>> svgData;
    public SizeChooser sizeChooser;
    private boolean MousePressed = false;
    static final private int NORMAL = 2;
    static final private int IMAGE = 1;
    static final private int LINE = 3;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void initCustomShape() throws FileNotFoundException {
        svgData = new ArrayList<>();
        try {
            String[] fileName = {
                    "basic","arrow","dialog_balloon","flowchart","game","music","nature","object",
                    "social","symbol","ui","weather"
            };
            for (String i :
                    fileName) {
                InputStream is=this.getClass().getResourceAsStream(String.format("svg_data/%s.json",i));
                BufferedReader br=new BufferedReader(new InputStreamReader(is));
                StringBuilder data= new StringBuilder();
                String s;
                while((s=br.readLine())!=null){
                    data.append(s);
                }
                SvgData item = IOUtils.getObjectData(data.toString(), SvgData.class);
                svgData.add(new Pair<>(i, item));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 程序启动时加载此方法
     */
    public void start() throws FileNotFoundException {
        initCustomShape();
        colorPicker.setValue(Color.BISQUE);
        colorPicker_Line.setValue(Color.BLACK);
        Background background = new Background(new BackgroundFill(Paint.valueOf("#535353"), null, null));
        Background background2 = new Background(new BackgroundFill(Paint.valueOf("#bfbfbf"), null, null));
        Background background3 = new Background(new BackgroundFill(Paint.valueOf("#3c3f41"), null, null));


        setCanvasSize();


        pane1.setBackground(background);
        vbox.setBackground(background2);
        btnFlowPane.setBackground(background);

        mainPane.setOnMousePressed(event -> {
            System.out.println("main press");
            mainPane.chooseNothing();
            labelHint.setText("");
        });

        vbox.setOnMousePressed(event -> {
            mainPane.chooseNothing();
            labelHint.setText("");
        });



        mainPane.setNodeRequestChoose(node -> {
            mainPane.chooseNothing();
            node.drawConner();
            mainPane.setChoosenNode(node);

            labelHint.setText(node.node.getClass().getSimpleName());

            if (node.paintFill != null) {
                colorPicker.setValue(node.paintFill);
            }
            if (node.paintStroke != null) {
                colorPicker_Line.setValue(node.paintStroke);
            }

            slider_rotate.setValue(node.rotate);
            slider.setValue(node.strokeWidth);
            mainPane.requestFocus();
        });

        colorPicker.getCustomColors().add(new Color(0, 0, 0, 0));
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            DragBox choosenNode = mainPane.getChoosenNode();
            if (choosenNode != null) {
                if (newValue != null) {
                    choosenNode.setColor(newValue);
                }
            } else {
//                GraphicsContext gc = canvas.getGraphicsContext2D();
//                gc.setFill(newValue);
//                mainPane.setBackGround(newValue);
//                gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
            }
        });
        colorPicker_Line.valueProperty().addListener((observable, oldValue, newValue) -> {
            DragBox choosenNode = mainPane.getChoosenNode();
            if (choosenNode != null && newValue != null) {
                choosenNode.setLineColor(newValue);
            }
        });
        colorPicker_Line.getCustomColors().add(new Color(0, 0, 0, 0));

        slider.setMax(20);
        slider.setMin(1);
        slider.setValue(2);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            mainPane.setLineWidth(newValue.doubleValue());
        });

        slider_rotate.setMax(180);
        slider_rotate.setMin(-180);
        slider_rotate.setValue(0);

        slider_rotate.valueProperty().addListener((observable, oldValue, newValue) -> {
            mainPane.setNodeRotate(newValue.doubleValue());
        });

        button_paint.setStyle("-fx-background-image: url('/drawable/paint.png');-fx-background-size: stretch;-fx-background-position: center;-fx-background-radius: 25;-fx-border-radius: 25;");
        button_text.setStyle("-fx-background-image: url('/drawable/text.png');-fx-background-size: stretch;-fx-background-position: center;-fx-background-radius: 25;-fx-border-radius: 25;");
        button_circle.setStyle("-fx-background-image: url('/drawable/circle.png');-fx-background-size: stretch;-fx-background-position: center;-fx-background-radius: 25;-fx-border-radius: 25;");
        button_pic.setStyle("-fx-background-image: url('/drawable/pic.png');-fx-background-size: stretch;-fx-background-position: center;-fx-background-radius: 25;-fx-border-radius: 25;");
        button_line.setStyle("-fx-background-image: url('/drawable/line.png');-fx-background-size: stretch;-fx-background-position: center;-fx-background-radius: 25;-fx-border-radius: 25;");
        button_rectangle.setStyle("-fx-background-image: url('/drawable/rect.png');-fx-background-size: stretch;-fx-background-position: center;-fx-background-radius: 25;-fx-border-radius: 25;");
        button_mini.setStyle("-fx-background-image: url('/drawable/heart.png');-fx-background-size: stretch;-fx-background-position: center;-fx-background-radius: 25;-fx-border-radius: 25;");
    }


    /**
     * 设置canvas大小
     */
    private void setCanvasSize() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader((getClass().getResource("size_chooser.fxml")));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage(DECORATED);
            stage.setTitle("选择画布");
            Scene scene = new Scene(root1);
            sizeChooser = fxmlLoader.getController();
            stage.setScene(scene);
            stage.showAndWait();
            if (sizeChooser.getCanvas() != null) {
                canvas.setHeight(sizeChooser.getCanvas().getHeight());
                canvas.setWidth(sizeChooser.getCanvas().getWidth());
                canvas.setLayoutX(450 - canvas.getWidth() / 2);
                canvas.setLayoutY(300 - canvas.getHeight() / 2);
                Rectangle rectangle = new Rectangle(canvas.getWidth(), canvas.getHeight());
                rectangle.setLayoutX(canvas.getLayoutX());
                rectangle.setLayoutY(canvas.getLayoutY());
                mainPane.setClip(rectangle);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            } else {
                //不选择就退出程序
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载工程文件
     *
     * @param actionEvent
     * @throws ClassNotFoundException
     */
    public void onButtonLoadProjClick(ActionEvent actionEvent) throws ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PhotoScrip file -- PSG (*.psg)", "*.psg"));
        File file = fileChooser.showOpenDialog(this.stage);
        ProjectSaver tempObj;
        int tempNum;
        if (file != null) {
            try {
                ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(file));
                mainPane.getChildren().remove(1, mainPane.getChildren().size());
                for (int i = objIn.readInt(); i > 1; i--) {
                    tempNum = objIn.readInt();
                    switch (tempNum) {
                        case IMAGE:
                            tempObj = (ProjectSaver) objIn.readObject();
                            ImageBox imageBox = tempObj.constructImage();
                            int[] buffer = (int[]) objIn.readObject();
                            int k = 0;
                            int tempW = buffer[k++];
                            int tempH = buffer[k++];
                            WritableImage wImage = new WritableImage(tempW, tempH);
                            PixelWriter pixelWriter = wImage.getPixelWriter();
                            for (int x = 0; x < tempW; x++) {
                                for (int y = 0; y < tempH; y++) {
                                    pixelWriter.setArgb(x, y, buffer[k++]);
                                }
                            }
                            imageBox.setContentNode(new ImageView(), (node, Parent) -> {
                                ((ImageView) node).setImage(wImage);
                                ((ImageView) node).xProperty().set(10);
                                ((ImageView) node).yProperty().set(10);
                                ((ImageView) node).fitWidthProperty().bind(Parent.widthProperty().subtract(20));
                                ((ImageView) node).fitHeightProperty().bind(Parent.heightProperty().subtract(20));
                                mainPane.getChildren().add(Parent);
                                mainPane.setChooseListener(Parent);
                            });
                            break;
                        case NORMAL:
                            tempObj = (ProjectSaver) objIn.readObject();
                            DragBox dragBox = tempObj.construtDragBox();
                            Color fill = new Color(tempObj.fr, tempObj.fg, tempObj.fb, tempObj.fa);
                            Color stroke = new Color(tempObj.r, tempObj.g, tempObj.b, tempObj.a);
                            switch (tempObj.getType()) {
                                case ELLIPSE:
                                    circle(dragBox, tempObj.strokeWidth, tempObj.rotate, fill, stroke);
                                    break;
                                case RECT:
                                    rectangle(dragBox, tempObj.strokeWidth, tempObj.rotate, fill, stroke);
                                    break;
                                case SVG:
                                    svg(dragBox, tempObj.svgPath, fill, stroke, tempObj.strokeWidth, tempObj.rotate);
                                    break;
                                case TEXT:
                                    text(dragBox, new Font(tempObj.fontFamily, tempObj.fontSize), tempObj.getText(),
                                            fill, stroke, tempObj.w, tempObj.strokeWidth, tempObj.rotate);
                                    break;
                                case LINE:
                                    DragBoxWithLine dragBoxWithLine = tempObj.construtLineDragBox();
                                    line(dragBoxWithLine, tempObj.x, tempObj.y, tempObj.xStart, tempObj.yStart,
                                            tempObj.xEnd, tempObj.yEnd, stroke, tempObj.strokeWidth);
                                    break;

                            }
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


    /**
     * 保存工程文件
     *
     * @param event
     */
    public void onButtonSaveProjClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("project");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PhotoScrip file -- PSG (*.psg)", "*.psg"));
        File file = fileChooser.showSaveDialog(this.stage);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeInt(mainPane.getChildren().size());
            for (Node node :
                    mainPane.getChildren()) {
                if (node instanceof ImageBox) {
                    objOut.writeInt(IMAGE);
                    objOut.writeObject(((ImageBox) node).getData());
                    objOut.writeObject(((ImageBox) node).getP());
                } else if (node instanceof DragBoxWithLine) {
                    objOut.writeInt(NORMAL);
                    objOut.writeObject(((DragBox) node).getData());
                } else if (node instanceof DragBox) {
                    objOut.writeInt(NORMAL);
                    objOut.writeObject(((DragBox) node).getData());
                }
            }
            objOut.flush();
            objOut.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "文件成功保存在：" + file.getAbsolutePath());
            alert.setTitle("保存成功");
            alert.setHeaderText(null);
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setTitle("保存失败");
            alert.setHeaderText(null);
            alert.showAndWait();
            e.printStackTrace();
        }
    }


    /**
     * 打开一张图片
     *
     * @param event
     */
    public void onButtonOpenImageClick(ActionEvent event) {

        button_pic.setDisable(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ALL Images (*.*)", "*.*"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG (*.jpg)", "*.jpg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP (*.bmp)", "*.bmp"));

        File file = fileChooser.showOpenDialog(this.stage);
        button_pic.setDisable(false);
        if (file != null) {
            ImageBox imageBox = new ImageBox();
            imageBox.setContentNode(new ImageView(), (node, Parent) -> {
                try {
                    ((ImageView) node).setImage(new Image(new FileInputStream(file)));
                    ((ImageView) node).xProperty().set(10);
                    ((ImageView) node).yProperty().set(10);

                    ((ImageView) node).fitWidthProperty().bind(Parent.widthProperty().subtract(20));
                    ((ImageView) node).fitHeightProperty().bind(Parent.heightProperty().subtract(20));

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
     * 保存当前画板内容到png
     *
     * @param event
     */
    public void onButtonSaveClick(ActionEvent event) {
        mainPane.chooseNothing();
        button_save.setDisable(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("picture" + System.currentTimeMillis());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(this.stage);
        button_save.setDisable(false);

        mainPane.setClip(new Rectangle(canvas.getWidth(), canvas.getHeight()));
        snapshot(mainPane, file);
    }

    /**
     * 画一个圆
     *
     * @param event
     */
    public void onButtonCircleClick(ActionEvent event) {
        circle(new DragBox(), slider.getValue(), slider_rotate.getValue(), colorPicker.getValue(), colorPicker_Line.getValue());
    }

    /**
     * 画一个矩形
     *
     * @param event
     */
    public void onButtonRectangleClick(ActionEvent event) {
        DragBox dragBox = new DragBox();
        rectangle(dragBox, slider.getValue(), slider_rotate.getValue(), colorPicker.getValue(), colorPicker_Line.getValue());
    }

    /**
     * 画一个箭头
     *
     * @param actionEvent
     */
    public void onButtonArrowClick(ActionEvent actionEvent) {
        svg(new DragBox(), "M20,20 120,20 120,0 160,30 120,60 120,40 20,40 z", colorPicker.getValue(), colorPicker_Line.getValue(),
                slider.getValue(), slider_rotate.getValue());
    }

    /**
     * 画一个星星
     *
     * @param actionEvent
     */
    public void onButtonStarClick(ActionEvent actionEvent) {
        svg(new DragBox(), "M50,10 75,90 10,40 90,40 25,90z", colorPicker.getValue(), colorPicker_Line.getValue(),
                slider.getValue(), slider_rotate.getValue());
    }


    /**
     * 完美的添加线段
     *
     * @param actionEvent
     */
    public void onButtonLineClick(ActionEvent actionEvent) {
        line(new DragBoxWithLine(), 400, 250, 0, 0, 100, 100,
                colorPicker_Line.getValue(), slider.getValue());
    }

    private void line(DragBoxWithLine dragBoxWithLine, double x, double y, double x1, double y1,
                      double x2, double y2, Color fill, double width) {
        dragBoxWithLine.setContentNode(new Line(), (node, parent) -> {
            dragBoxWithLine.setLineParam(x, y, x1, y1, x2, y2);
            parent.setLineWidth(width);
            parent.setLineColor(fill);
        });
//        System.out.println("添加线 "+mainPane.getChildren().size());
        mainPane.getChildren().add(dragBoxWithLine);
        mainPane.setChooseListener(dragBoxWithLine);
        mainPane.requestFocus();
//        System.out.println("添加线 "+mainPane.getChildren().size());
    }


    /**
     * 点击添加文本
     *
     * @param actionEvent
     */
    public void onButtonTextClick(ActionEvent actionEvent) {
        DragBox dragBox = new DragBox();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader((getClass().getResource("font_chooser.fxml")));
            Parent root1 = fxmlLoader.load();
            Stage text_dialog = new Stage(DECORATED);
            text_dialog.setTitle("添加文本");
            Scene scene = new Scene(root1);
            fontChooserController = fxmlLoader.getController();
            // t1.setStage(modal_dialog);
            text_dialog.setScene(scene);
            text_dialog.showAndWait();
            if (fontChooserController.getSelectedFont() != null) {
                Font font = fontChooserController.getSelectedFont().getFont();
                String input = fontChooserController.getSelectedFont().getInputString();
                Color color = fontChooserController.getSelectedFont().getColor();
                text(dragBox, new Font(font.getName(),font.getSize()+30), input, null, color, 0, slider.getValue(), slider_rotate.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 节点向上一层移动
     *
     * @param actionEvent
     */
    public void onButtonUpClick(ActionEvent actionEvent) {
        mainPane.up();
    }

    /**
     * 节点向下一层移动
     *
     * @param actionEvent
     */
    public void onButtonDownClick(ActionEvent actionEvent) {
        mainPane.down();
    }

    /**
     * 节点移动到顶层
     *
     * @param actionEvent
     */
    public void onButtonTopClick(ActionEvent actionEvent) {
        mainPane.toTop();
    }

    /**
     * 节点移动到底层
     *
     * @param actionEvent
     */
    public void onButtonBottomClick(ActionEvent actionEvent) {
        mainPane.toBottom();
    }

    /**
     * 添加一个SvgPath类型的节点
     *
     * @param path svgPath的输入字符串
     */
    private void svg(DragBox dragBox, String path, Color fill, Color line, double width, double rotate) {
        dragBox.setSvgNode(path, fill, line, width, rotate);
        mainPane.getChildren()
                .add(dragBox);
        mainPane.setChooseListener(dragBox);
        mainPane.requestFocus();
    }

    /**
     * 添加一个椭圆
     *
     * @param dragBox
     * @param strokeWidth
     * @param rotate
     * @param fill
     * @param stroke
     */
    private void circle(DragBox dragBox, double strokeWidth, double rotate, Color fill, Color stroke) {
        dragBox.setContentNode(new Ellipse(), (node, root) -> {
            ((Ellipse) node).centerXProperty().bind(root.widthProperty().divide(2));
            ((Ellipse) node).centerYProperty().bind(root.heightProperty().divide(2));
            ((Ellipse) node).radiusXProperty().bind(root.widthProperty().divide(2).subtract(10));
            ((Ellipse) node).radiusYProperty().bind(root.heightProperty().divide(2).subtract(10));
            ((Ellipse) node).setSmooth(true);

            root.setLineWidth(strokeWidth);
            root.setNodeRotate(rotate);
            root.setColor(fill);
            root.setLineColor(stroke);
        });
        mainPane.getChildren()
                .add(dragBox);
        mainPane.setChooseListener(dragBox);

        mainPane.requestFocus();
    }

    /**
     * 添加一个矩形
     * @param dragBox
     * @param strokeWidth
     * @param rotate
     * @param fill
     * @param stroke
     */
    private void rectangle(DragBox dragBox, double strokeWidth, double rotate, Color fill, Color stroke) {
        dragBox.setContentNode(new Rectangle(), (node, root) -> {
            ((Rectangle) node).widthProperty().bind(root.widthProperty().subtract(20));
            ((Rectangle) node).heightProperty().bind(root.heightProperty().subtract(20));
            ((Rectangle) node).layoutXProperty().set(10);
            ((Rectangle) node).layoutYProperty().set(10);
            ((Rectangle) node).setSmooth(true);

            root.setLineWidth(strokeWidth);
            root.setNodeRotate(rotate);
            root.setColor(fill);
            root.setLineColor(stroke);
        });
        mainPane.getChildren()
                .add(dragBox);
        mainPane.setChooseListener(dragBox);
        mainPane.requestFocus();
    }

    /**
     * 添加文字
     *
     * @param dragBox
     * @param font
     * @param text
     * @param color
     * @param stroke
     * @param w
     */
    private void text(DragBox dragBox, Font font, String text, Color color, Color stroke, double w,
                      double strokeWidth, double rotate) {
        dragBox.setContentNode(new Text(), (node, parent) -> {
            parent.setOnDoubleClickListener((root, node1) -> {
                if (node1 instanceof Text) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader((getClass().getResource("font_chooser.fxml")));
                        Parent root1 = fxmlLoader.load();
                        Stage text_dialog = new Stage(DECORATED);
                        text_dialog.setTitle("修改文本");
                        Scene scene = new Scene(root1);
                        FontChooserController fontChooserController = fxmlLoader.getController();
                        fontChooserController.setInitText(((Text) node1).getText());
                        text_dialog.setScene(scene);
                        text_dialog.showAndWait();
                        if (fontChooserController.getSelectedFont() != null) {
                            Font font1 = fontChooserController.getSelectedFont().getFont();
                            String input = fontChooserController.getSelectedFont().getInputString();
                            Color color1 = fontChooserController.getSelectedFont().getColor();
                            ((Text) node1).setText(input);
                            ((Text) node1).setFont(font1);
                            ((Text) node1).setFill(color1);
                            ((Text) node1).setStroke(color1);
                            Bounds bounds = node1.getLayoutBounds();
                            parent.setPrefWidth(bounds.getWidth() + 20);
                            parent.setPrefHeight(bounds.getHeight() + 20);
                            ((Text) node).setLayoutY(10 - bounds.getMinY());
                            ((Text) node).setLayoutX(10 - bounds.getMinX());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            if (stroke != null) {
                parent.setLineColor(stroke);
                if(color != null){
                    parent.setColor(color);
                }else{
                    parent.setColor(Color.BLACK);
                }
            }else{
                parent.setLineColor(Color.BLACK);
                if(color != null){
                    parent.setColor(color);
                }else{
                    parent.setColor(Color.BLACK);
                }
            }

            ((Text) node).setText(text);
            ((Text) node).setFont(font);

            Bounds bounds = node.getLayoutBounds();

            if (w > 20) {
                parent.setPrefWidth(w);
            } else {
                parent.setPrefWidth(bounds.getWidth() + 20);
            }
            parent.setPrefHeight(bounds.getHeight() + 20);

            ((Text) node).setLayoutY(10 - bounds.getMinY());
            ((Text) node).setLayoutX(10 - bounds.getMinX());
            ((Text) node).setTextAlignment(TextAlignment.CENTER);

            parent.widthProperty().addListener((observable, oldValue, newValue) -> {
                double wrap = newValue.doubleValue() - 20;
                ((Text) node).setWrappingWidth(wrap);

                Bounds textBounds = node.getLayoutBounds();
                double textHeight = textBounds.getHeight();
                parent.setPrefHeight(textHeight + 20);
            });

            parent.setLineWidth(strokeWidth);
            parent.setNodeRotate(rotate);

        });
        mainPane.getChildren().add(dragBox);
        mainPane.setChooseListener(dragBox);
        dragBox.requestFocus();
    }


    /**
     * 保存图片
     *
     * @param view 要保存的View
     * @param file 保存的文件
     */
    private void snapshot(Node view, File file) {


        Rectangle2D rectangle2D = new Rectangle2D(canvas.getLayoutX(),
                canvas.getLayoutY()+mainPane.getLayoutY(),
                canvas.getWidth(),canvas.getHeight());
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setViewport(rectangle2D);

        mainPane.setClip(null);

        Image image = view.snapshot(snapshotParameters ,null);

        Rectangle rectangle = new Rectangle(canvas.getWidth(), canvas.getHeight());
        rectangle.setLayoutX(canvas.getLayoutX());
        rectangle.setLayoutY(canvas.getLayoutY());
        mainPane.setClip(rectangle);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "文件成功保存在：" + file.getAbsolutePath(),
                    new ButtonType("分享", ButtonBar.ButtonData.LEFT),
                    new ButtonType("返回", ButtonBar.ButtonData.RIGHT)
            );
            alert.setTitle("保存成功");
            alert.setHeaderText(null);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get().getButtonData().equals(ButtonBar.ButtonData.LEFT)) {
                Thread thread = new Thread(new uploadTask(file), "upload");
                thread.setDaemon(true);
                thread.start();

            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setTitle("保存失败");
            alert.setHeaderText(null);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * 复制选中的组件
     *
     * @param actionEvent
     */
    public void onButtonCopyClick(ActionEvent actionEvent) {
        DragBox dragBox = mainPane.getChoosenNode();
        DragBox newBox = new DragBox(dragBox);
        Node node = dragBox.getChildren().get(0);
        if (node instanceof Ellipse) {
            circle(newBox, dragBox.strokeWidth, dragBox.rotate, dragBox.paintFill, dragBox.paintStroke);
        } else if (node instanceof Rectangle) {
            rectangle(newBox, dragBox.strokeWidth, dragBox.rotate, dragBox.paintFill, dragBox.paintStroke);
        } else if (node instanceof SVGPath) {
            svg(newBox, ((SVGPath) node).getContent(), dragBox.paintFill, dragBox.paintStroke, dragBox.strokeWidth, dragBox.rotate);
        } else if (node instanceof ImageView) {
            Image image = ((ImageView) node).getImage();
            ImageBox imageBox = new ImageBox();
            imageBox.setContentNode(new ImageView(), (nod, Parent) -> {
                ((ImageView) nod).setImage(image);
                ((ImageView) nod).xProperty().set(10);
                ((ImageView) nod).yProperty().set(10);
                ((ImageView) nod).fitWidthProperty().bind(Parent.widthProperty().subtract(20));
                ((ImageView) nod).fitHeightProperty().bind(Parent.heightProperty().subtract(20));

                mainPane.getChildren().add(Parent);
                mainPane.setChooseListener(Parent);
                mainPane.requestFocus();
            });
            imageBox.setPrefWidth(dragBox.getPrefWidth());
            imageBox.setPrefHeight(dragBox.getPrefHeight());
            imageBox.setNodeRotate(dragBox.rotate);
            imageBox.X.set(dragBox.X.getValue()+20);
            imageBox.Y.set(dragBox.Y.getValue()+20);
        } else if (node instanceof Text) {
            text(newBox, ((Text) node).getFont(), ((Text) node).getText(), (Color) ((Text) node).getFill(),
                    (Color) ((Text) node).getStroke(), ((Text) node).getWrappingWidth() + 20,
                    dragBox.strokeWidth, dragBox.rotate);
        } else if (node instanceof Line) {
            DragBoxWithLine dragBoxWithLine = new DragBoxWithLine(mainPane.getChoosenNode());
            line(dragBoxWithLine, node.getLayoutX() + 20, node.getLayoutY() + 20, ((Line) node).getStartX(),
                    ((Line) node).getStartY(), ((Line) node).getEndX(), ((Line) node).getEndY(),
                    (Color) ((Line) node).getStroke(), ((Line) node).getStrokeWidth());
        }
    }

    /**
     * 删除选中的组件
     *
     * @param actionEvent
     */
    public void onButtonDeleteClick(ActionEvent actionEvent) {
        if (mainPane.getChoosenNode() != null) {
            mainPane.getChildren().remove(mainPane.getChoosenNode());
            mainPane.setChoosenNode(null);
        }
    }

    /**
     * 按钮画笔操作被点击
     *
     * @param actionEvent
     */
    public void onButtonPaintClick(ActionEvent actionEvent) {
        mainPane.chooseNothing();
        Image image = new Image("drawable/penCursor.png");
        mainPane.setCursor(new ImageCursor(image,image.getHeight()/2-24,
                image.getWidth()/2+24));
        new PaintBrush().paint(canvas, colorPicker_Line, colorPicker, slider, mainPane);
    }

    /**
     * 清空画板
     *
     * @param actionEvent
     */
    public void onButtonClearClick(ActionEvent actionEvent) {
        mainPane.getChildren().remove(1, mainPane.getChildren().size());
    }

    /**
     * 新建一个工程文件
     *
     * @param actionEvent
     */
    public void onButtonCreateProjClick(ActionEvent actionEvent) {
        if (mainPane.getChildren().size() > 1) {
            Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "要保存当前工程吗？",
                    new ButtonType("不保存", ButtonBar.ButtonData.NO),
                    new ButtonType("保存", ButtonBar.ButtonData.YES),
                    new ButtonType("取消", ButtonBar.ButtonData.LEFT)
            );
            alert.setTitle("注意");
            alert.setHeaderText(null);
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get().getButtonData().equals(ButtonBar.ButtonData.NO)) {
                mainPane.getChildren().remove(1, mainPane.getChildren().size());
                setCanvasSize();
            } else if (buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                onButtonSaveProjClick(actionEvent);
                mainPane.getChildren().remove(1, mainPane.getChildren().size());
                setCanvasSize();
            } else {
                return;
            }
        } else {
            mainPane.getChildren().remove(1, mainPane.getChildren().size());
            setCanvasSize();
        }
    }

    /**
     * 添加svg预定义图片
     * @param actionEvent
     */
    public void onButtonMiniClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader((getClass().getResource("graphic_board.fxml")));
            Parent root1 = fxmlLoader.load();
            Stage stage_mini = new Stage(DECORATED);
            stage_mini.setTitle("添加小图形");
            Scene scene = new Scene(root1);
            GraphicBoard graphicBoard = fxmlLoader.getController();
            graphicBoard.init_board(this);
            graphicBoard.setSvgData(svgData);
            stage_mini.setScene(scene);
            stage_mini.showAndWait();

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * 文件上传类
     */
    private class uploadTask extends Task<String> {
        private File file;

        uploadTask(File file) {
            this.file = file;
            labelHint.setText("正在上传...");
        }

        @Override
        protected String call() throws Exception {
            final String url = PictureUploadUtil.getUrl(file.getAbsolutePath());

            Platform.runLater(() -> {
                labelHint.setText("");
                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION,
                        url,
                        new ButtonType("复制到剪切板", ButtonBar.ButtonData.YES)
                );
                alert.setTitle("上传成功");
                alert.setHeaderText(null);
                Optional<ButtonType> buttonType = alert.showAndWait();
                if (buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(url);
                    clipboard.setContent(cc);
                }
            });
            return url;
        }

    }

    public void drawSvg(String content){
        svg(new DragBox(),content,colorPicker.getValue(),
                colorPicker_Line.getValue(),slider.getValue(),slider_rotate.getValue());
    }

}
