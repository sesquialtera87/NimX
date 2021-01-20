package mth.nim;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Stage {

    public static final EventType<Event> MOVE_COMPLETE_EVENT_TYPE = new EventType<>("move_complete");
    @FXML
    public BorderPane board;
    protected StackPane tileSurface;
    protected IntegerProperty activePile = new SimpleIntegerProperty(-1);
    protected IntegerProperty userDeletions = new SimpleIntegerProperty(0);
    protected SimpleObjectProperty<App.Player> player = new SimpleObjectProperty<>();

    private final List<Pile> piles = new ArrayList<>(6);

    public final <T extends Event> void addEventHandler(EventType<T> var1, EventHandler<? super T> var2) {
        board.addEventHandler(var1, var2);
    }

    private <T extends Event> void fireEvent(T event) {
        board.fireEvent(event);
    }

    public void popTiles(int pile, int tileCount) {
        piles.get(pile).pop(tileCount);
    }

    public void initializeTileSurface() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/TileSurface.fxml"));
            tileSurface = fxmlLoader.load();

            board.setCenter(tileSurface);

            AtomicInteger i = new AtomicInteger();
            ((Pane) tileSurface.lookup("#tileSurface")).getChildren().stream()
                    .map(c -> (HBox) c)
                    .forEach(pile -> {
                        Pile p = new Pile(pile, i.getAndIncrement(), activePile, player);
                        p.addEventHandler(EventType.ROOT, e -> {
                            if (e instanceof DeleteEvent) {
                                DeleteEvent evt = (DeleteEvent) e;
                                userDeletions.set(userDeletions.get() + 1);
                                System.out.println(userDeletions);

                                if (evt.isPileEmpty) {
                                    fireEvent(new Event(MOVE_COMPLETE_EVENT_TYPE));
                                }
                            }
                        });
                        piles.add(p);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        private final HBox container;

        public Pile(HBox container, int id, IntegerProperty activePile, ObjectProperty<App.Player> player) {
            this.pileID = id;
            this.container = container;

            for (Node image : container.getChildren()) {
                image.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
                    if (player.get() != App.Player.USER)
                        return;

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

        public void pop(int tileCount) {
            for (int i = 0; i < tileCount; i++) {
                Node image = container.getChildren().get(container.getChildren().size() - 1 - i);

                ParallelTransition animation = getAnimation(image);
                animation.setOnFinished(e -> {
                    image.setVisible(false);
                    container.getChildren().remove(image);

                    if (container.getChildren().isEmpty()) {
                        ((VBox) container.getParent()).getChildren().remove(container);
                        System.out.println("Pile empty, removed from board");
                    }
                });
                animation.playFromStart();
            }
        }

        public final <T extends Event> void addEventHandler(EventType<T> var1, EventHandler<? super T> var2) {
            container.addEventHandler(var1, var2);
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
