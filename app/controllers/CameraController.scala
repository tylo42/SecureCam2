package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.Camera

object CameraController extends Controller {
  def cameras = Action {
    Ok(views.html.cameras(Camera.all()))
  }
}
