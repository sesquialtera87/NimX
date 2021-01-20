package mth.nim;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.util.Callback;

import java.io.IOException;

public class ChoiceDialog {

    @FXML
    protected Button button1, button2;

    public void setLeftButtonAction(Runnable action) {
        button1.setOnAction(e -> action.run());
    }

    public void setRightButtonAction(Runnable action) {
        button2.setOnAction(e -> action.run());
    }

    public static void showDialog(Callback<HPos, Void> callback) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/popup.fxml"));
            Parent content = fxmlLoader.load();
            content.getStylesheets().add("mth/nim/resources/style.css");
            ChoiceDialog controller = fxmlLoader.getController();

            DialogUtil.CustomDialog dialog = new DialogUtil.CustomDialog(content);
            dialog.openDialog();
            controller.setRightButtonAction(() -> {
                dialog.closeDialog();
                callback.call(HPos.RIGHT);
            });
            controller.setLeftButtonAction(() -> {
                dialog.closeDialog();
                callback.call(HPos.LEFT);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
