package models

case class Node(node_id: Long, hostname: String)

object Node {
  def all(): List[Node] = {
    dummy
  }

  def getByCameraId(id: Long): Node = {
    val node_id = Camera.get(id).node_id
    dummy.find(_.node_id == node_id).get
  }

  val dummy = List(
    Node(1, "localhost"),
    Node(2, "node1")
  )

}
