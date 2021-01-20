package mth.nim;

import javafx.animation.KeyFrame;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;
import java.util.Random;

import static mth.nim.App.Player.USER;

public class App extends javafx.application.Application {

    public enum Player {
        AI, USER
    }

    public static final Tuple PILE_CONFIGURATION = new Tuple(5, 4, 3, 2, 1);

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
                Platform.runLater(() -> {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    Optional<Integer> result = GameDialog.gameCompleteDialog(player.get()).showAndWait();
                    result.ifPresent(res -> {
                        if (res == GameDialog.QUIT)
                            Platform.exit();
                        else if (res == GameDialog.START_GAME)
                            startNewGame();
                    });
                });
            }

        });

        aiTime = new Timeline();
        aiTime.setAutoReverse(false);
        aiTime.setCycleCount(1);
        aiTime.setDelay(Duration.seconds(1));
        aiTime.getKeyFrames().add(new KeyFrame(Duration.millis(300), e -> AiMove()));


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
        scene.getStylesheets().add("mth/nim/resources/style.css");
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
                DelayedAction.run(this::startNewGame, Duration.millis(1000));
            } else if (hPos == HPos.RIGHT) DelayedAction.run(Platform::exit, Duration.millis(1500));
            return null;
        }), Duration.millis(800)));
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
        player.set(USER);

        gameComplete.set(game.gameEnded());
    }

    private void playerMove() {
        System.out.println("Player turn");
        System.out.println("pile=" + activePile.get() + " total=" + userDeletions.get() + "\n");

        if (activePile.get() < 0 || userDeletions.get() == 0) {
            System.err.println("Invalid player move");
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
        game = new Nim(PILE_CONFIGURATION);
        AI.setGame(game);

        activePile.set(-1);
        userDeletions.set(0);

        board.initializeTileSurface();
        chooseInitialPlayer();
    }

    public static void main(String[] args) {
        launch(args);
    }
}