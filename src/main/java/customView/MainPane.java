package customView;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;



/**
 * Created by FANGs on 2017/9/27.
 */
public class MainPane extends javafx.scene.layout.Pane{

    private requestChoose requestChooseListener;
    private DragBox choosenNode;
    private Color backGround;

    public MainPane(){

        setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE){
                System.out.println("delete2");
                if(choosenNode != null){
                    System.out.println("delete2 have choocenNode");
                    getChildren().remove(choosenNode);
                    choosenNode = null;
                }
            }
        });
    }




    public void setChooseListener(DragBox box){
        box.setChooseListener(this.requestChooseListener);
    }
    public interface requestChoose{
        void request(DragBox node);
    }

    public void chooseNothing(){
        for (Node i :
                getChildren()) {
            if(i instanceof DragBox){
                ((DragBox) i).clearConner();
            }
        }
        choosenNode = null;
    }

    public DragBox getChoosenNode(){
        return choosenNode;
    }

    public void up(){
        if(choosenNode != null){
            int index = getChildren().indexOf(choosenNode);
            int childrenNum = getChildren().size() - 1;
            //除去canavs子视图
            if(index < childrenNum) {
                getChildren().remove(choosenNode);
                getChildren().add(index+1, choosenNode);
            }
        }
    }

    public void down(){
        if(choosenNode != null){
            int index = getChildren().indexOf(choosenNode);
            if(index >1) {
                getChildren().remove(choosenNode);
                getChildren().add(index-1, choosenNode);
            }
        }
    }

    public void toTop(){
        if(choosenNode != null){
            getChildren().remove(choosenNode);
            getChildren().add(choosenNode);
        }
    }

    public void toBottom(){
        if(choosenNode != null){
            getChildren().remove(choosenNode);
            getChildren().add(1,choosenNode);
        }
    }

    public void setLineWidth(double lineWidth){
        if(choosenNode != null){
            choosenNode.setLineWidth(lineWidth);
        }
    }

    public void setNodeRotate(double rotate){
        if(choosenNode != null){
            choosenNode.setNodeRotate(rotate);
        }
    }

    public void setNodeRequestChoose(requestChoose requestChooseListener){
        this.requestChooseListener = requestChooseListener;
    }

    public void setChoosenNode(DragBox node){
        this.choosenNode = node;
    }

    public Color getBackGround() {
        return backGround;
    }

    public void setBackGround(Color backGround) {
        this.backGround = backGround;
    }
}
