package mth.nim;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.IntegerProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.Serial;

public class Pile extends HBox {

    public static class DeleteEvent extends Event {

        @Serial
        private static final long serialVersionUID = 1L;
        public int pile;
        public boolean isPileEmpty = false;

        public DeleteEvent(int pile, boolean empty) {
            super(EventType.ROOT);

            this.pile = pile;
            this.isPileEmpty = empty;
        }

    }

    private int pileID;
    public static Event DELETE_EVENT = new Event(EventType.ROOT);
    private IntegerProperty activePile;

    public Pile(int elementsNumber, int id, IntegerProperty activePile) {
        this.pileID = id;
        this.activePile = activePile;

        for (int i = 0; i < elementsNumber; i++) {
            ImageView image = new ImageView(new Image(App.class.getResourceAsStream("icon_bat_64.png")));
            image.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
                if (this.activePile.getValue() < 0) {
                    this.activePile.set(pileID); // lock this pile for future selections
                } else if (this.activePile.getValue() != pileID) // do nothing, this is an invalid move
                    return;

                int imageIndex = getChildren().indexOf(image);

                // only the last image can be removed
                if (imageIndex == getChildren().size() - 1) {
                    ParallelTransition animation = getAnimation(image);
                    animation.playFromStart();
                    animation.setOnFinished(e -> {
                        image.setVisible(false);
                        Pile.this.getChildren().remove(image);
                        fireEvent(new DeleteEvent(pileID, getChildren().size() == 0));
                    });
                }
            });

            this.getChildren().add(image);
        }
    }

    public int getPileId() {
        return pileID;
    }

    public void pop(int n) {
        for (int i = 0; i < n; i++) {
            Node image = getChildren().get(getChildren().size() - 1 - i);
            ParallelTransition animation = getAnimation(image);
            animation.playFromStart();
            animation.setOnFinished(e -> {
                image.setVisible(false);
                getChildren().remove(image);
            });
        }
    }

    private ParallelTransition getAnimation(Node image) {
        FadeTransition t = new FadeTransition(Duration.millis(500), image);
        t.setFromValue(1.0);
        t.setToValue(0.0);
        t.setCycleCount(1);
        t.setAutoReverse(false);

        ScaleTransition st = new ScaleTransition(Duration.millis(500), image);
        st.setAutoReverse(false);
        st.setCycleCount(1);
        st.setToX(3f);
        st.setToY(3f);

        ParallelTransition pt = new ParallelTransition(t, st);
        pt.setCycleCount(1);

        return pt;
    }
}
