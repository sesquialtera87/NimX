package mth.nim;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

public class Stage {
    @FXML
    Label label;
    @FXML
    BorderPane background;
    @FXML
    Button testButton;

    public void initialize() {
        testButton.setOnMouseClicked(e -> {
            TranslateTransition t = new TranslateTransition(Duration.millis(1000), label);
            t.setAutoReverse(false);
            t.setByX(300);
            t.setByY(20.0);

            ScaleTransition t1 = new ScaleTransition(Duration.millis(1000));
            t1.setToX(0.1);
            t1.setToY(0.1);
            t1.setAutoReverse(true);

            ParallelTransition pt = new ParallelTransition(label, t, t1);
            pt.setOnFinished(ef -> {
//                label.setVisible(false);
            });
            pt.play();
        });
    }
}
