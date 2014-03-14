package controllers

import models.User
import io.github.nremond.PBKDF2

trait UserFactory {
  def apply(username: String, password: String): User
}

class ConcreteUserFactory extends UserFactory{
  def apply(username: String, password: String): User = {
    val salt = RandomStringGenerator(64)
    val hashedPassword = PBKDF2(password, salt)
    User(username, hashedPassword, salt)
  }
}
