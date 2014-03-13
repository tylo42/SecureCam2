package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import models.{ConcreteUserService, UserService, User}
import io.github.nremond.PBKDF2

class Authentication(userService: UserService) extends Controller {
  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying ("Invalid username or password", result => result match {
      case (username, password) => check(username, password)
    })
  )

  def check(username: String, password: String): Boolean = {
    userService.get(username) match {
      case None => false
      case Some(user) => user.password == PBKDF2(password, user.salt)
    }

  }

  def login = Action { implicit request =>
    if(userService.isEmpty) {
      Redirect(routes.InstallationController.install())
    } else {
      Ok(views.html.login(loginForm))
    }
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.Application.index()).withSession(Security.username -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.Authentication.login()).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }

  trait Secured {

    def username(request: RequestHeader): Option[String] = {
      request.session.get(Security.username).flatMap{ username =>
        if(userService.exists(username)) { Some(username) } else { None }
      }
    }

    def onUnauthorized(request: RequestHeader) = {
      Results.Redirect(routes.Authentication.login())
    }

    def withAuth(f: => String => Request[AnyContent] => Result) = {
      Security.Authenticated(username, onUnauthorized) { user =>
        Action(request => f(user)(request))
      }
    }
  }
}

object Authentication extends Authentication(new ConcreteUserService()) {}
