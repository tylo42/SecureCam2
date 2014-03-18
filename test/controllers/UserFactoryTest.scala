package controllers

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import models.RoleService
import io.github.nremond.PBKDF2

class UserFactoryTest extends Specification with Mockito {
  val roleService = mock[RoleService]

  val testObject = new ConcreteUserFactory(roleService)

  "Apply" should {
    "create users" in {
      roleService.getId("role") returns Option(255)

      val user = testObject("username", "password", "role")

      user.username must equalTo("username")
      PBKDF2("password", user.salt) must equalTo(user.password)
      user.role_id must equalTo(255)
    }
  }
}
