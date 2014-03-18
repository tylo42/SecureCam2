package controllers

import play.api.mvc._
import models._
import scala.Some

class CameraController(_userService: UserService) extends Controller with Secured {
  def camera(id: Long) = isAuthenticated {
    username => implicit request =>
      Ok(views.html.camera(Some(username), Camera.get(id), Node.getByCameraId(id)))
  }

  override val userService: UserService = _userService
}

object CameraController extends CameraController(new ConcreteUserService(new ConcreteRoleService()))
