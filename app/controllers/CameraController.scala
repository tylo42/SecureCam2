package controllers

import play.api.mvc._
import models._

class CameraController(_userRoleService: UserRoleService, nodeCamerasService: NodeCamerasService) extends Controller with Secured {
  def camera(id: Long) = isAuthenticated {
    implicit username => implicit request =>
      (nodeCamerasService.getCameraById(id), nodeCamerasService.getNodeByCameraId(id)) match {
        case (Some(camera), Some(node)) => Ok(views.html.camera(camera, node))
        case _ => Redirect(routes.NodeCamerasController.cameras())
      }
  }

  def delete(id: Long) = isAdmin {
    implicit username => implicit request => {
      nodeCamerasService.deleteCamera(id)
      MotionController.restartMotion()
      Redirect(routes.NodeCamerasController.cameras())
    }
  }

  override val userRoleService = _userRoleService
}

object CameraController extends CameraController(new ConcreteUserRoleService(new ConcreteUserService, new ConcreteRoleService()), new ConcreteNodeCamerasService(new ConcreteNodeService(), new ConcreteCameraService()))
