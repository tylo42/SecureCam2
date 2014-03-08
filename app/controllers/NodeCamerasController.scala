package controllers

import play.api.mvc._
import models.NodeCameras

object NodeCamerasController extends Controller {
  def cameras = Action {
    Ok(views.html.nodeCameras(NodeCameras.all()))
  }
}
