package mth.nim;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;

import static javafx.util.Duration.*;
import static mth.nim.App.Player.USER;

public class App extends javafx.application.Application {

    public enum Player {
        AI, USER
    }

    public static final String PILE_CONFIGURATION = "5, 4, 3, 2, 1";

    private final SimpleObjectProperty<Player> player = new SimpleObjectProperty<>();
    private final IntegerProperty activePile = new SimpleIntegerProperty(-1);
    private final BooleanProperty gameComplete = new SimpleBooleanProperty(false);

    private mth.nim.Stage board;
    private final SimpleIntegerProperty userDeletions = new SimpleIntegerProperty();

    private Nim game = new Nim(new Tuple(1, 2, 3, 4, 5));
    private final NimAi AI = new NimAi(game);
    private Timeline aiTime;

    @Override
    public void start(Stage stage) throws Exception {
        player.addListener((observableValue, oldV, newV) -> {
            System.out.println("Player changed -> " + newV);
            if (newV == Player.AI) {
                aiTime.playFromStart();
            }
        });

        gameComplete.addListener(c -> {
            if (gameComplete.get()) {
                System.out.println("Game ended. Winner -> " + player);

                Image icon = player.get() == Player.AI ? EndGameDialog.AI_IMAGE : EndGameDialog.PLAYER_IMAGE;
                String text = player.get() == Player.AI ? "Sorry! AI Wins" : "You win!";
                Callback<HPos, Void> callback = hPos -> {
                    if (hPos == HPos.LEFT) DelayedAction.run(this::startNewGame, millis(800));
                    else DelayedAction.run(Platform::exit, millis(1000));
                    return null;
                };
                DelayedAction.run(() -> EndGameDialog.showDialog(callback, icon, text.toUpperCase()), millis(1000));
            }

        });

        aiTime = new Timeline();
        aiTime.setAutoReverse(false);
        aiTime.setCycleCount(1);
        aiTime.setDelay(seconds(1));
        aiTime.getKeyFrames().add(new KeyFrame(millis(300), e -> AiMove()));


        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/stage.fxml"));
        Parent node = fxmlLoader.load();
        board = fxmlLoader.getController();
        board.addEventHandler(EventType.ROOT, e -> {
            if (e.getEventType() == mth.nim.Stage.MOVE_COMPLETE_EVENT_TYPE) {
                playerMove();
            }
        });

        // bindings
        userDeletions.bindBidirectional(board.userDeletions);
        activePile.bindBidirectional(board.activePile);
        player.bindBidirectional(board.player);

        Scene scene = new Scene(node);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, evt -> {
            if (evt.getCode() == KeyCode.P) {
                if (player.get() == USER) {
                    playerMove();
                    evt.consume();
                }
                System.out.println("P pressed");
            }
        });
        Util.style(scene);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnShowing(e -> {
            String path = "C:\\Users\\utente\\Documents\\Java\\nim\\Nim\\src\\mth\\nim\\kn.txt";
//            ai.importKnowledge(path);
        });
        stage.setOnHiding(e -> {
            String path = "C:\\Users\\utente\\Documents\\Java\\nim\\Nim\\src\\mth\\nim\\kn.txt";
//            ai.exportKnowledge(path);
        });
        stage.setOnShown(e -> DelayedAction.run(() -> ChoiceDialog.showDialog(hPos -> {
            if (hPos == HPos.LEFT) { // left button pressed
                DelayedAction.run(this::startNewGame, millis(1000));
            } else if (hPos == HPos.RIGHT) DelayedAction.run(Platform::exit, millis(1500));
            return null;
        }), millis(800)));
        stage.show();

    }

    private void AiMove() {
        System.out.println("AI turn");

        Tuple oldState = game.actualState();
        Tuple aiMove = AI.move();
        int PILE = aiMove.get(0);
        int ELEMENTS_TO_REMOVE = aiMove.get(1);

        Tuple state = game.update(aiMove);
        AI.learn(oldState, state, aiMove, NimAi.AI);

        System.out.println(gameComplete);

        board.popTiles(PILE, ELEMENTS_TO_REMOVE);

        gameComplete.set(game.gameEnded());

        if (!gameComplete.get())
            player.set(USER);
    }

    private void playerMove() {
        System.out.println("Player turn");
        System.out.println("pile=" + activePile.get() + " total=" + userDeletions.get() + "\n");

        if (activePile.get() < 0 || userDeletions.get() == 0) {
            System.err.println("Invalid player move");
            return;
        }

        // get the total deletion of the player and build a move
        Tuple oldState = game.actualState();
        Tuple userMove = new Tuple(activePile.get(), userDeletions.get());
        Tuple state = game.update(userMove);

        AI.learn(oldState, state, userMove, NimAi.PLAYER);

        gameComplete.set(game.gameEnded());

        System.out.println(game.getPileConfiguration());

        if (gameComplete.not().get()) {
            userDeletions.set(0);
            activePile.set(-1); // remove the pile lock

            player.set(Player.AI);
        }
    }

    private void chooseInitialPlayer() {
        int value = new Random().nextInt(2);

        player.set(value == 0 ? Player.USER : Player.AI);
    }

    private void startNewGame() {
        player.set(null);
        game = new Nim(Tuple.parse(PILE_CONFIGURATION));
        AI.setGame(game);

        System.out.println(game.getPileConfiguration());
        System.out.println(AI.getGame());

        activePile.set(-1);
        userDeletions.set(0);

        ParallelTransition pt = new ParallelTransition();
        pt.getChildren().addAll(board.initializeTileSurface());
        pt.setOnFinished(e -> chooseInitialPlayer());
        pt.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}