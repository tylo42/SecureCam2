package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Node(id: Long, hostname: String)


trait NodeService {
  def all(): List[Node]

  def get(id: Long): Option[Node]
}

class ConcreteNodeService() extends NodeService {
  private val nodeParser: RowParser[Node] = {
    long("id") ~
      str("hostname") map {
      case id ~ hostname => Node(id, hostname)
    }
  }

  private val nodesParser: ResultSetParser[List[Node]] = nodeParser.*


  def all(): List[Node] = DB.withConnection {
    implicit c => {
      SQL("select * from Node order by id").as(nodesParser)
    }
  }

  def get(id: Long): Option[Node] = DB.withConnection {
    implicit c => {
      SQL("select * from Node where id = {id}").on(
        'id -> id
      ).as(nodesParser) match {
        case Nil => None
        case l => Some(l.head)
      }
    }
  }
}
