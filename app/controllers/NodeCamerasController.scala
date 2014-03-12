package controllers

import play.api.mvc._
import models.NodeCameras
import controllers.Authentication.Secured

object NodeCamerasController extends Controller with Secured {
  def cameras = withAuth { username => implicit request =>
    Ok(views.html.nodeCameras(Some(username), NodeCameras.all()))
  }
}
