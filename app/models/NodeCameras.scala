package models


case class NodeCameras(node: Node, cameras: List[Camera])

trait NodeCamerasService {
  def addCameraToNode(port: Long, description: String, nodeId: Long): Unit

  def all(): List[NodeCameras]

  def nodeCameras(nodeId: Long): Option[NodeCameras]

  def getNodeByCameraId(cameraId: Long): Option[Node]

  def getCameraById(cameraId: Long): Option[Camera]
}

class ConcreteNodeCamerasService(nodeService: NodeService, cameraService: CameraService) extends NodeCamerasService {
  def all(): List[NodeCameras] = {
    val cameras = cameraService.all()
    nodeService.all().map(node => {
      NodeCameras(node, cameras.filter(_.node_id == node.id))
    })
  }

  def nodeCameras(nodeId: Long): Option[NodeCameras] = {
    val cameras = cameraService.all()
    nodeService.get(nodeId) match {
      case None => None
      case Some(node) => Some(NodeCameras(node, cameras.filter(_.node_id == node.id)))
    }
  }

  def getNodeByCameraId(id: Long): Option[Node] = {
    cameraService.get(id) match {
      case None => None
      case Some(camera) => nodeService.get(camera.node_id)
    }
  }

  def getCameraById(cameraId: Long): Option[Camera] = cameraService.get(cameraId)

  def addCameraToNode(port: Long, description: String, nodeId: Long): Unit = {
    cameraService.addCamera(port, description, nodeId)
  }
}