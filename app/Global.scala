import controllers.MotionController
import play.api._
import play.Logger
import scala.sys.process._

object Global extends GlobalSettings {
  override def onStart(app: play.api.Application): Unit = {
    Logger.info("Application has started")
    MotionController.writeConfig()
    MotionController.startMotion()
  }

  override def onStop(app: play.api.Application): Unit = {
    MotionController.stopMotion()
  }
}
