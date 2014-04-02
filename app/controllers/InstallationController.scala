package controllers

import play.api.mvc._
import models._

class InstallationController(userRoleService: UserRoleService, userFactory: UserFactory) extends Controller {
  private val userForm = UserFormFactory(userRoleService.users)

  def install = Action {
    if (!userRoleService.users.isEmpty) {
      Redirect(routes.Application.index())
    } else {
      Ok(views.html.install(userForm.fill(UserRegistration("admin", "", "", None))))
    }
  }

  def firstUser = Action {
    implicit request =>
      this.synchronized {
        if (!userRoleService.users.isEmpty) {
          Redirect(routes.Application.index())
        } else {
          userForm.bindFromRequest().fold(
            errors => BadRequest(views.html.install(errors)),
            value => {
              userRoleService.users.create(userFactory("admin", value.password, "super"))
              Redirect(routes.Application.index()).withSession(Security.username -> value.username)
            }
          )
        }
      }
  }
}

object InstallationController extends InstallationController(new ConcreteUserRoleService(new ConcreteUserService, new ConcreteRoleService()), new ConcreteUserFactory(new ConcreteRoleService())) {}
