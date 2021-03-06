package controllers

import play.api.mvc._
import models._

class CameraController(_userService: UserService, nodeCamerasService: NodeCamerasService, videoService: VideoService) extends Controller with Secured {
  def camera(id: Long) = isAuthenticated {
    implicit username => implicit request =>
      (nodeCamerasService.getCameraById(id), nodeCamerasService.getNodeByCameraId(id)) match {
        case (Some(camera), Some(node)) => Ok(views.html.camera(camera, node, videoService.getMostRecentVideo(id)))
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

  override val userService = _userService
}

object CameraController extends CameraController(
  new ConcreteUserService(new ConcreteRoleService()),
  new ConcreteNodeCamerasService(new ConcreteNodeService(), new ConcreteCameraService()),
  new ConcreteVideoService)
