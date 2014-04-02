package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import models._
import io.github.nremond.PBKDF2
import scala.Some

class Authentication(userRoleService: UserRoleService) extends Controller {
  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying("Username or password is incorrect", result => result match {
      case (username, password) => check(username, password)
    })
  )

  def check(username: String, password: String): Boolean = {
    userRoleService.users.get(username) match {
      case None => false
      case Some(user) => user.password == PBKDF2(password, user.salt)
    }

  }

  def login = Action {
    implicit request =>
      if (userRoleService.users.isEmpty) {
        Redirect(routes.InstallationController.install())
      } else {
        Ok(views.html.login(loginForm))
      }
  }

  def authenticate = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.login(formWithErrors)),
        user => Redirect(routes.Application.index()).withSession(Security.username -> user._1)
      )
  }

  def logout = Action {
    Redirect(routes.Authentication.login()).withNewSession.flashing(
      "success" -> "You have successfully logged out."
    )
  }
}

object Authentication extends Authentication(new ConcreteUserRoleService(new ConcreteUserService, new ConcreteRoleService())) {}
