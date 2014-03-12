package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.User
import io.github.nremond.PBKDF2
import controllers.Authentication.Secured

case class UserRegistration(username: String, password: String, confirmPassword: String)

object UserController extends Controller with Secured {
  val userForm = Form(
    mapping(
      "Username" -> text(minLength = 3, maxLength = 255),
      "Password" -> text(minLength = 6),
      "Confirm password" -> text(minLength = 6)
    )(UserRegistration.apply)(UserRegistration.unapply).verifying(
        "Passwords must match", user => user.password == user.confirmPassword)
  )

  def users = withAuth { username => implicit request =>
    Ok(views.html.users(Some(username), User.all(), userForm))
  }

  def newUser = withAuth { username => implicit request =>
      userForm.bindFromRequest().fold(
        errors => BadRequest(views.html.users(Some(username), User.all(), errors)),
        value => {
          createUser(value)
          Redirect(routes.UserController.users)
        }
      )
  }

  def createUser(value: UserRegistration) = {
    val salt = RandomStringGenerator(64)
    val password = PBKDF2(value.password, salt)
    User.create(User(value.username, password, salt))
  }

  def deleteUser(username: String) = Action {
    User.delete(username)
    Redirect(routes.UserController.users)
  }
}
