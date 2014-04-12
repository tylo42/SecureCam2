package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.Logger
import models._
import io.github.nremond.PBKDF2
import scala.Some

class Authentication(userService: UserService) extends Controller {
  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying("Username or password is incorrect", result => result match {
      case (username, password) => check(username, password)
    })
  )

  def check(username: String, password: String): Boolean = {
    userService.get(username) match {
      case None => false
      case Some(user) => user.password == PBKDF2(password, user.salt)
    }

  }

  def login = Action {
    implicit request =>
      if (userService.isEmpty) {
        Redirect(routes.InstallationController.install())
      } else {
        Ok(views.html.login(loginForm))
      }
  }

  def authenticate = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.login(formWithErrors)),
        user => {
          Logger.info("User logged in: " + user._1)
          Redirect(routes.Application.index()).withSession(Security.username -> user._1)
        }
      )
  }

  def logout = Action {
    Redirect(routes.Authentication.login()).withNewSession.flashing(
      "success" -> "You have successfully logged out."
    )
  }
}

object Authentication extends Authentication(new ConcreteUserService(new ConcreteRoleService())) {}
