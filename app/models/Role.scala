package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import anorm.~
import play.api.Play.current

case class Role(id: Long, name: String)

trait RoleService {
  def getId(name: String): Option[Long]
}

class ConcreteRoleService extends RoleService {
  private val roleParser: RowParser[Role] = {
      long("id") ~
      str("name") map {
      case id~name => Role(id, name)
    }
  }

  private val rolesParser: ResultSetParser[List[Role]] = roleParser.*

  def getId(name: String): Option[Long] = DB.withConnection {
    implicit c => SQL("select * from role where name = {name}").on(
      'name -> name
    ).as(rolesParser) match {
      case Nil => None
      case l => Some(l.head.id)
    }
  }
}
