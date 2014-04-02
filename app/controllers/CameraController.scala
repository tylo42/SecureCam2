package controllers

import play.api.mvc._
import models._

class CameraController(_userRoleService: UserRoleService, nodeCamerasService: NodeCamerasService) extends Controller with Secured {
  def camera(id: Long) = isAuthenticated {
    implicit username => implicit request =>
      Ok(views.html.camera(nodeCamerasService.getCameraById(id).get, nodeCamerasService.getNodeByCameraId(id).get))
  }

  override val userRoleService = _userRoleService
}

object CameraController extends CameraController(new ConcreteUserRoleService(new ConcreteUserService, new ConcreteRoleService()), new ConcreteNodeCamerasService(new ConcreteNodeService(), new ConcreteCameraService()))
