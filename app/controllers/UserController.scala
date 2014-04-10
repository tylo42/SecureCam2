package controllers

import play.api.mvc._
import models._

class UserController(_userRoleService: UserRoleService) extends Controller with Secured {
  private val userForm = UserFormFactory(_userRoleService)

  def users = isAdmin {
    implicit username => implicit request =>
      Ok(views.html.users(userRoleService.allUsers(), userForm))
  }

  def newUser = isAdmin {
    implicit username => implicit request =>
      userForm.bindFromRequest().fold(
        errors => BadRequest(views.html.users(userRoleService.allUsers(), errors)),
        value => {
          createUser(value)
          Redirect(routes.UserController.users())
        }
      )
  }

  def deleteUser(username: String) = isAdmin {
    signedInUser => implicit request =>
      userRoleService.deleteUser(username)
      Redirect(routes.UserController.users())
  }

  private def createUser(value: UserRegistration) = {
    userRoleService.createUser(value.username, value.password, value.role)
  }

  override val userRoleService = _userRoleService
}

object UserController extends UserController(new ConcreteUserRoleService(new ConcreteUserService, new ConcreteRoleService())) {}
