package controllers

import play.api.mvc._
import play.api.templates.Html
import models._
import scala.Some

class Application(_userService: UserService) extends Controller with Secured {

  def index = isAuthenticated {
    username => implicit request =>
      Ok(views.html.main("Home", Some(username))(Html("<p>test</p>")))
  }

  override val userService = _userService
}

object Application extends Application(new ConcreteUserService(new ConcreteRoleService()))
