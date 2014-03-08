package models


case class Node(node_id: Int, hostname: String)

object Node {
  def all(): List[Node] = {
    // dummy data
    List(
      Node(1, "localhost"),
      Node(2, "node1")
    )
  }

}
