package mth.nim;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import mth.nim.App.Player;

public class GameDialog extends Dialog<Integer> {

    public static final int START_GAME = 0;
    public static final int QUIT = -1;
    private static final Font font = Font.loadFont(App.class.getResourceAsStream("BRLNSDT.TTF"), 16.0);

    public static final int START_DIALOG = -432;
    public static final int GAME_COMPLETE_DIALOG = 542;

    public GameDialog() {
        super();

        initStyle(StageStyle.UNDECORATED);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static GameDialog gameCompleteDialog(Player player) {
        GameDialog d = new GameDialog();

        ButtonType playButton = new ButtonType("PLAY AGAIN");
        d.getDialogPane().getButtonTypes().add(playButton);

        ButtonType quitButton = new ButtonType("QUIT GAME");
        d.getDialogPane().getButtonTypes().add(quitButton);

        ButtonBar buttonBar = (ButtonBar) d.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().stream().map(e -> (Button) e).forEach(button -> button.setFont(font));

        d.setResultConverter(button -> {
            Integer result = null;

            if (button == playButton) {
                result = START_GAME;
            } else if (button == quitButton)
                result = QUIT;

            return result;
        });

        String text = null;

        if (player == Player.AI)
            text = "GAME OVER. AI WINS!";
        else if (player == Player.USER)
            text = "AI DOWN. YOU WIN!";

        Label label = new Label(text);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(font);

        d.getDialogPane().setContent(label);

        return d;
    }

    public static GameDialog gameOnStartDialog() {
        GameDialog d = new GameDialog();
        d.getDialogPane().setContent(playScene(d));

        return d;
    }

    private static Node playScene(GameDialog d) {
        ButtonType playButton = new ButtonType("PLAY");
        d.getDialogPane().getButtonTypes().add(playButton);

        ButtonType quitButton = new ButtonType("QUIT");
        d.getDialogPane().getButtonTypes().add(quitButton);

        ButtonBar buttonBar = (ButtonBar) d.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().stream().map(e -> (Button) e).forEach(button -> button.setFont(font));

        d.setResultConverter(button -> {
            Integer result = null;

            if (button == playButton) {
                result = START_GAME;
            } else if (button == quitButton)
                result = QUIT;

            return result;
        });

        return new HBox();
    }
}
