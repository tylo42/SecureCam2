package models

case class NodeCameras(node: Node, cameras: List[Camera])

object NodeCameras {
  def all(): List[NodeCameras] = {
    List(
      NodeCameras(Node(1, "localhost"),
        List(Camera(1, 50505, "Front door", 1))
      ),
      NodeCameras(Node(2, "node1"),
        List(
          Camera(2, 50506, "Back door", 2),
          Camera(3, 50505, "Living room", 2)
        )
      )
    )
  }
}
