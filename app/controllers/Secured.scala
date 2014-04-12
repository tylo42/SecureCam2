package controllers

import play.api.mvc._
import scala.Some
import models.UserService

trait Secured {
  val userService: UserService

  def isAuthenticated(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) {
      user =>
        Action(request => f(user)(request))
    }
  }

  def isAdmin(f: => String => Request[AnyContent] => Result) = hasPrivileges(f) {
    implicit user => userService.isAdmin || userService.isSuper
  }

  private def hasPrivileges(f: => String => Request[AnyContent] => Result)(g: String => Boolean) = isAuthenticated {
    user => request =>
      if (g(user)) {
        f(user)(request)
      } else {
        Results.Redirect(routes.Application.index())
      }
  }

  private def username(request: RequestHeader): Option[String] = {
    request.session.get(Security.username).flatMap {
      username =>
        if (userService.exists(username)) {
          Some(username)
        } else {
          None
        }
    }
  }

  private def onUnauthorized(request: RequestHeader) = {
    Results.Redirect(routes.Authentication.login())
  }
}