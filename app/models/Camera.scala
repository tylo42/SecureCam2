package models

case class Camera(camera_id: Long, port: Long, description: String, node_id: Long)

object Camera {
  def all(): List[Camera] = {
    dummy
  }

  def get(id: Long): Camera = {
    dummy.find(_.camera_id == id).get
  }


  val dummy = List(
    Camera(1, 50505, "Front door", 1),
    Camera(2, 50506, "Back door", 2),
    Camera(3, 50506, "Living room", 2)
  )

}
