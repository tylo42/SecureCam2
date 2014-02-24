package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.api.Play.current

case class User(id: Long, userName: String, password: String)

object User {
  def all(): List[User] = DB.withConnection {
    implicit c => SQL("select * from user").as(user *)
  }

  def create(userName: String, password: String) {
    DB.withConnection {
      implicit c =>
        SQL("insert into user (username, password) values ({username}, {password})").on(
          'username -> userName,
          'password -> password
        ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from user where user_id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }

  val user = {
    get[Long]("user_id") ~
    get[String]("username") ~
    get[String]("password") map {
      case id~username~password => User(id, username, password)
    }
  }

}
