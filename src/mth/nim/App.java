package mth.nim;

import static mth.nim.App.Player.AI;
import static mth.nim.App.Player.USER;

import java.util.Optional;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import mth.nim.Pile.DeleteEvent;

public class App extends javafx.application.Application {

    public enum Player {
        AI, USER
    }

    public static final Tuple PILE_CONFIGURATION = new Tuple(1, 2, 3, 4, 5);

    private Player player = USER;
    private final IntegerProperty pileChoice = new SimpleIntegerProperty(-1);
    private final BooleanProperty gameComplete = new SimpleBooleanProperty(false);
    private int userDeletions = 0;

    private mth.nim.Stage controller;

    Pile[] piles;

    Nim nim = new Nim(new Tuple(1, 2, 3, 4, 5));
    Label statusLabel = new Label();
    NimAi ai = new NimAi(nim);
    Timeline aiTime;
    BorderPane gameScene = new BorderPane();

    @Override
    public void start(Stage stage) throws Exception {
        gameComplete.addListener(c -> {
            if (gameComplete.get()) {
                Platform.runLater(() -> {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    Optional<Integer> result = GameDialog.gameCompleteDialog(player).showAndWait();
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
        aiTime.getKeyFrames().add(new KeyFrame(Duration.millis(300), e -> {
            AiMove();
        }));

        gameScene.setPadding(new Insets(0, 40, 0, 40));
        gameScene.setStyle("-fx-background-color: #ffe6cc");
        gameScene.addEventHandler(KeyEvent.KEY_RELEASED, evt -> {
            if (evt.getCode() == KeyCode.P) {
                if (player == USER) {
                    playerMove();
                    evt.consume();
                }
                System.out.println("P pressed");
            }
        });

        ColorPicker cp = new ColorPicker();
        cp.setOnMouseClicked(e -> {
            String value = cp.getValue().toString();
            gameScene.setStyle("-fx-background-color: " + value.replace("0x", "#"));
        });

        // pane.setCenter(pileNode);
        gameScene.setTop(new HBox(cp));
        gameScene.setBottom(statusLabel);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("stage.fxml"));
        Parent node = fxmlLoader.load();
        controller = fxmlLoader.getController();


        Scene scene = new Scene(node, 400, 400);
        scene.getStylesheets().add("mth/nim/res/style.css");
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
        stage.setOnShown(e -> {
            GameDialog d = GameDialog.gameOnStartDialog();
            d.initOwner(stage.getOwner());
            d.showAndWait().ifPresent(result -> {
                if (result == GameDialog.QUIT)
                    Platform.exit();
                else if (result == GameDialog.START_GAME)
                    gameScene.setCenter(buildScene1());
            });
        });
        stage.show();

    }

    private void AiMove() {
        System.out.println("AI turn");

        Tuple oldState = nim.actualState();
        Tuple aiMove = ai.move();
        int PILE = aiMove.get(0);
        int ELEMENTS_TO_REMOVE = aiMove.get(1);

        Tuple state = nim.update(aiMove);
        ai.learn(oldState, state, aiMove, NimAi.AI);

        System.out.println(gameComplete);

        piles[PILE].pop(ELEMENTS_TO_REMOVE);
        player = USER;

        gameComplete.set(nim.gameEnded());
    }

    private void playerMove() {
        System.out.println("Player turn");
        System.out.println("pile=" + pileChoice.get() + " total=" + userDeletions + "\n");

        // get the total deletion of the player and build a move
        Tuple oldState = nim.actualState();
        Tuple userMove = new Tuple(pileChoice.get(), userDeletions);
        Tuple state = nim.update(userMove);

        ai.learn(oldState, state, userMove, NimAi.PLAYER);

        gameComplete.set(nim.gameEnded());

        System.out.println(nim.getPileConfiguration());

        if (gameComplete.not().get()) {
            userDeletions = 0;
            pileChoice.set(-1); // remove the pile lock

            player = AI;
            aiTime.playFromStart();
        }
    }

    private void startNewGame() {
        nim = new Nim(PILE_CONFIGURATION);
        ai.setGame(nim);
        pileChoice.set(-1);
        userDeletions = 0;

        gameScene.setTop(buildScene1());
        gameScene.requestFocus();
    }

    private void updateStatus() {
        String[] binaries = new String[nim.getPileCount()];
        int stringLength = 0;

        for (int i = 0; i < nim.getPileCount(); i++) {
            binaries[i] = Integer.toBinaryString(nim.getPileConfiguration().get(i));
            stringLength = Math.max(stringLength, binaries[i].length());
        }

        for (int j = 0; j < binaries.length; j++) {
            String s = binaries[j];

            if (s.length() < stringLength) {
                StringBuilder b = new StringBuilder(s);

                for (int i = 0; i < stringLength - s.length(); i++)
                    b.insert(0, "0");

                binaries[j] = b.toString();
            }
        }

        statusLabel.setText(Util.nimSum(binaries));
    }

    private Node buildScene1() {
        piles = new Pile[nim.getPileCount()];
        VBox pane = new VBox();
        Tuple state = nim.getPileConfiguration();
        // items = new HashMap<>(nim.getPileCount());

        for (int pile = 0; pile < state.getSize(); pile++) {
            Pile p = new Pile(state.get(pile), pile, pileChoice);
            p.addEventHandler(EventType.ROOT, evt -> {
                if (evt instanceof DeleteEvent) {
                    System.out.println("User click for deletion");

                    if (userDeletions < nim.getPileConfiguration().get(p.getPileId()))
                        userDeletions++;

                    System.out.println(((DeleteEvent) evt).isPileEmpty);

                    if (((DeleteEvent) evt).isPileEmpty) {
                        /* if the pile is empty, automatically switch to the next AI move */
                        playerMove();
                    }

                    System.out.println(nim.getPileConfiguration());
                }
            });
            pane.getChildren().add(p);

            piles[pile] = p;
        }

        return pane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}