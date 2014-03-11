package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.api.Play.current

case class User(userName: String, password: String, salt: String)

object User {
  private val userParser: RowParser[User] = {
    str("username") ~
    str("password") ~
    str("salt") map {
      case username~password~salt => User(username, password, salt)
    }
  }

  private val usersParser: ResultSetParser[List[User]] = userParser *


  def all(): List[User] = DB.withConnection {
    implicit c => SQL("select * from user order by username asc").as(usersParser)
  }

  def get(username: String): Option[User] = DB.withConnection {
    implicit c =>
      SQL("select * from user where username = {username}").on(
        'username -> username).as(usersParser) match {
        case Nil => None
        case l => Some(l.head)
      }
  }

  def create(user: User): Unit = DB.withConnection {
    implicit c =>
      SQL("insert into user (username, password, salt) values ({username}, {password}, {salt})").on(
        'username -> user.userName,
        'password -> user.password,
        'salt -> user.salt
      ).executeUpdate()
  }

  def delete(username: String): Unit = DB.withConnection { implicit c =>
    SQL("delete from user where username = {username}").on(
      'username -> username
    ).executeUpdate()
  }
}
