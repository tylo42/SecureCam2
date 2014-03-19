package controllers

import play.api.mvc._
import models.{ConcreteRoleService, ConcreteUserService, UserService}

case class UserRegistration(username: String, password: String, confirmPassword: String, role: Option[String])

class UserController(_userService: UserService, userFactory: UserFactory) extends Controller with Secured {
  private val userForm = UserFormFactory(_userService)

  def users = isAdmin {
    implicit username => implicit request =>
      Ok(views.html.users(userService.all(), userForm))
  }

  def newUser = isAdmin {
    implicit username => implicit request =>
      userForm.bindFromRequest().fold(
        errors => BadRequest(views.html.users(userService.all(), errors)),
        value => {
          createUser(value)
          Redirect(routes.UserController.users())
        }
      )
  }

  def deleteUser(username: String) = isAdmin {
    signedInUser => implicit request =>
      userService.delete(username)
      Redirect(routes.UserController.users())
  }

  private def createUser(value: UserRegistration) = {
    userService.create(userFactory(value.username, value.password, value.role.get))
  }

  override val userService: UserService = _userService
}

object UserController extends UserController(new ConcreteUserService(new ConcreteRoleService), new ConcreteUserFactory(new ConcreteRoleService)) {}
