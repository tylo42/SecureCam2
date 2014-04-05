package controllers

import play.api.data.Form
import play.api.data.Forms._
import models.UserService

case class UserRegistration(username: String, password: String, confirmPassword: String, role: Option[String])

object UserFormFactory {
  def apply(userService: UserService) = Form(
    mapping(
      "Username" -> text(minLength = 3, maxLength = 32),
      "Password" -> text(minLength = 6),
      "Confirm password" -> text(minLength = 6),
      "Role" -> optional(text)
    )(UserRegistration.apply)(UserRegistration.unapply)
      .verifying("Passwords must match", user => user.password == user.confirmPassword)
      .verifying("Username is already in use", user => !userService.exists(user.username))
      .verifying("Username may not contain whitespace", user => !user.username.toList.exists(_.isWhitespace))
      .verifying("Password may not contain whitespace", user => !user.password.toList.exists(_.isWhitespace))
  )
}