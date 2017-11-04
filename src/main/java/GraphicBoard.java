import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.util.Pair;
import tools.SvgData;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphicBoard {

    public ListView<String> listView;
    public Pane iconsPane;
    private ArrayList<Pair<String, SvgData>> svgData;
    private Controller controller;
    public ScrollBar scroller;

    public GraphicBoard() {
    }

    public void init_board(Controller controller) {
        this.controller = controller;

    }

    @FXML
    private void itemClick(MouseEvent mouseEvent) {
        String cases = listView.getSelectionModel().getSelectedItem();
        int index = map.get(cases);
        iconsPane.getChildren().clear();
        showSvgs(index);
    }

    private void showSvgs(int index) {
        int x = 0, y = 0;
        for (String j :
                svgData.get(index).getValue().getData().values()) {
            SVGPath path = new SVGPath();
            path.setContent(j);
            Bounds bounds = path.boundsInLocalProperty().getValue();
            double scale = Math.max(bounds.getHeight(),bounds.getWidth());
            path.setScaleX(30 / scale);
            path.setScaleY(30 / scale);
            Button button = new Button();
            button.setGraphic(path);
            button.getStylesheets().add("css/svgbutton_style.css");
            button.setOnMouseClicked(event -> {
                SVGPath svgPath = (SVGPath) button.getGraphic();
                controller.drawSvg(svgPath.getContent());
                (button.getScene().getWindow()).hide();
            });
            button.setManaged(false);
            button.resizeRelocate(x , y, 50, 50);
            x+=55;
            if (x > 399) {
                x = 0;
                y += 55;
            }
            iconsPane.getChildren().add(button);
        }
    }

    HashMap<String, Integer> map;

    public void setSvgData(ArrayList<Pair<String, SvgData>> svgData) {
        this.svgData = svgData;
        map = new HashMap<>();
        ObservableList<String> data = FXCollections.observableArrayList();
        int index = 0;
        for (Pair<String, SvgData> i :
                svgData) {
            String name = i.getKey();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            map.put(name, index++);
            data.add(name);
        }
        listView.setItems(data);
        showSvgs(0);
    }

    public ArrayList<Pair<String, SvgData>> getSvgData() {
        return svgData;
    }
}
