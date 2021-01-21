package mth.nim;

import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import kotlin.Pair;

import java.util.List;
import java.util.Random;

import static javafx.util.Duration.millis;
import static javafx.util.Duration.seconds;
import static mth.nim.App.Player.USER;

public class App1 extends javafx.application.Application {


    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(App1.class.getResource("fxml/stage.fxml"));
        Parent node = fxmlLoader.load();

        StackPane stack = (StackPane) node.lookup("#stackPane");


        Scene scene = new Scene(node);

        Util.style(scene);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnShown(e -> {
            Pair<Game.Board, ParallelTransition> res = Game.createGameBoard(new Tuple(5, 4, 3, 2, 1));
            stack.getChildren().add(res.getFirst());
            DelayedAction.run(() -> {
                res.getSecond().playFromStart();
                System.out.println("Uuuuuu");
            }, Duration.millis(2000));

        });
        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}