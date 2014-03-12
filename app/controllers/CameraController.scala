package controllers

import play.api.mvc._
import models.{Node, Camera}
import controllers.Authentication.Secured

object CameraController extends Controller with Secured {
  def camera(id: Long) = withAuth { username => implicit request =>
    Ok(views.html.camera(Some(username), Camera.get(id), Node.getByCameraId(id)))
  }

}
