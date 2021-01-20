package mth.nim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class DelayedAction {

    private final Timeline t = new Timeline();

    public DelayedAction(Runnable action, Duration delay) {
        KeyFrame frame = new KeyFrame(delay, e -> action.run());
        t.getKeyFrames().add(frame);
        t.setCycleCount(1);
        t.setAutoReverse(false);
    }

    public void execute() {
        t.playFromStart();
    }

    public static void run(Runnable action, Duration delay) {
        new DelayedAction(action, delay).execute();
    }
}
