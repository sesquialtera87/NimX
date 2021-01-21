package mth.nim

import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox

class Pile(tileCount: Int = 1) : HBox() {

    private val tileEventHandler = EventHandler<Event> {
        if (it.eventType == Tile.REMOVE_EVENT_TYPE) {
            println(it.source)
            children.remove(it.source)
        }
    }

    var active = false

    init {
        alignment = Pos.CENTER

        for (i in 1..tileCount) {
            val newTile = Tile(this)
            newTile.addEventHandler(tileEventHandler)

            children.add(newTile.tile)
        }
    }

    val tiles get() = children.map { node -> node as ImageView }.toList()

}