package controllers

import play.api.mvc._
import models.{Node, Camera}

object CameraController extends Controller {
  def camera(id: Long) = Action {
    Ok(views.html.camera(Camera.get(id), Node.getByCameraId(id)))
  }

}
