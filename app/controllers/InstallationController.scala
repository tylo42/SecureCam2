package controllers

import play.api.mvc._
import models._

class InstallationController(userRoleService: UserRoleService) extends Controller {
  private val userForm = UserFormFactory(userRoleService)

  def install = Action {
    if (!userRoleService.usersIsEmpty) {
      Redirect(routes.Application.index())
    } else {
      Ok(views.html.install(userForm.fill(UserRegistration("admin", "", "", "super"))))
    }
  }

  def firstUser = Action {
    implicit request =>
      this.synchronized {
        if (!userRoleService.usersIsEmpty) {
          Redirect(routes.Application.index())
        } else {
          userForm.bindFromRequest().fold(
            errors => BadRequest(views.html.install(errors)),
            value => {
              userRoleService.createUser("admin", value.password, "super")
              Redirect(routes.Application.index()).withSession(Security.username -> value.username)
            }
          )
        }
      }
  }
}

object InstallationController extends InstallationController(new ConcreteUserRoleService(new ConcreteUserService, new ConcreteRoleService())) {}
