package controllers

import play.api.mvc.{Action, Controller}
import play.api.data.Forms._
import play.api.data.Form
import models.{ConcreteVideoService, VideoService}

case class VideoInsert(time: Long, video: String, camera_id: Long)

class VideoController(videoService: VideoService) extends Controller {
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

  def allVideos() = Action {
    Ok(views.html.video(videoService.all()))
  }

}

object VideoController extends VideoController(new ConcreteVideoService())