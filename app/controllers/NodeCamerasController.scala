package controllers

import play.api.mvc._
import models._

class NodeCamerasController(_userService: UserService, nodeCamerasService: NodeCamerasService) extends Controller with Secured {
  def cameras = isAuthenticated {
    implicit username => implicit request =>
      Ok(views.html.nodeCameras(nodeCamerasService.all()))
  }

  override val userService = _userService
}

object NodeCamerasController extends NodeCamerasController(new ConcreteUserService(new ConcreteRoleService()), new ConcreteNodeCamerasService(new ConcreteNodeService(), new ConcreteCameraService()))
