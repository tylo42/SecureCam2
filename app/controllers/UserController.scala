package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.{ConcreteRoleService, ConcreteUserService, UserService}
import controllers.Authentication.Secured
import views.html.helper.options

case class UserRegistration(username: String, password: String, confirmPassword: String, role: Option[String])

class UserController(userService: UserService, userFactory: UserFactory) extends Controller with Secured {
  val userForm = Form(
    mapping(
      "Username" -> text(minLength = 3, maxLength = 32),
      "Password" -> text(minLength = 6),
      "Confirm password" -> text(minLength = 6),
      "Role" -> optional(text)
    )(UserRegistration.apply)(UserRegistration.unapply)
      .verifying("Passwords must match", user => user.password == user.confirmPassword)

  )

  def users = isAdmin { username => implicit request =>
    Ok(views.html.users(Some(username), userService.all(), userForm))
  }

  def newUser = isAdmin { username => implicit request =>
      userForm.bindFromRequest().fold(
        errors => BadRequest(views.html.users(Some(username), userService.all(), errors)),
        value => {
          createUser(value)
          Redirect(routes.UserController.users)
        }
      )
  }

  def createUser(value: UserRegistration) = {
    userService.create(userFactory(value.username, value.password, value.role.get))
  }

  def deleteUser(username: String) = Action {
    userService.delete(username)
    Redirect(routes.UserController.users)
  }
}

object UserController extends UserController(new ConcreteUserService(new ConcreteRoleService), new ConcreteUserFactory(new ConcreteRoleService)) {}
