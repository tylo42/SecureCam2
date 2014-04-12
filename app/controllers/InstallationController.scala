package controllers

import play.api.mvc._
import models._

class InstallationController(userService: UserService) extends Controller {
  private val userForm = UserFormFactory(userService)

  def install = Action {
    if (!userService.isEmpty) {
      Redirect(routes.Application.index())
    } else {
      Ok(views.html.install(userForm.fill(UserRegistration("admin", "", "", "super"))))
    }
  }

  def firstUser = Action {
    implicit request =>
      this.synchronized {
        if (!userService.isEmpty) {
          Redirect(routes.Application.index())
        } else {
          userForm.bindFromRequest().fold(
            errors => BadRequest(views.html.install(errors)),
            value => {
              userService.create("admin", value.password, "super")
              Redirect(routes.Application.index()).withSession(Security.username -> value.username)
            }
          )
        }
      }
  }
}

object InstallationController extends InstallationController(new ConcreteUserService(new ConcreteRoleService())) {}
