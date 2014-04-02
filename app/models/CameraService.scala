package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Camera(id: Long, port: Long, description: String, node_id: Long)

trait CameraService {
  def addCamera(port: Long, description: String, nodeId: Long): Unit

  def all(): List[Camera]

  def get(id: Long): Option[Camera]
}

class ConcreteCameraService extends CameraService {
  private val cameraParser: RowParser[Camera] = {
    long("id") ~
      long("port") ~
      str("description") ~
      long("node_id") map {
      case id ~ port ~ description ~ node_id => Camera(id, port, description, node_id)
    }
  }

  private val camerasParser: ResultSetParser[List[Camera]] = cameraParser.*

  def all(): List[Camera] = DB.withConnection {
    implicit c => {
      SQL("select * from camera order by id").as(camerasParser)
    }
  }

  def get(id: Long): Option[Camera] = DB.withConnection {
    implicit c => {
      SQL("select * from camera where id = {id}").on(
        'id -> id
      ).as(camerasParser) match {
        case Nil => None
        case l => Some(l.head)
      }
    }
  }

  def addCamera(port: Long, description: String, nodeId: Long): Unit = DB.withConnection {
    implicit c => {
      SQL("insert into camera(port, description, node_id) values({port}, {description}, {nodeId})").on(
        'port -> port,
        'description -> description,
        'nodeId -> nodeId
      ).executeUpdate
    }
  }
}