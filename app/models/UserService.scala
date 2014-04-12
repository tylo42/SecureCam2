package models

import play.api.db.DB
import play.api.Play.current
import play.Logger
import anorm._
import anorm.SqlParser._
import io.github.nremond.PBKDF2

case class User(id: Long, username: String, password: String, salt: String, role: String)

trait UserService {
  def all(): List[User]

  def isEmpty: Boolean

  def get(username: String): Option[User]

  def exists(username: String): Boolean

  def create(username: String, password: String, role: String): Unit

  def delete(username: String): Unit

  def isSuper(implicit username: String): Boolean

  def isAdmin(implicit username: String): Boolean

  def canDelete(username: String): Boolean
}

class ConcreteUserService(roleService: RoleService) extends UserService {
  private val userParser: RowParser[User] = {
    long("id") ~
    str("username") ~
      str("password") ~
      str("salt") ~
      str("name") map {
      case id ~ username ~ password ~ salt ~ role => User(id, username, password, salt, role)
    }
  }

  private val usersParser: ResultSetParser[List[User]] = userParser.*

  def all(): List[User] = DB.withConnection {
    implicit c => SQL("select user.id, user.username, user.password, user.salt, role.name from user inner join role on user.roleId=role.id order by username asc").as(usersParser)
  }

  def isEmpty: Boolean = all().isEmpty

  def get(username: String): Option[User] = DB.withConnection {
    implicit c =>
      SQL("select user.id, user.username, user.password, user.salt, role.name from user inner join role on user.roleId=role.id where username = {username}").on(
        'username -> username).as(usersParser) match {
        case Nil => None
        case l => Some(l.head)
      }
  }

  def exists(username: String): Boolean = get(username).isDefined

  def create(username: String, password: String, role: String): Unit = DB.withConnection {
    implicit c => {
      roleService.getId(role).foreach { roleId =>
        val salt = RandomStringGenerator(64)
        val hashedPassword = PBKDF2(password, salt)
        Logger.info("Created user: " + username)
        SQL("insert into user (username, password, salt, roleId) values ({username}, {password}, {salt}, {roleId})").on(
          'username -> username,
          'password -> hashedPassword,
          'salt -> salt,
          'roleId -> roleId
        ).executeUpdate()
      }
    }
  }

  def delete(username: String): Unit = {
    if (!isSuper(username)) {
      DB.withConnection {
        implicit c =>
          Logger.info("Deleting user: " + username)
          SQL("delete from user where username = {username}").on(
            'username -> username
          ).executeUpdate()
      }
    }
  }

  def isSuper(implicit username: String): Boolean = isRole("super")

  def isAdmin(implicit username: String): Boolean = isRole("admin")

  def canDelete(username: String): Boolean = !isSuper(username)

  private def isRole(role: String)(implicit username: String): Boolean = {
    get(username) match {
      case None => false
      case Some(user) => user.role.equalsIgnoreCase(role)
    }
  }
}
