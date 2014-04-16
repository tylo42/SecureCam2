package controllers

import play.api.mvc.{Action, Controller}
import play.api.data.Forms._
import play.api.data.Form
import play.Logger
import models._
import java.io.File
import play.mvc.BodyParser.AnyContent
import org.joda.time.{Duration, Interval, Instant}

case class VideoInsert(time: Long, video: String, event: Long, cameraId: Long)

case class PictureInsert(picture: String, event: Long, cameraId: Long)

class VideoController(_userService: UserService, videoService: VideoService) extends Controller with Secured {
  val videoForm = Form(
    mapping(
      "time" -> longNumber(),
      "video" -> text(),
      "event" -> longNumber(),
      "cameraId" -> longNumber()
    )(VideoInsert.apply)(VideoInsert.unapply)
  )

  val pictureForm = Form(
    mapping(
      "picture" -> text(),
      "event" -> longNumber(),
      "cameraId" -> longNumber()
    )(PictureInsert.apply)(PictureInsert.unapply)
  )

  def newVideo() = Action {
    implicit request =>
      videoForm.bindFromRequest().fold(
        errors => BadRequest,
        value => {
          Logger.info("Adding video: " + value)
          videoService.insertVideo(value.time, getRelativePath(value.video), value.event, value.cameraId)
          Ok
        }
      )
  }

  def newPicture() = Action {
    implicit request =>
      pictureForm.bindFromRequest().fold(
        errors => BadRequest,
        value => {
          Logger.info("Adding picture: " + value)
          videoService.insertPicture(getRelativePath(value.picture), value.event, value.cameraId)
          Ok
        }
      )
  }

  def allVideos() = isAdmin {
    implicit username => implicit request => {
      Ok(views.html.video(videoService.getBetweenInterval(new Interval(Instant.now().minus(Duration.standardDays(1)), Instant.now()), None)))
    }
  }

  val AbsolutePath = """^(/|[a-zA-Z]:\\).*""".r

  def assetAt(file: String) = isAdmin {
    implicit username => implicit request => {
      val fileToServe = new File(MotionController.videoDirectory, file)

      if (fileToServe.exists) {
        Ok.sendFile(fileToServe, inline = true)
      } else {
        Logger.error("Failed to find file: " + file)
        NotFound
      }
    }
  }

  override val userService = _userService

  private def getRelativePath(path: String): String = MotionController.videoDirectory.toURI.relativize(new File(path).toURI).getPath
}

object VideoController extends VideoController(new ConcreteUserService(new ConcreteRoleService()), new ConcreteVideoService())