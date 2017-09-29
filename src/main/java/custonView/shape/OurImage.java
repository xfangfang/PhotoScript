package custonView.shape;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import custonView.DragBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class OurImage extends DragBox{
    private ContextMenu contextFileMenu = new ContextMenu();
    public OurImage(){
        super();
        init();
        System.out.println("finish");
    }
    private void init(){
        MenuItem item1 = new MenuItem("明亮");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                imagePixeOperator(0);
            }
        });

        MenuItem item2 = new MenuItem("昏暗");
        item2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                imagePixeOperator(1);
            }
        });
        MenuItem item3 = new MenuItem(("灰度处理"));
        item3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                imagePixeOperator(2);
            }
        });

        MenuItem item4  = new MenuItem("颜色反转");
        item4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                imagePixeOperator(3);
            }
        });

        MenuItem item5 = new MenuItem("增加饱和度");
        item5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                imagePixeOperator(4);
            }
        });
        MenuItem item6 = new MenuItem("减少饱和度");
        item6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                imagePixeOperator(5);
            }
        });
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
        if(ImageView.class.isInstance(getChildren().get(1))) {
            tempImageView = (ImageView) getChildren().get(1);
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

