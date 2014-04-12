package models

import scala.Some
import io.github.nremond.PBKDF2

case class UserRole(username: String, role: String, canDelete: Boolean)

trait UserRoleService {
  def createUser(username: String, password: String, role: String): Unit

  def getUser(username: String): Option[User]

  def deleteUser(implicit username: String): Unit

  def allUsers(): List[User]

  def userExists(username: String): Boolean

  def usersIsEmpty(): Boolean

  def userRoles(): List[UserRole]

  def isSuper(implicit username: String): Boolean

  def isAdmin(implicit username: String): Boolean
}

class ConcreteUserRoleService(userService: UserService, roleService: RoleService) extends UserRoleService {
  def createUser(username: String, password: String, role: String): Unit = {
    roleService.getId(role).map {
      val salt = RandomStringGenerator(64)
      val hashedPassword = PBKDF2(password, salt)
      userService.create(username, hashedPassword, salt, _)
    }
  }

  def getUser(username: String): Option[User] = userService.get(username)

  def deleteUser(implicit username: String): Unit = {
    if (!isSuper) {
      userService.delete(username)
    }
  }

  def allUsers(): List[User] = userService.all()

  def userExists(username: String): Boolean = userService.exists(username)

  def usersIsEmpty(): Boolean = userService.isEmpty

  def userRoles(): List[UserRole] = {
    userService.all().map(user => {
      UserRole(user.username, roleService.getName(user.roleId).get, !isSuper(user.username))
    })
  }

  def isSuper(implicit username: String): Boolean = isRole("super")

  def isAdmin(implicit username: String): Boolean = isRole("admin")

  private def isRole(role: String)(implicit username: String): Boolean = {
    userService.get(username) match {
      case None => false
      case Some(user) => roleService.getName(user.roleId).get.equalsIgnoreCase(role)
    }
  }
}
