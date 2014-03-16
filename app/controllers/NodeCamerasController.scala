package controllers

import play.api.mvc._
import models.{ConcreteRoleService, ConcreteUserService, UserService, NodeCameras}

class NodeCamerasController(_userService: UserService) extends Controller with Secured {
  def cameras = isAuthenticated { username => implicit request =>
    Ok(views.html.nodeCameras(Some(username), NodeCameras.all()))
  }

  override val userService: UserService = _userService
}

object NodeCamerasController extends NodeCamerasController(new ConcreteUserService(new ConcreteRoleService()))
