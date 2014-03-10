package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.api.Play.current

case class User(userName: String, password: String, salt: String)

object User {
  def all(): List[User] = DB.withConnection {
    implicit c => SQL("select * from user").as(user *)
  }

  def create(user: User) {
    DB.withConnection {
      implicit c =>
        SQL("insert into user (username, password, salt) values ({username}, {password}, {salt})").on(
          'username -> user.userName,
          'password -> user.password,
          'salt -> user.salt
        ).executeUpdate()
    }
  }

  def delete(username: String) {
    DB.withConnection { implicit c =>
      SQL("delete from user where username = {username}").on(
        'username -> username
      ).executeUpdate()
    }
  }

  val user = {
    get[String]("username") ~
    get[String]("password") ~
    get[String]("salt") map {
      case username~password~salt => User(username, password, salt)
    }
  }

}
