package mth.nim

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.util.Duration


class DelayedAction(action: Runnable, delay: Duration) {

    private val t: Timeline

    fun execute() {
        t.playFromStart()
    }

    companion object {
        @JvmStatic
        fun run(action: Runnable, delay: Duration) {
            DelayedAction(action, delay).execute()
        }
    }

    init {
        val frame = KeyFrame(delay, { action.run() })
        t = Timeline().apply {
            keyFrames.add(frame)
            cycleCount = 1
            isAutoReverse = false
        }

    }
}