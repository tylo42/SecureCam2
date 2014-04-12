package controllers

import play.api.test._
import play.api.test.Helpers._

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import models.{User, UserService}
import play.api.libs.iteratee.Enumerator
import play.api.mvc.Security
import play.mvc.Http.HeaderNames

class UserControllerTest extends Specification with Mockito {
  val userService = mock[UserService]

  val testObject = new UserController(userService)

  val fakeExistingUsers = List(
    User(1, "admin", "hashed password", "salt", "role1"),
    User(2, "user1", "hashed password", "salt", "role2")
  )

  "Users" should {
    "redirect to / for non-admin users" in {
      "create admin user" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
        userService.exists("username") returns true
        userService.isAdmin("username") returns false

        userService.all() returns fakeExistingUsers
        userService.exists("newUser") returns false

        val requestBody = Enumerator("".getBytes) andThen Enumerator.eof
        val result = requestBody |>>> testObject.users()(FakeRequest().withSession(Security.username -> "username"))

        status(result) must equalTo(SEE_OTHER)
        redirectLocation(result) must beSome("/")

        there was no(userService).all()
      }
    }

    "show users page" in {
      "create admin user" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
        userService.exists("username") returns true
        userService.isAdmin("username") returns true

        userService.all() returns fakeExistingUsers
        userService.exists("newUser") returns false

        val requestBody = Enumerator("".getBytes) andThen Enumerator.eof
        val result = requestBody |>>> testObject.users()(FakeRequest().withSession(Security.username -> "username"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("text/html")

        there was one(userService).all()
      }
    }
  }

  "New user" should {
    "redirect to / for non-admin users" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      userService.exists("username") returns true
      userService.isAdmin("username") returns false

      userService.all() returns fakeExistingUsers
      userService.exists("newUser") returns false

      val requestBody = Enumerator("Username=newUser&Password=password&Confirm password=password&Role=admin".getBytes) andThen Enumerator.eof
      val result = requestBody |>>> testObject.newUser()(FakeRequest(POST, "post")
        .withSession(Security.username -> "username")
        .withHeaders((HeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded"))
      )

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/")

      there was no(userService).create(anyString, anyString, anyString)
    }

    "create admin user" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      userService.exists("username") returns true
      userService.isAdmin("username") returns true

      userService.all() returns fakeExistingUsers
      userService.exists("newUser") returns false

      val requestBody = Enumerator("Username=newUser&Password=password&Confirm password=password&Role=admin".getBytes) andThen Enumerator.eof
      val result = requestBody |>>> testObject.newUser()(FakeRequest(POST, "post")
        .withSession(Security.username -> "username")
        .withHeaders((HeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded"))
      )

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/users")

      there was one(userService).create("newUser", "password", "admin")
    }
  }

  "Delete user" should {
    "redirect to / for non-admins" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      userService.exists("username") returns true
      userService.isAdmin("username") returns false

      val requestBody = Enumerator("".getBytes) andThen Enumerator.eof
      val result = requestBody |>>> testObject.deleteUser("username")(FakeRequest().withSession(Security.username -> "username"))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/")

      there was no(userService).delete("username")
      there was no(userService).delete("username")
    }


    "delete user" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      userService.exists("username") returns true
      userService.isAdmin("username") returns true

      val requestBody = Enumerator("".getBytes) andThen Enumerator.eof
      val result = requestBody |>>> testObject.deleteUser("username")(FakeRequest().withSession(Security.username -> "username"))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/users")

      there was one(userService).delete("username")
    }
  }
}
