package controllers

import models._
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import java.io.File

case class newCamera(port: Long, device: String, description: String)

class NodeController(_userRoleService: UserRoleService, nodeCamerasService: NodeCamerasService) extends Controller with Secured {
  private val cameraForm = Form(
    mapping(
      "Port" -> longNumber(min = 1024, max = 65535)
        .verifying("Port is already in use", !nodeCamerasService.isPortUsedOnNode(1, _)),
      "Device" -> text()
        .verifying("Device does not exist", new File("/dev", _).exists())
        .verifying("Device is already in use", s => !nodeCamerasService.isDeviceUsedOnNode(1, new File("/dev", s))),
      "Description" -> text(minLength = 6)
    )(newCamera.apply)(newCamera.unapply)
  )

  def node(id: Long) = isAuthenticated {
    implicit username => implicit request =>
      Ok(views.html.node(nodeCamerasService.nodeCameras(id).get, cameraForm))
  }

  def addCameraToNode(nodeId: Long) = isAdmin {
    implicit username => implicit request =>
      cameraForm.bindFromRequest().fold(
        errors => BadRequest(views.html.node(nodeCamerasService.nodeCameras(nodeId).get, errors)),
        value => {
          nodeCamerasService.addCameraToNode(value.port, new File("/dev", value.device), value.description, nodeId)
          MotionController.restartMotion()
          Redirect(routes.NodeController.node(nodeId))
        }
      )
  }

  override val userRoleService = _userRoleService
}

object NodeController extends NodeController(new ConcreteUserRoleService(new ConcreteUserService, new ConcreteRoleService()), new ConcreteNodeCamerasService(new ConcreteNodeService(), new ConcreteCameraService()))
