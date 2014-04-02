package controllers

import play.api.mvc._
import models._

class NodeCamerasController(_userRoleService: UserRoleService, nodeCamerasService: NodeCamerasService) extends Controller with Secured {
  def cameras = isAuthenticated {
    implicit username => implicit request =>
      Ok(views.html.nodeCameras(nodeCamerasService.all()))
  }

  override val userRoleService = _userRoleService
}

object NodeCamerasController extends NodeCamerasController(new ConcreteUserRoleService(new ConcreteUserService, new ConcreteRoleService()), new ConcreteNodeCamerasService(new ConcreteNodeService(), new ConcreteCameraService()))
