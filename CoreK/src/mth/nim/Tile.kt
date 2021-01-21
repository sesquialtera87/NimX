package mth.nim

import javafx.animation.*
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent.MOUSE_CLICKED
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.util.Duration
import javafx.util.Duration.millis
import java.util.*

class Tile(private val parent: Pile) {

    val tile: ImageView = FXMLLoader.load(Tile::class.java.getResource("fxml/Tile.fxml"))

    init {
        tile.opacity = 0.0
        tile.addEventHandler(MOUSE_CLICKED) {
            if (Game.player == Player.USER) {
                if (!Game.pileActivated) {
                    Game.pileActivated = true
                    parent.active = true
                }

                if (parent.active) {
                    fadeOutTransition().apply {
                        onFinished = EventHandler { tile.fireEvent(Event(REMOVE_EVENT_TYPE)) }
                        playFromStart()
                    }
                } else println("Inactive pile. Cannot remove tiles from this pile")
            }
        }
    }

    fun addEventHandler(eventHandler: EventHandler<Event>) {
        tile.addEventHandler(EventType.ROOT, eventHandler)
    }

    fun fadeOutTransition(): ParallelTransition {

        val ph = PathTransition().apply {
            node = tile
            duration = millis(400.0)
            cycleCount = 1
            isAutoReverse = false
            interpolator = Interpolator.EASE_BOTH

            println(tile.layoutBounds)
            println(tile.boundsInLocal)
            println(tile.boundsInParent)
            println(tile.x)
            println(tile.y)
            path = Path().apply {
                elements.addAll(
                    MoveTo(.0, .0),
                    LineTo(100.0, 200.0)
                )

            }
        }

        val t = FadeTransition(millis(500.0), tile).apply {
            fromValue = 1.0
            toValue = 0.0
            cycleCount = 1
            isAutoReverse = false
            interpolator = Interpolator.EASE_IN
        }

        val st = ScaleTransition(millis(500.0), tile).apply {
            isAutoReverse = false
            cycleCount = 1
            toX = 3.0
            toY = 3.0
        }

        val pt = ParallelTransition(t, st, ph)
        pt.cycleCount = 1

        return pt
    }

    companion object {
        val REMOVE_EVENT_TYPE = EventType<Event>("remove")

        fun fadeInTransition(node: Node): FadeTransition {
            val ft = FadeTransition(millis(400.0), node)
            ft.toValue = 1.0
            ft.interpolator = Interpolator.EASE_OUT
            ft.cycleCount = 1
            ft.delay = millis(Random().nextInt(1100).toDouble())
            return ft
        }
    }
}