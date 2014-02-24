package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.User

object UserController extends Controller {
  val userForm = Form(
    tuple(
      "Username" -> nonEmptyText,
      "Password" -> text(minLength = 6),
      "Confirm password" -> text(minLength = 6)
    )
  )

  def users = Action {
    Ok(views.html.users(User.all(), userForm))
  }

  def newUser = Action {
    implicit request =>
      userForm.bindFromRequest().fold(
        errors => BadRequest(views.html.users(User.all(), errors)),
        value => {
          User.create(value._1, value._2)
          Redirect(routes.UserController.users)
        }
      )
  }

  def deleteUser(id: Long) = Action {
    User.delete(id)
    Redirect(routes.UserController.users)
  }
}
