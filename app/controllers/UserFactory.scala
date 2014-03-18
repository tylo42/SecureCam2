package controllers

import models.{RoleService, User}
import io.github.nremond.PBKDF2

trait UserFactory {
  def apply(username: String, password: String, role: String): User
}

class ConcreteUserFactory(roleService: RoleService) extends UserFactory {
  def apply(username: String, password: String, role: String): User = {
    val salt = RandomStringGenerator(64)
    val hashedPassword = PBKDF2(password, salt)
    User(username, hashedPassword, salt, roleService.getId(role).get)
  }
}
