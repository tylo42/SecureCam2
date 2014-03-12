package controllers

import play.api.mvc._
import play.api.templates.Html
import controllers.Authentication.Secured

object Application extends Controller with Secured {

  def index = withAuth { username => implicit request =>
    Ok(views.html.main("title", Some(username))(Html("<p>test</p>")))
  }
}
