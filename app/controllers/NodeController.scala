package controllers

import models._
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import java.io.File
import play.api.mvc.SimpleResult

case class newCamera(port: Long, device: String, description: String)

class NodeController(_userService: UserService, nodeCamerasService: NodeCamerasService) extends Controller with Secured {
  private val cameraForm = Form(
    mapping(
      "Port" -> longNumber(min = 1024, max = 65535)
        .verifying("Port is already in use", !nodeCamerasService.isPortUsedOnNode(1, _)),
      "Device" -> text()
        .verifying("Device does not exist", new File(_).exists())
        .verifying("Device is already in use", s => !nodeCamerasService.isDeviceUsedOnNode(1, new File(s))),
      "Description" -> text(minLength = 6)
    )(newCamera.apply)(newCamera.unapply)
  )

  def node(id: Long) = isAuthenticated {
    implicit username => implicit request => {
      nodeCamerasService.nodeCameras(id) match {
        case Some(nodeCameras) => Ok(views.html.node(nodeCameras, !openDevicesIsEmpty(id)))
        case _ => Redirect(routes.NodeCamerasController.cameras())
      }
    }
  }

  def newCameraToNode(nodeId: Long) = isAdmin {
    implicit username => implicit request =>
      cameraForm.bindFromRequest().fold(
        errors => {
          addCameraToNodeImpl(nodeId)({ node: Node =>
            BadRequest(views.html.addCameraToNode(node, errors, openDevices(nodeId)))
          })
        },
        value => {
          nodeCamerasService.addCameraToNode(value.port, new File(value.device), value.description, nodeId)
          MotionController.restartMotion()
          Redirect(routes.NodeController.node(nodeId))
        }
      )
  }

  def addCameraToNode(nodeId: Long) = isAdmin {
    implicit username => implicit request => {
      addCameraToNodeImpl(nodeId)({ node: Node =>
        Ok(views.html.addCameraToNode(node, cameraForm, openDevices(nodeId)))
      })
    }
  }

  /**
   * Redirects to node's page if no devices available. Redirects to node cameras page if node is not
   * found. Otherwise uses happyPath's result
   * @param nodeId The node to add the camera to
   * @param happyPath The result if everything is consistent
   * @return
   */
  private def addCameraToNodeImpl(nodeId: Long)(happyPath: (Node) => SimpleResult): SimpleResult = {
    nodeCamerasService.nodeCameras(nodeId) match {
      case Some(nodeCameras) =>
        if(!openDevicesIsEmpty(nodeId)) {
          happyPath(nodeCameras.node)
        } else {
          Redirect(routes.NodeController.node(nodeId))
        }
      case _ => Redirect(routes.NodeCamerasController.cameras())
    }
  }

  private def openDevices(id: Long): List[String] = new File("/dev").listFiles()
    .filter(_.getName.startsWith("video"))
    .filter(!nodeCamerasService.isDeviceUsedOnNode(id, _))
    .map(_.getAbsolutePath)
    .toList

  private def openDevicesIsEmpty(id: Long): Boolean = openDevices(id).isEmpty

  override val userService = _userService
}

object NodeController extends NodeController(new ConcreteUserService(new ConcreteRoleService()), new ConcreteNodeCamerasService(new ConcreteNodeService(), new ConcreteCameraService()))
