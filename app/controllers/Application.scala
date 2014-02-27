package controllers

import play.api.mvc._
import play.api.templates.Html
import controllers.Auth.Secured

object Application extends Controller with Secured {

  def index = withAuth { username => implicit request =>
    Ok(views.html.main(username)(Html("<p>test</p>")))
  }

  //def user() = withUser { user => implicit request =>
  //  val username = user.username
  //  Ok(views.html.user(user))
  //}

}
