import controllers.MotionController
import play.api._
import play.Logger

object Global extends GlobalSettings {
  override def onStart(app: play.api.Application): Unit = {
    Logger.info("Application has started")
    MotionController.writeConfig()
  }
}
