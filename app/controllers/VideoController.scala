package controllers

import play.api.mvc.{Action, Controller}
import play.api.data.Forms._
import play.api.data.Form
import models._

case class VideoInsert(time: Long, video: String, camera_id: Long)

class VideoController(_userRoleService: UserRoleService, videoService: VideoService) extends Controller with Secured {
  val videoForm = Form(
    mapping(
      "time" -> longNumber(),
      "video" -> text(),
      "camera_id" -> longNumber()
    )(VideoInsert.apply)(VideoInsert.unapply)
  )

  def newVideo() = Action {
    implicit request =>
      videoForm.bindFromRequest().fold(
        errors => BadRequest,
        value => {
          videoService.insertVideo(value.time, value.video, value.camera_id)
          Ok
        }
      )
  }

  def newPicture() = ???

  def allVideos() = isAdmin {
    implicit username => implicit request => {
      Ok(views.html.video(videoService.all()))
    }
  }

  override val userRoleService = _userRoleService
}

object VideoController extends VideoController(new ConcreteUserRoleService(new ConcreteUserService(), new ConcreteRoleService()), new ConcreteVideoService())