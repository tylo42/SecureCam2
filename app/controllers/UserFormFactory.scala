package controllers

import play.api.data.Form
import play.api.data.Forms._
import models.UserRoleService

case class UserRegistration(username: String, password: String, confirmPassword: String, role: String)

object UserFormFactory {
  def apply(userRoleService: UserRoleService) = Form(
    mapping(
      "Username" -> text(minLength = 3, maxLength = 32)
        .verifying("Username is already in use", !userRoleService.userExists(_))
        .verifying("May not contain whitespace", !containsWhitespace(_)),
      "Password" -> text(minLength = 6)
        .verifying("May not contain whitespace", !containsWhitespace(_)),
      "Confirm password" -> text(minLength = 6),
      "Role" -> text
    )(UserRegistration.apply)(UserRegistration.unapply)
      .verifying("Passwords must match", user => user.password == user.confirmPassword)
  )

  def containsWhitespace(s: String): Boolean = s.toList.exists(_.isWhitespace)
}