package controllers

import play.api.mvc._
import play.api.templates.Html
import models._
import scala.Some

class Application(_userRoleService: UserRoleService) extends Controller with Secured {

  def index = isAuthenticated {
    username => implicit request =>
      Ok(views.html.main("Home", Some(username))(Html("<p>test</p>")))
  }

  override val userRoleService = _userRoleService
}

object Application extends Application(new ConcreteUserRoleService(new ConcreteUserService, new ConcreteRoleService()))
