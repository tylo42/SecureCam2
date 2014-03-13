package controllers

import play.api.mvc._
import models.{UserService, ConcreteUserService}

class InstallationController(userService: UserService) extends Controller {
  def install = Action {
    if(!userService.isEmpty) {
      Redirect(routes.Application.index())
    } else {
      Ok(views.html.install(UserController.userForm))
    }
  }

  def firstUser = Action { implicit request =>
    this.synchronized {
      if(!userService.isEmpty) {
        Redirect(routes.Application.index())
      } else {
        UserController.userForm.bindFromRequest().fold(
          errors => BadRequest(views.html.install(errors)),
          value => {
            new UserController(userService).createUser(value) // TODO: Move this methods implementation to a new class
            Redirect(routes.Application.index()).withSession(Security.username -> value.username)
          }
        )
      }
    }
  }
}

object InstallationController extends InstallationController(new ConcreteUserService()) {}
