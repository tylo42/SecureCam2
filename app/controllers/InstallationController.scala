package controllers

import play.api.mvc._
import models.{ConcreteRoleService, UserService, ConcreteUserService}

class InstallationController(userService: UserService, userFactory: UserFactory) extends Controller {
  def install = Action {
    if(!userService.isEmpty) {
      Redirect(routes.Application.index())
    } else {
      Ok(views.html.install(UserController.userForm.fill(UserRegistration("admin", "", ""))))
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
            userService.create(userFactory(value.username, value.password, "super"))
            Redirect(routes.Application.index()).withSession(Security.username -> value.username)
          }
        )
      }
    }
  }
}

object InstallationController extends InstallationController(new ConcreteUserService(new ConcreteRoleService), new ConcreteUserFactory(new ConcreteRoleService())) {}
