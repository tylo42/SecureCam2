package models

import play.api.db.DB
import play.api.Play.current
import play.Logger
import anorm._
import anorm.SqlParser._

case class User(id: Long, username: String, password: String, salt: String, roleId: Long)

trait UserService {
  def all(): List[User]

  def isEmpty: Boolean

  def get(username: String): Option[User]

  def exists(username: String): Boolean

  def create(username: String, password: String, salt: String, roleId: Long): Unit

  def delete(username: String): Unit
}

class ConcreteUserService extends UserService {
  private val userParser: RowParser[User] = {
    long("id") ~
    str("username") ~
      str("password") ~
      str("salt") ~
      long("roleId") map {
      case id ~ username ~ password ~ salt ~ roleId => User(id, username, password, salt, roleId)
    }
  }

  private val usersParser: ResultSetParser[List[User]] = userParser.*

  def all(): List[User] = DB.withConnection {
    implicit c => SQL("select * from user order by username asc").as(usersParser)
  }

  def isEmpty: Boolean = all().isEmpty

  def get(username: String): Option[User] = DB.withConnection {
    implicit c =>
      SQL("select * from user where username = {username}").on(
        'username -> username).as(usersParser) match {
        case Nil => None
        case l => Some(l.head)
      }
  }

  def exists(username: String): Boolean = get(username).isDefined

  def create(username: String, password: String, salt: String, roleId: Long): Unit = DB.withConnection {
    implicit c =>
      Logger.info("Created user: " + username)
      SQL("insert into user (username, password, salt, roleId) values ({username}, {password}, {salt}, {roleId})").on(
        'username -> username,
        'password -> password,
        'salt -> salt,
        'roleId -> roleId
      ).executeUpdate()
  }

  def delete(username: String): Unit = DB.withConnection {
    implicit c =>
      Logger.info("Deleting user: " + username)
      SQL("delete from user where username = {username}").on(
        'username -> username
      ).executeUpdate()
  }
}
