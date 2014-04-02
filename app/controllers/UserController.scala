package controllers

import play.api.mvc._
import models._

case class UserRegistration(username: String, password: String, confirmPassword: String, role: Option[String])

class UserController(_userRoleService: UserRoleService, userFactory: UserFactory) extends Controller with Secured {
  private val userForm = UserFormFactory(_userRoleService.users)

  def users = isAdmin {
    implicit username => implicit request =>
      Ok(views.html.users(userRoleService.users.all(), userForm))
  }

  def newUser = isAdmin {
    implicit username => implicit request =>
      userForm.bindFromRequest().fold(
        errors => BadRequest(views.html.users(userRoleService.users.all(), errors)),
        value => {
          createUser(value)
          Redirect(routes.UserController.users())
        }
      )
  }

  def deleteUser(username: String) = isAdmin {
    signedInUser => implicit request =>
      userRoleService.delete(username)
      Redirect(routes.UserController.users())
  }

  private def createUser(value: UserRegistration) = {
    userRoleService.users.create(userFactory(value.username, value.password, value.role.get))
  }

  override val userRoleService = _userRoleService
}

object UserController extends UserController(new ConcreteUserRoleService(new ConcreteUserService, new ConcreteRoleService()), new ConcreteUserFactory(new ConcreteRoleService)) {}
