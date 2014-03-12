package controllers

import play.api.mvc._
import models.User

object InstallationController extends Controller {
  def install = Action {
    if(!User.all().isEmpty) {
      Redirect(routes.Application.index())
    } else {
      Ok(views.html.install(UserController.userForm))
    }
  }

  def firstUser = Action { implicit request =>
    UserController.userForm.bindFromRequest().fold(
      errors => BadRequest(views.html.install(errors)),
      value => {
        UserController.createUser(value)
        Redirect(routes.Application.index()).withSession(Security.username -> value.username)
      }
    )
  }
}
