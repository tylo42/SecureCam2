package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.User
import io.github.nremond.PBKDF2

case class UserRegistration(username: String, password: String, confirmPassword: String)

object UserController extends Controller {
  val userForm = Form(
    mapping(
      "Username" -> text(minLength = 3, maxLength = 255),
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
          val salt = RandomStringGenerator(64)
          val password = PBKDF2(value.password, salt)
          User.create(User(value.username, password, salt))
          Redirect(routes.UserController.users)
        }
      )
  }


  def deleteUser(username: String) = Action {
    User.delete(username)
    Redirect(routes.UserController.users)
  }
}
