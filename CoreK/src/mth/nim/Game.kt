package mth.nim

import javafx.animation.ParallelTransition
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.scene.layout.VBox

object Game {
    @JvmStatic
    var playerProperty = SimpleObjectProperty(Player.USER)

    @JvmStatic
    var player: Player
        get() = playerProperty.get()
        set(value) = playerProperty.set(value)

    @JvmStatic
    var pileActivated = false

    @JvmStatic
    var board = Board(Tuple(5, 4, 3, 2, 1))

    @JvmStatic
    fun createGameBoard(pileConfiguration: Tuple): Pair<Board, ParallelTransition> {
        val animation = ParallelTransition()

        board = Board(pileConfiguration)
        board.piles.forEach { pile ->
            animation.children.addAll(pile.tiles.map { Tile.fadeInTransition(it) })
        }

        return Pair(board, animation)
    }

    class Board(pileConfiguration: Tuple) : VBox() {
        init {
            padding = Insets(20.0, 20.0, 20.0, 20.0)

            for (i in 0 until pileConfiguration.size)
                children.add(Pile(pileConfiguration[i]))
        }

        val piles get() = children.map { node -> node as Pile }.toList()
    }
}



