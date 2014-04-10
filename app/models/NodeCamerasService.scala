package models

import java.io.File


case class NodeCameras(node: Node, cameras: List[Camera])

trait NodeCamerasService {
  def addCameraToNode(port: Long, device: File, description: String, nodeId: Long): Unit

  def all(): List[NodeCameras]

  def nodeCameras(nodeId: Long): Option[NodeCameras]

  def getNodeByCameraId(cameraId: Long): Option[Node]

  def getCameraById(cameraId: Long): Option[Camera]

  def isPortUsedOnNode(nodeId: Long, port: Long): Boolean

  def isDeviceUsedOnNode(nodeId: Long, device: File): Boolean

  def deleteCamera(cameraId: Long): Unit
}

class ConcreteNodeCamerasService(nodeService: NodeService, cameraService: CameraService) extends NodeCamerasService {
  def all(): List[NodeCameras] = {
    val cameras = cameraService.all()
    nodeService.all().map(node => {
      NodeCameras(node, cameras.filter(_.nodeId == node.id))
    })
  }

  def nodeCameras(nodeId: Long): Option[NodeCameras] = {
    val cameras = cameraService.all()
    nodeService.get(nodeId) match {
      case None => None
      case Some(node) => Some(NodeCameras(node, cameras.filter(_.nodeId == node.id)))
    }
  }

  def getNodeByCameraId(id: Long): Option[Node] = {
    cameraService.get(id) match {
      case None => None
      case Some(camera) => nodeService.get(camera.nodeId)
    }
  }

  def getCameraById(cameraId: Long): Option[Camera] = cameraService.get(cameraId)

  def addCameraToNode(port: Long, device: File, description: String, nodeId: Long): Unit = {
    cameraService.create(port, device, description, nodeId)
  }

  def isPortUsedOnNode(nodeId: Long, port: Long): Boolean = existsOnNode(nodeId)(_.port == port)

  def isDeviceUsedOnNode(nodeId: Long, device: File): Boolean = existsOnNode(nodeId)(_.device == device)

  private def existsOnNode(nodeId: Long)(p: Camera => Boolean): Boolean = nodeCameras(nodeId).fold(false)(_.cameras.exists(p))

  def deleteCamera(cameraId: Long): Unit = {
    cameraService.delete(cameraId)
  }
}