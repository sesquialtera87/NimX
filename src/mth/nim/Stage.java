package mth.nim;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Stage {
    @FXML
    VBox gameBoard;
    IntegerProperty activePile = new SimpleIntegerProperty(-1);

    private final List<Pile> piles = new ArrayList<>(6);

    public void initialize() {
        AtomicInteger i = new AtomicInteger();
        gameBoard.getChildren().stream()
                .map(c -> (HBox) c)
                .forEach(pile -> {
                    Pile p = new Pile(pile, i.getAndIncrement(), activePile);
                    piles.add(p);
                });
    }

    public static class DeleteEvent extends Event {

        public int pile;
        public boolean isPileEmpty;

        public DeleteEvent(int pile, boolean empty) {
            super(EventType.ROOT);

            this.pile = pile;
            this.isPileEmpty = empty;
        }

    }

    static class Pile {
        private final int pileID;
        public static Event DELETE_EVENT = new Event(EventType.ROOT);

        public Pile(HBox container, int id, IntegerProperty activePile) {
            this.pileID = id;

            for (Node image : container.getChildren()) {
                image.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
                    if (activePile.getValue() < 0) {
                        activePile.set(pileID); // lock this pile for future selections
                    } else if (activePile.getValue() != pileID) // do nothing, this is an invalid move
                        return;

                    ParallelTransition animation = getAnimation(image);
                    animation.playFromStart();
                    animation.setOnFinished(e -> {
                        image.setVisible(false);
                        container.getChildren().remove(image);

                        if (container.getChildren().isEmpty()) {
                            ((VBox) container.getParent()).getChildren().remove(container);
                            System.out.println("Pile empty, removed from board");
                        }
                        container.fireEvent(new DeleteEvent(pileID, container.getChildren().size() == 0));
                    });
                });
            }
        }

        public int getPileId() {
            return pileID;
        }

        private static ParallelTransition getAnimation(Node image) {
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

}
