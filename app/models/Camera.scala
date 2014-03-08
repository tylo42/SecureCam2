package models

case class Camera(camera_id: Int, port: Int, description: String, node_id: Int)

object Camera {
  def all(): List[Camera] = {
    // dummy data
    List(
      Camera(1, 50505, "Front door", 1),
      Camera(2, 50506, "Back door", 1)
    )
  }

}
