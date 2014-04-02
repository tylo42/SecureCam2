package models

import scala.Some

trait UserRoleService {
  def delete(implicit username: String): Unit

  def isSuper(implicit username: String): Boolean

  def isAdmin(implicit username: String): Boolean

  val users: UserService
}

class ConcreteUserRoleService(_userService: UserService, roleService: RoleService) extends UserRoleService {
  def delete(implicit username: String): Unit = {
    if (!isSuper) {
      //users.delete(username)
    }
  }

  def isSuper(implicit username: String): Boolean = isRole("super")

  def isAdmin(implicit username: String): Boolean = isRole("admin")

  private def isRole(role: String)(implicit username: String): Boolean = {
    users.get(username) match {
      case None => false
      case Some(user) => roleService.getName(user.role_id).get.equalsIgnoreCase(role)
    }
  }

  val users = _userService
}
