import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SizeChooser {
    public Button size1;
    public Button size2;
    public Button size3;
    public Button size4;
    private Canvas canvas;

    public void size1Click(ActionEvent actionEvent) {
        this.canvas = new Canvas();
        this.canvas.setWidth(800);
        this.canvas.setHeight(600);
        ((Stage) size1.getScene().getWindow()).hide();
    }
    public void size2Click(ActionEvent actionEvent) {
        this.canvas = new Canvas();
        this.canvas.setWidth(700);
        this.canvas.setHeight(500);
        ((Stage) size2.getScene().getWindow()).hide();
    }
    public void size3Click(ActionEvent actionEvent) {
        this.canvas = new Canvas();
        this.canvas.setWidth(640);
        this.canvas.setHeight(480);
        ((Stage) size3.getScene().getWindow()).hide();
    }
    public void size4Click(ActionEvent actionEvent) {
        this.canvas = new Canvas();
        this.canvas.setWidth(480);
        this.canvas.setHeight(320);
        ((Stage) size4.getScene().getWindow()).hide();
    }

    public Canvas getCanvas() {
        return this.canvas;
    }
}
