package mth.nim

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.util.Duration

/**
 * Execute a [Runnable] action after a specified delay
 */
infix fun Runnable.runDelayed(delay: Duration) {
    Timeline().apply {
        keyFrames.add(KeyFrame(delay, { run() }))
        cycleCount = 1
        isAutoReverse = false
    }.run { playFromStart() }
}