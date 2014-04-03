package models

import org.joda.time.{Interval, DateTime}
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Video(id: Long, time: DateTime, video: String, picture: Option[String], flagged: Boolean, camera_id: Long)

trait VideoService {
  def getBetweenInterval(interval: Interval, flagged: Option[Boolean] = None): List[Video]

  def insertVideo(time: Long, video: String, camera_id: Long): Unit

  def all(): List[Video]
}

class ConcreteVideoService extends VideoService {
  private val videoParser: RowParser[Video] = {
    long("id") ~
      long("time") ~
      str("video") ~
      get[Option[String]]("picture") ~
      bool("flagged") ~
      long("camera_id") map {
      case id ~ time ~ video ~ picture ~ flagged ~ camera_id => Video(id, new DateTime(time * 1000), video, picture, flagged, camera_id)
    }
  }

  private val videosParser: ResultSetParser[List[Video]] = videoParser.*

  def all(): List[Video] = DB.withConnection {
    implicit c => {
      SQL("select * from video order by time").as(videosParser)
    }
  }

  def getBetweenInterval(interval: Interval, flagged: Option[Boolean] = None): List[Video] = DB.withConnection {
    implicit c => {
      if (flagged.isDefined) {
        SQL("select * from video where {start} <= time and time <= {end} and flagged = {flagged} order by time").on(
          'start -> interval.getStart,
          'end -> interval.getEnd,
          'flagged -> flagged.get
        ).as(videosParser)
      } else {
        SQL("select * from video where {start} <= time and time <= {end} order by time").on(
          'start -> interval.getStart,
          'end -> interval.getEnd
        ).as(videosParser)
      }
    }
  }

  def insertVideo(time: Long, video: String, camera_id: Long): Unit = DB.withConnection {
    implicit c => {
      SQL("insert into video (time, video, camera_id) values ({time}, {video}, {camera_id})").on(
        'time -> time,
        'video -> video,
        'camera_id -> camera_id
      ).executeUpdate()
    }
  }
}
