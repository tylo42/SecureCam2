package controllers

import play.api.test._
import play.api.test.Helpers._

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import models.{User, UserService}

class InstallationControllerTest extends Specification with Mockito {
  val userService = mock[UserService]

  val testObject = new InstallationController(userService)


  "Installation controller install" should {
    "redirect home" in {
      userService.isEmpty returns false

      val result = testObject.install()(FakeRequest())

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/")
    }

    "load install page" in {
      userService.isEmpty returns true

      val result = testObject.install()(FakeRequest())

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }

  }

  "Installation controller firstUser" should {
    "redirect home without created user" in {
      userService.isEmpty returns false

      val result = testObject.firstUser()(FakeRequest())

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/")

      there was no(userService).create(anyString, anyString, anyString)
    }

    "empty post" in {
      userService.isEmpty returns true

      val result = testObject.firstUser()(FakeRequest())

      status(result) must equalTo(BAD_REQUEST)

      there was no(userService).create(anyString, anyString, anyString)
    }

    "short username" in new WithApplication {
      userService.isEmpty returns true

      val result = testObject.firstUser()(FakeRequest(POST, "post").withFormUrlEncodedBody(
        "Username" -> "a",
        "Password" -> "password",
        "Confirm password" -> "password"
      ))

      status(result) must equalTo(BAD_REQUEST)

      there was no(userService).create(anyString, anyString, anyString)
    }

    "passwords to not match" in new WithApplication {
      userService.isEmpty returns true

      val result = testObject.firstUser()(FakeRequest(POST, "post").withFormUrlEncodedBody(
        "Username" -> "admin",
        "Password" -> "password1",
        "Confirm password" -> "password2"
      ))

      status(result) must equalTo(BAD_REQUEST)

      there was no(userService).create(anyString, anyString, anyString)
    }

    "create first user" in new WithApplication {
      userService.isEmpty returns true

      val result = testObject.firstUser()(FakeRequest(POST, "post").withFormUrlEncodedBody(
        "Username" -> "not_admin",
        "Password" -> "password",
        "Confirm password" -> "password",
        "Role" -> "not_super"
      ))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/")

      there was one(userService).create("admin", "password", "super")
    }
  }
}
