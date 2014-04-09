import controllers.MotionController
import play.api._
import play.Logger

object Global extends GlobalSettings {
  override def onStart(app: play.api.Application): Unit = {
    Logger.info("Application has started")
    MotionController.writeConfig()
    MotionController.startMotion()
  }

  override def onStop(app: play.api.Application): Unit = {
    MotionController.stopMotion()
    // Give motion a few seconds to shutdown and write out last picture if needed
    Thread.sleep(5000)
  }
}
