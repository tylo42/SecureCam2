package controllers

import play.api.mvc.{Action, Controller}
import play.api.data.Forms._
import play.api.data.Form
import play.Logger
import models._

case class VideoInsert(time: Long, video: String, event: Long, camera_id: Long)

case class PictureInsert(picture: String, event: Long, camera_id: Long)

class VideoController(_userRoleService: UserRoleService, videoService: VideoService) extends Controller with Secured {
  val videoForm = Form(
    mapping(
      "time" -> longNumber(),
      "video" -> text(),
      "event" -> longNumber(),
      "camera_id" -> longNumber()
    )(VideoInsert.apply)(VideoInsert.unapply)
  )

  val pictureForm = Form(
    mapping(
      "picture" -> text(),
      "event" -> longNumber(),
      "camera_id" -> longNumber()
    )(PictureInsert.apply)(PictureInsert.unapply)
  )

  def newVideo() = Action {
    implicit request =>
      videoForm.bindFromRequest().fold(
        errors => BadRequest,
        value => {
          Logger.info("Adding video: " + value)
          videoService.insertVideo(value.time, value.video, value.event, value.camera_id)
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
          videoService.insertPicture(value.picture, value.event, value.camera_id)
          Ok
        }
      )
  }

  def allVideos() = isAdmin {
    implicit username => implicit request => {
      Ok(views.html.video(videoService.all()))
    }
  }

  override val userRoleService = _userRoleService
}

object VideoController extends VideoController(new ConcreteUserRoleService(new ConcreteUserService(), new ConcreteRoleService()), new ConcreteVideoService())