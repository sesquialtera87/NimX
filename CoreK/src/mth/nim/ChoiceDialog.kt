package mth.nim

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.HPos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.util.Callback
import java.io.IOException

open class ChoiceDialog {
    @FXML
    protected lateinit var button1: Button

    @FXML
    protected lateinit var button2: Button

    fun setLeftButtonAction(action: Runnable) {
        button1.onAction = EventHandler { action.run() }
    }

    fun setRightButtonAction(action: Runnable) {
        button2.onAction = EventHandler { action.run() }
    }

    companion object {
        @JvmStatic
        fun showDialog(callback: Callback<HPos, Void>) {
            try {
                val fxmlLoader = FXMLLoader(ChoiceDialog::class.java.getResource("fxml/popup.fxml"))
                val content = fxmlLoader.load<Parent>()
                content.stylesheets.add("mth/nim/resources/style.css")

                val controller = fxmlLoader.getController<ChoiceDialog>()

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