package controllers

import play.api.mvc._
import models._

class CameraController(_userService: UserService) extends Controller with Secured {
  def camera(id: Long) = isAuthenticated {
    implicit username => implicit request =>
      Ok(views.html.camera(Camera.get(id), Node.getByCameraId(id)))
  }

  override val userService: UserService = _userService
}

object CameraController extends CameraController(new ConcreteUserService(new ConcreteRoleService()))
