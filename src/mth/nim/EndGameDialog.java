package mth.nim;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.io.IOException;

public class EndGameDialog {
    @FXML
    protected Button button1, button2;
    @FXML
    protected ImageView iconViewer;
    @FXML
    protected Label textArea;

    public static final Image AI_IMAGE = new Image(App.class.getResourceAsStream("resources/robot.png"));
    public static final Image PLAYER_IMAGE = new Image(App.class.getResourceAsStream("resources/badge.png"));

    public void setLeftButtonAction(Runnable action) {
        button1.setOnAction(e -> action.run());
    }

    public void setRightButtonAction(Runnable action) {
        button2.setOnAction(e -> action.run());
    }

    public static void showDialog(Callback<HPos, Void> callback, Image icon) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/EndGamePopup.fxml"));
            Parent content = fxmlLoader.load();
            content.getStylesheets().add("mth/nim/resources/style.css");
            EndGameDialog controller = fxmlLoader.getController();
            controller.iconViewer.setImage(icon);

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
