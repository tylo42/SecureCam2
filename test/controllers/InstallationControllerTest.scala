package controllers

import play.api.test._
import play.api.test.Helpers._

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import models.{User, UserRoleService}

class InstallationControllerTest extends Specification with Mockito {
  val userRoleService = mock[UserRoleService]

  val testObject = new InstallationController(userRoleService)


  "Installation controller install" should {
    "redirect home" in {
      userRoleService.usersIsEmpty returns false

      val result = testObject.install()(FakeRequest())

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/")
    }

    "load install page" in {
      userRoleService.usersIsEmpty returns true

      val result = testObject.install()(FakeRequest())

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }

  }

  "Installation controller firstUser" should {
    "redirect home without created user" in {
      userRoleService.usersIsEmpty returns false

      val result = testObject.firstUser()(FakeRequest())

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/")

      there was no(userRoleService).createUser(anyString, anyString, anyString)
    }

    "empty post" in {
      userRoleService.usersIsEmpty returns true

      val result = testObject.firstUser()(FakeRequest())

      status(result) must equalTo(BAD_REQUEST)

      there was no(userRoleService).createUser(anyString, anyString, anyString)
    }

    "short username" in new WithApplication {
      userRoleService.usersIsEmpty returns true

      val result = testObject.firstUser()(FakeRequest(POST, "post").withFormUrlEncodedBody(
        "Username" -> "a",
        "Password" -> "password",
        "Confirm password" -> "password"
      ))

      status(result) must equalTo(BAD_REQUEST)

      there was no(userRoleService).createUser(anyString, anyString, anyString)
    }

    "passwords to not match" in new WithApplication {
      userRoleService.usersIsEmpty returns true

      val result = testObject.firstUser()(FakeRequest(POST, "post").withFormUrlEncodedBody(
        "Username" -> "admin",
        "Password" -> "password1",
        "Confirm password" -> "password2"
      ))

      status(result) must equalTo(BAD_REQUEST)

      there was no(userRoleService).createUser(anyString, anyString, anyString)
    }

    "create first user" in new WithApplication {
      userRoleService.usersIsEmpty returns true

      val result = testObject.firstUser()(FakeRequest(POST, "post").withFormUrlEncodedBody(
        "Username" -> "not_admin",
        "Password" -> "password",
        "Confirm password" -> "password",
        "Role" -> "not_super"
      ))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/")

      there was one(userRoleService).createUser("admin", "password", "super")
    }
  }
}
