package controllers

import play.api.test._
import play.api.test.Helpers._

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import models.{UserRoleService, User}
import play.api.libs.iteratee.Enumerator
import play.api.mvc.Security
import play.mvc.Http.HeaderNames

class UserControllerTest extends Specification with Mockito {
  val userRoleService = mock[UserRoleService]

  val testObject = new UserController(userRoleService)

  val fakeExistingUsers = List(
    User(1, "admin", "hashed password", "salt", 255),
    User(2, "user1", "hashed password", "salt", 255)
  )

  "Users" should {
    "redirect to / for non-admin users" in {
      "create admin user" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
        userRoleService.userExists("username") returns true
        userRoleService.isAdmin("username") returns false

        userRoleService.allUsers() returns fakeExistingUsers
        userRoleService.userExists("newUser") returns false

        val requestBody = Enumerator("".getBytes) andThen Enumerator.eof
        val result = requestBody |>>> testObject.users()(FakeRequest().withSession(Security.username -> "username"))

        status(result) must equalTo(SEE_OTHER)
        redirectLocation(result) must beSome("/")

        there was no(userRoleService).allUsers()
      }
    }

    "show users page" in {
      "create admin user" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
        userRoleService.userExists("username") returns true
        userRoleService.isAdmin("username") returns true

        userRoleService.allUsers() returns fakeExistingUsers
        userRoleService.userExists("newUser") returns false

        val requestBody = Enumerator("".getBytes) andThen Enumerator.eof
        val result = requestBody |>>> testObject.users()(FakeRequest().withSession(Security.username -> "username"))

        status(result) must equalTo(OK)
        contentType(result) must beSome("text/html")

        there was one(userRoleService).allUsers()
      }
    }
  }

  "New user" should {
    "redirect to / for non-admin users" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      userRoleService.userExists("username") returns true
      userRoleService.isAdmin("username") returns false

      userRoleService.allUsers() returns fakeExistingUsers
      userRoleService.userExists("newUser") returns false

      val requestBody = Enumerator("Username=newUser&Password=password&Confirm password=password&Role=admin".getBytes) andThen Enumerator.eof
      val result = requestBody |>>> testObject.newUser()(FakeRequest(POST, "post")
        .withSession(Security.username -> "username")
        .withHeaders((HeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded"))
      )

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/")

      there was no(userRoleService).createUser(anyString, anyString, anyString)
    }

    "create admin user" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      userRoleService.userExists("username") returns true
      userRoleService.isAdmin("username") returns true

      userRoleService.allUsers() returns fakeExistingUsers
      userRoleService.userExists("newUser") returns false

      val requestBody = Enumerator("Username=newUser&Password=password&Confirm password=password&Role=admin".getBytes) andThen Enumerator.eof
      val result = requestBody |>>> testObject.newUser()(FakeRequest(POST, "post")
        .withSession(Security.username -> "username")
        .withHeaders((HeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded"))
      )

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/users")

      there was one(userRoleService).createUser("newUser", "password", "admin")
    }
  }

  "Delete user" should {
    "redirect to / for non-admins" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      userRoleService.userExists("username") returns true
      userRoleService.isAdmin("username") returns false

      val requestBody = Enumerator("".getBytes) andThen Enumerator.eof
      val result = requestBody |>>> testObject.deleteUser("username")(FakeRequest().withSession(Security.username -> "username"))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/")

      there was no(userRoleService).deleteUser("username")
      there was no(userRoleService).deleteUser("username")
    }


    "delete user" in new WithApplication(new FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      userRoleService.userExists("username") returns true
      userRoleService.isAdmin("username") returns true

      val requestBody = Enumerator("".getBytes) andThen Enumerator.eof
      val result = requestBody |>>> testObject.deleteUser("username")(FakeRequest().withSession(Security.username -> "username"))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/users")

      there was one(userRoleService).deleteUser("username")
    }
  }
}
