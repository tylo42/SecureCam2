package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.User

case class UserRegistration(username: String, password: String, confirmPassword: String)

object UserController extends Controller {
  val userForm = Form(
    mapping(
      "Username" -> nonEmptyText,
      "Password" -> text(minLength = 6),
      "Confirm password" -> text(minLength = 6)
    )(UserRegistration.apply)(UserRegistration.unapply).verifying(
        "Passwords must match", user => user.password == user.confirmPassword)
  )

  def users = Action {
    Ok(views.html.users(User.all(), userForm))
  }

  def newUser = Action {
    implicit request =>
      userForm.bindFromRequest().fold(
        errors => BadRequest(views.html.users(User.all(), errors)),
        value => {
          User.create(User(value.username, value.password))
          Redirect(routes.UserController.users)
        }
      )
  }

  def deleteUser(username: String) = Action {
    User.delete(username)
    Redirect(routes.UserController.users)
  }
}
