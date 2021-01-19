package mth.nim

import java.util.Formatter
import java.util.Random
import java.util.Scanner

typealias Action = Tuple

fun Action.pile() = this.elements[0]

fun Action.elementsToRemove() = this.elements[1]

fun Tuple.copyOf() = Tuple(*this.elements)

fun Tuple.countNotEmpty(): Int {
    var n = 0
    for (i in 0 until this.size) if (this[i] != 0) n++

    return n
}

class Nim(var pileConfiguration: Tuple) {

    public val pileCount: Int
        get() = pileConfiguration.size

    fun gameEnded(): Boolean {
        for (n in pileConfiguration.elements) {
            if (n != 0) return false
        }

        return true
    }

    fun actualState() = pileConfiguration.copyOf()

    fun update(action: Action): Tuple {
        val remaining = pileConfiguration[action.pile()] - action.elementsToRemove()

        if (remaining < 0) throw IllegalArgumentException()

        pileConfiguration[action.pile()] = remaining

        return actualState()
    }

    companion object {

        @JvmStatic
        fun availableActions(state: Tuple): List<Action> {
            val actions = mutableListOf<Action>()

            for (k in 0 until state.size) for (i in 1..state[k]) actions.add(Action(k, i))

            return actions
        }
    }
}

class NimAi(var game: Nim) {

    companion object {
        const val AI = 0
        const val PLAYER = 1
        const val ACTION = "action"
        const val STATE = "state"
    }

    val last = Array(2) { mutableMapOf<String, Tuple>() }

    val rnd = Random()
    var q = Q()

    /**
     * The value rules the choiche of the next move during the game. With probability [epsilon] the
     * AI choose a move at random, otherwise it choose a move among those with the highest Q value
     */
    var epsilon = 0.7

    fun learn(oldState: Tuple, newState: Tuple, action: Action, player: Int) {
        last[player][STATE] = oldState
        last[player][ACTION] = action

        val nonEmptyPiles = newState.countNotEmpty()

        if (game.gameEnded()) {
            if (player == AI) {
                updateQ(last[PLAYER][STATE]!!, last[PLAYER][ACTION]!!, newState, -1.0)
            } else {
                updateQ(last[AI][STATE]!!, last[AI][ACTION]!!, newState, -1.0)
            }

            updateQ(oldState, action, newState, +1.0)
        } else if (player == AI) {
            if (nonEmptyPiles == 1) updateQ(oldState, action, newState, -0.4)
            else updateQ(oldState, action, newState, 0.0)
        }

        println(q)
    }

    fun move(): Action {
        if (rnd.nextDouble() < epsilon) return randomMove() else return bestAvaliableMove()
    }

    fun bestAvaliableMove(): Action {
        println("Best move choosed")

        val actions = Nim.availableActions(game.pileConfiguration)

        var bestQ = -100000.0
        var bestAction: Action = Action()
        var qValue: Double

        actions.forEach { action ->
            qValue = q.getQ(game.pileConfiguration, action)
            if (qValue > bestQ) {
                bestQ = qValue
                bestAction = action
            }
        }

        return bestAction
    }

    /** Return a move, choose at random, from the avaliable in the actual state */
    fun randomMove(): Action {
        val actions = Nim.availableActions(game.pileConfiguration)
        return actions[rnd.nextInt(actions.size)]
    }

    fun updateQ(state: Tuple, action: Action, newState: Tuple, reward: Double) {
        q.update(state, action, newState, reward)
    }

    fun train(n: Int) {
        val config = game.pileConfiguration.copyOf()
        val last = arrayOf(mutableMapOf<String, Tuple>(), mutableMapOf<String, Tuple>())
        q.alpha = 0.5

        for (i in 0..n) {
            var player = rnd.nextInt(2) // the starting player

            game.pileConfiguration = config.copyOf()
            var previousState: Tuple
            var action: Action
            var newState: Tuple

            while (!game.gameEnded()) {
                previousState = game.actualState()
                action = move()
                newState = game.update(action)
                val nonEmptyPiles = newState.countNotEmpty()

                last[player]["state"] = previousState
                last[player]["action"] = action

                if (game.gameEnded()) {
                    if (player == AI) {
                        updateQ(previousState, action, newState, +1.0)
                        updateQ(last[PLAYER]["state"]!!, last[PLAYER]["action"]!!, newState, -1.0)
                    } else {
                        updateQ(last[AI]["state"]!!, last[AI]["action"]!!, newState, -1.0)
                        updateQ(previousState, action, newState, +1.0)
                    }
                } else if (player == AI) {
                    if (nonEmptyPiles == 1) updateQ(previousState, action, newState, -0.5)
                    else updateQ(previousState, action, newState, 0.0)
                }

                player = (player + 1) % 2 // switch the player in turn
            }
        }
    }

    fun exportKnowledge(path: String) {
        val writer: Formatter = Formatter(path)

        q.q.forEach { state, actionMap ->
            actionMap.forEach { action, value -> writer.format("%s@%s@%f\n", state, action, value) }
        }

        writer.close()
    }

    fun importKnowledge(path: String) {
        val input = Scanner(java.io.File(path))
        val q = Q()

        while (input.hasNext()) {
            val line = input.nextLine()
            if (line.isBlank()) continue

            val tokens = line.split("@")

            val state = Tuple.parse(tokens[0])
            val action = Tuple.parse(tokens[1])
            val qValue = tokens[2].replace(",", ".").toDouble()

            q.setQ(state, action, qValue)
        }

        this.q = q
        println(q)
    }
}

fun interactive() {
    val input: Scanner = Scanner(System.`in`)
    val game = Nim(Tuple(1, 2, 1))
    val ai = NimAi(game)

    while (true) {
        println("Play?")

        if (input.next().toLowerCase() == "y") {
            val USER = 0
            val AI = 1
            var player = Random().nextInt(2)
            val last = mutableMapOf<String, Tuple>()
            game.pileConfiguration = Tuple(1, 2, 1)

            while (game.gameEnded().not()) {
                println("@@@ " + game.pileConfiguration)

                when (player) {
                    USER -> {
                        println("Enter pile and elements:")
                        val tokens = input.next().split(",")
                        val pile = tokens[0].toInt()
                        val elements = tokens[1].toInt()

                        game.update(Action(pile, elements))
                        player = AI

                        if (game.gameEnded())
                                ai.updateQ(
                                        last["state"]!!, last["action"]!!, game.actualState(), -2.0)
                    }
                    AI -> {
                        val action = ai.move()
                        last["state"] = game.actualState()
                        last["action"] = action
                        val state = game.update(action)

                        if (game.gameEnded()) {
                            ai.updateQ(last["state"]!!, last["action"]!!, state, 1.0)
                        } else if (state.countNotEmpty() == 1)
                                ai.updateQ(last["state"]!!, last["action"]!!, state, -1.0)
                        else ai.updateQ(last["state"]!!, last["action"]!!, state, 0.0)

                        println("AI moved")
                        println(ai.q)
                        println()

                        player = USER
                    }
                }
            }
        } else return
    }
}

fun main() {
    val input: Scanner = Scanner(System.`in`)
    while (input.hasNext()) {
        val n = input.next().toInt()
        if (n == 0) break

        NimAi(Nim(Tuple(1, 2, 1))).apply {
            train(n)

            exportKnowledge("C:\\Users\\utente\\Documents\\Java\\nim\\Nim\\src\\mth\\nim\\kn.txt")
        }
    }

    // interactive()
}
