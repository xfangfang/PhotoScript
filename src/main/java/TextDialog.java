import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class TextDialog extends AnchorPane{
    private static TextDialog textDialog;
    private static Stage newAlertDialog ;
    public Label text;

    private  TextDialog(String message) {
        FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("textDialog.fxml"));
        fXMLLoader.setRoot(TextDialog.this);
        fXMLLoader.setController(TextDialog.this);
        try {
            fXMLLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        text.setText(message);
    }
    public static void showAlertDialog(String message) {
        newAlertDialog = new Stage(StageStyle.TRANSPARENT);
        newAlertDialog.setResizable(false);
        textDialog = new TextDialog(message);
        newAlertDialog.setScene(new Scene(textDialog));
        newAlertDialog.show();
    }

    public static void hideAlertDialog() {
        if(newAlertDialog != null) {
            newAlertDialog.hide();
        }
    }

    public class te extends Alert{

        public te(AlertType alertType) {
            super(alertType);
        }

        public te(AlertType alertType, String contentText, ButtonType... buttons) {
            super(alertType, contentText, buttons);
        }
    }
}
