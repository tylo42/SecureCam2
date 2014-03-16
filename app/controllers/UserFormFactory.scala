package controllers

import play.api.data.Form
import play.api.data.Forms._
import models.UserService

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
  )
}
