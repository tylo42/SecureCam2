package controllers

import play.api.mvc._
import models._

class UserController(_userService: UserService) extends Controller with Secured {
  private val userForm = UserFormFactory(_userService)

  def users = isAdmin {
    implicit username => implicit request => {
      Ok(views.html.users(
        userService.all().map(user => (user, userService.canDelete(user.username)))
      ))
    }
  }

  def newUser() = isAdmin {
    implicit username => implicit request =>
      userForm.bindFromRequest().fold(
        errors => BadRequest(views.html.addUser(errors)),
        value => {
          createUser(value)
          Redirect(routes.UserController.users())
        }
      )
  }

  def addUser() = isAdmin {
    implicit username => implicit request => {
      Ok(views.html.addUser(userForm))
    }
  }

  def deleteUser(username: String) = isAdmin {
    signedInUser => implicit request => {
      userService.delete(username)
      Redirect(routes.UserController.users())
    }
  }

  private def createUser(value: UserRegistration) = {
    userService.create(value.username, value.password, value.role)
  }

  override val userService = _userService
}

object UserController extends UserController(new ConcreteUserService(new ConcreteRoleService())) {}
