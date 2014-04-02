package controllers

import play.api.mvc._
import models._

class CameraController(_userService: UserService, nodeCamerasService: NodeCamerasService) extends Controller with Secured {
  def camera(id: Long) = isAuthenticated {
    implicit username => implicit request =>
      Ok(views.html.camera(nodeCamerasService.getCameraById(id).get, nodeCamerasService.getNodeByCameraId(id).get))
  }

  override val userService: UserService = _userService
}

object CameraController extends CameraController(new ConcreteUserService(new ConcreteRoleService()), new ConcreteNodeCamerasService(new ConcreteNodeService(), new ConcreteCameraService()))
