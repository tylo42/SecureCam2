package models

case class Camera(camera_id: Int, hostname: String, port: Int, description: String)

object Camera {
  def all(): List[Camera] = {
    // dummy data
    List(
      Camera(1, "localhost", 50505, "Front door"),
      Camera(2, "localhost", 50506, "Back door"))
  }

}
