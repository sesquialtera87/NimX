package mth.nim

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.HPos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.util.Callback
import mth.nim.EndGameDialog
import java.io.IOException

open class EndGameDialog {

    @FXML
    protected lateinit var button1: Button

    @FXML
    protected lateinit var button2: Button

    @FXML
    protected lateinit var iconViewer: ImageView

    @FXML
    protected lateinit var textArea: Label

    fun setLeftButtonAction(action: Runnable) {
        button1.onAction = EventHandler { action.run() }
    }

    fun setRightButtonAction(action: Runnable) {
        button2.onAction = EventHandler { action.run() }
    }

    companion object {
        @JvmField
        val AI_IMAGE = Image(EndGameDialog::class.java.getResourceAsStream("resources/robot.png"))

        @JvmField
        val PLAYER_IMAGE = Image(EndGameDialog::class.java.getResourceAsStream("resources/badge.png"))

        @JvmStatic
        fun showDialog(callback: Callback<HPos, Void>, icon: Image, text: String) {
            try {
                val fxmlLoader = FXMLLoader(EndGameDialog::class.java.getResource("fxml/EndGamePopup.fxml"))

                val content = fxmlLoader.load<Parent>()
                content.stylesheets.add("mth/nim/resources/style.css")

                val controller = fxmlLoader.getController<EndGameDialog>()
                controller.iconViewer.image = icon
                controller.textArea.text = text

                val dialog = AnimatedDialog(content)
                dialog.openDialog()

                controller.setRightButtonAction {
                    dialog.closeDialog()
                    callback.call(HPos.RIGHT)
                }
                controller.setLeftButtonAction {
                    dialog.closeDialog()
                    callback.call(HPos.LEFT)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}