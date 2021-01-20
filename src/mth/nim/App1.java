package mth.nim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;
import java.util.Random;

import static javafx.util.Duration.millis;
import static javafx.util.Duration.seconds;
import static mth.nim.App.Player.USER;

public class App1 extends javafx.application.Application {


    private mth.nim.Stage board;
    private final SimpleIntegerProperty userDeletions = new SimpleIntegerProperty();


    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(App1.class.getResource("fxml/stage.fxml"));
        Parent node = fxmlLoader.load();
        board = fxmlLoader.getController();


        Scene scene = new Scene(node);

        Util.style(scene);
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setOnShown(e -> {
            DelayedAction.run(() -> {
                List<Transition> transitions = board.initializeTileSurface();
                for (Transition t : transitions)
                    t.playFromStart();

            }, millis(1500));
        });
        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}