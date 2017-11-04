package customView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import tools.SHAPE;
import tools.ProjectSaver;

public class ImageBox extends DragBox {
    private ContextMenu contextFileMenu = new ContextMenu();
    public ImageBox(){
        super();
        ini();
    }
    public ImageBox(double x, double y, double w, double h, SHAPE.TYPE type, double rotate, double a, double r, double g, double b, double fa, double fr, double fg, double fb){
        super(x, y, w,h, type, rotate,0,a, r, g, b, fa, fr, fg, fb);
        ini();
    }
    @Override
    public ProjectSaver getData(){
        return super.getData();
    }
    public int [] getP(){
        Image tempImage = ((ImageView)node).getImage();
        PixelReader pixelReader = tempImage.getPixelReader();
        int [] buffer = new int[((int)tempImage.getWidth() * (int)tempImage.getWidth()) + 2];
        int tempH, tempW;
        tempH = (int)tempImage.getHeight();
        tempW = (int)tempImage.getWidth();
        int k = 0;
        buffer[k++] = tempW;
        buffer[k++] = tempH;
        for(int i = 0; i < tempW; i++){
            for(int j = 0; j < tempH; j++){
                buffer[k++] = pixelReader.getArgb(i, j);
            }
        }
        return buffer;
    }


    private void ini(){
        MenuItem item1 = new MenuItem("明亮");
        item1.setOnAction(e -> imagePixeOperator(0));

        MenuItem item2 = new MenuItem("昏暗");
        item2.setOnAction(e -> imagePixeOperator(1));
        MenuItem item3 = new MenuItem(("灰度处理"));
        item3.setOnAction(e -> imagePixeOperator(2));

        MenuItem item4  = new MenuItem("颜色反转");
        item4.setOnAction(event -> imagePixeOperator(3));

        MenuItem item5 = new MenuItem("增加饱和度");
        item5.setOnAction(event -> imagePixeOperator(4));
        MenuItem item6 = new MenuItem("减少饱和度");
        item6.setOnAction(event -> imagePixeOperator(5));
        contextFileMenu.getItems().add(item1);
        contextFileMenu.getItems().add(item2);
        contextFileMenu.getItems().add(item3);
        contextFileMenu.getItems().add(item4);
        contextFileMenu.getItems().add(item5);
        contextFileMenu.getItems().add(item6);
        setOnMouseClicked(event ->{
            if (event.getButton() == MouseButton.SECONDARY  || event.isControlDown())  {
                contextFileMenu.show(this, event.getScreenX(), event.getScreenY());
            }  else  {
                contextFileMenu.hide();
            }
        });
    }

    public void imagePixeOperator(int type){
        ImageView tempImageView = null;
        if(ImageView.class.isInstance(getChildren().get(0))) {
            tempImageView = (ImageView) getChildren().get(0);
        }else{
            System.out.print("fail");
            return;
        }
        tempImageView.getImage();
        Image tempImage = tempImageView.getImage();
        PixelReader pixelReader = tempImage.getPixelReader();
        // Create WritableImage
        WritableImage wImage = new WritableImage(
                (int)tempImage.getWidth(),
                (int)tempImage.getHeight());
        PixelWriter pixelWriter = wImage.getPixelWriter();
        double tempH, tempW;
        tempH = tempImage.getHeight();
        tempW = tempImage.getWidth();
        for(int y = 0; y < tempH; y++){
            for(int x = 0; x < tempW; x++){
                Color color = pixelReader.getColor(x, y);
                switch (type){
                    case 0:
                        color = color.brighter();
                        break;
                    case 1:
                        color = color.darker();
                        break;
                    case 2:
                        color = color.grayscale();
                        break;
                    case 3:
                        color = color.invert();
                        break;
                    case 4:
                        color = color.saturate();
                        break;
                    case 5:
                        color = color.desaturate();
                        break;
                    default:
                        break;
                }
                pixelWriter.setColor(x, y, color);
            }
        }
        tempImageView.setImage(wImage);

    }

}

