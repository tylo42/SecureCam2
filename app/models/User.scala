package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.api.Play.current

case class User(username: String, password: String, salt: String, role_id: Long)

trait UserService {
  def all(): List[User]

  def isEmpty: Boolean

  def get(username: String): Option[User]

  def exists(username: String): Boolean

  def create(user: User): Unit

  def delete(username: String): Unit

  def isSuper(username: String): Boolean

  def isAdmin(username: String): Boolean
}

class ConcreteUserService(roleService: RoleService) extends UserService {
  private val userParser: RowParser[User] = {
    str("username") ~
      str("password") ~
      str("salt") ~
      long("role_id") map {
      case username ~ password ~ salt ~ role_id => User(username, password, salt, role_id)
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

  def create(user: User): Unit = DB.withConnection {
    implicit c =>
      SQL("insert into user (username, password, salt, role_id) values ({username}, {password}, {salt}, {role_id})").on(
        'username -> user.username,
        'password -> user.password,
        'salt -> user.salt,
        'role_id -> user.role_id
      ).executeUpdate()
  }

  def delete(username: String): Unit = {
    if (!isSuper(username)) {
      DB.withConnection {
        implicit c =>
          SQL("delete from user where username = {username}").on(
            'username -> username
          ).executeUpdate()
      }
    }
  }

  def isSuper(username: String): Boolean = isRole(username, "super")

  def isAdmin(username: String): Boolean = isRole(username, "admin")

  private def isRole(username: String, role: String): Boolean = {
    get(username) match {
      case None => false
      case Some(user) => roleService.getName(user.role_id).get.equalsIgnoreCase(role)
    }
  }
}
