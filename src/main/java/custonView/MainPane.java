package custonView;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;

/**
 * Created by FANGs on 2017/9/27.
 */
public class MainPane extends javafx.scene.layout.Pane {

    private requestChoose requestChooseListener;
    private DragBox choosenNode;
    public MainPane(){
        requestChooseListener = node -> {
            for (Node i :
                    getChildren()) {
                if(i instanceof DragBox){
                    ((DragBox) i).setNotChoosen();
                }
            }
            node.setChoosen();
            choosenNode = node;
        };

        setOnMouseReleased(event -> {
            for (Node i :
                    getChildren()) {
                if(i instanceof DragBox){
                    ((DragBox) i).setNotChoosen();
                }
            }
            choosenNode = null;
        });

        setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE){
                if(choosenNode != null){
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
            System.out.println(choosenNode);
        }
    }
}
