package mth.nim

import javafx.animation.Interpolator
import javafx.animation.ScaleTransition
import javafx.animation.SequentialTransition
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import kotlin.math.pow


class AnimatedDialog(content: Parent) : Stage() {

    companion object {
        private val EXP_IN: Interpolator = object : Interpolator() {
            override fun curve(t: Double): Double {
                return if (t == 1.0) 1.0 else 1 - 2.0.pow(-10 * t)
            }
        }
        private val EXP_OUT: Interpolator = object : Interpolator() {
            override fun curve(t: Double): Double {
                return if (t == 0.0) 0.0 else 2.0.pow(10 * (t - 1))
            }
        }
    }

    private val scale1 = ScaleTransition()
    private val scale2 = ScaleTransition()
    private val anim = SequentialTransition(scale1, scale2)

    fun openDialog() {
        show()
        anim.play()
    }

    fun closeDialog() {
        anim.onFinished = EventHandler { close() }
        anim.isAutoReverse = true
        anim.cycleCount = 2
        anim.playFrom(Duration.seconds(0.66))
    }

    init {
        with(scale1) {
            fromX = 0.01
            fromY = 0.01
            toY = 1.0
            duration = Duration.seconds(0.33)
            interpolator = EXP_IN
            node = content
        }

        scale2.apply {
            fromX = 0.01
            toX = 1.0
            duration = Duration.seconds(0.33)
            interpolator = EXP_OUT
            node = content
        }

        initStyle(StageStyle.TRANSPARENT)
        initModality(Modality.APPLICATION_MODAL)

        scene = Scene(content, null)
    }
}

