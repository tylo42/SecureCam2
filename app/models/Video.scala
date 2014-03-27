package models

import org.joda.time.{Interval, DateTime}
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Video(id: Long, time: DateTime, video: String, picture: String, flagged: Boolean, camera_id: Long);

trait VideoService {
  def get(interval: Interval, flagged: Option[Boolean] = None): List[Video]
}

class ConcreteVideoService extends VideoService {
  private val videoParser: RowParser[Video] = {
    long("id") ~
      long("time") ~
      str("video") ~
      str("picture") ~
      bool("flagged") ~
      long("camera_id") map {
      case id ~ time ~ video ~ picture ~ flagged ~ camera_id => Video(id, new DateTime(time), video, picture, flagged, camera_id)
    }
  }

  private val videosParser: ResultSetParser[List[Video]] = videoParser.*

  def get(interval: Interval, flagged: Option[Boolean] = None): List[Video] = DB.withConnection {
    implicit c => {
      if (flagged.isDefined) {
        SQL("select * from user where {start} <= time and time <= {end} and flagged = {flagged} order by time").on(
          'start -> interval.getStart,
          'end -> interval.getEnd,
          'flagged -> flagged.get
        ).as(videosParser)
      } else {
        SQL("select * from user where {start} <= time and time <= {end} order by time").on(
          'start -> interval.getStart,
          'end -> interval.getEnd
        ).as(videosParser)
      }
    }
  }
}
