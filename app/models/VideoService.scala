package models

import org.joda.time.{Interval, DateTime}
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Video(id: Long, time: DateTime, video: String, picture: Option[String], flagged: Boolean, event: Long, cameraId: Long, cameraDescription: String)

trait VideoService {
  def getBetweenInterval(interval: Interval, flagged: Option[Boolean] = None): List[Video]

  def getMostRecentVideo(cameraId: Long): Option[Video]

  def insertVideo(time: Long, video: String, event: Long, cameraId: Long): Unit

  def insertPicture(picture: String, event: Long, cameraId: Long): Unit

  def all(): List[Video]
}

class ConcreteVideoService extends VideoService {
  private val videoParser: RowParser[Video] = {
    long("id") ~
      long("time") ~
      str("video") ~
      get[Option[String]]("picture") ~
      bool("flagged") ~
      long("event") ~
      long("cameraId") ~
      str("camera.description") map {
      case id ~ time ~ video ~ picture ~ flagged ~ event ~ cameraId ~ cameraDescription => Video(id, new DateTime(time * 1000), video, picture, flagged, event, cameraId, cameraDescription)
    }
  }

  private val videosParser: ResultSetParser[List[Video]] = videoParser.*

  def all(): List[Video] = DB.withConnection {
    implicit c => {
      SQL("select video.*,camera.description from video inner join camera on video.cameraId=camera.id order by time").as(videosParser)
    }
  }

  def getBetweenInterval(interval: Interval, flagged: Option[Boolean] = None): List[Video] = DB.withConnection {
    implicit c => {
      flagged match {
        case Some(f) => SQL("select video.*,camera.description from video inner join camera on video.cameraId=camera.id where {start} <= time and time <= {end} and flagged = {flagged} order by time").on(
          'start -> interval.getStart,
          'end -> interval.getEnd,
          'flagged -> f
        ).as(videosParser)
        case _ => SQL("elect video.*,camera.description from video inner join camera on video.cameraId=camera.id where {start} <= time and time <= {end} order by time").on(
          'start -> interval.getStart,
          'end -> interval.getEnd
        ).as(videosParser)
      }
    }
  }

  def getMostRecentVideo(cameraId: Long): Option[Video] = DB.withConnection {
    implicit c => {
      SQL("select video.*,camera.description from video inner join camera on video.cameraId=camera.id where cameraId = {cameraId} and picture is not null").on(
        'cameraId -> cameraId
      ).as(videosParser) match {
        case Nil => None
        case l   => Some(l.head)
      }
    }
  }

  def insertVideo(time: Long, video: String, event: Long, cameraId: Long): Unit = DB.withConnection {
    implicit c => {
      SQL("insert into video (time, video, event, cameraId) values ({time}, {video}, {event}, {cameraId})").on(
        'time -> time,
        'video -> video,
        'event -> event,
        'cameraId -> cameraId
      ).executeUpdate()
    }
  }

  def insertPicture(picture: String, event: Long, cameraId: Long): Unit = DB.withConnection {
    implicit c => {
      SQL("update video set picture = {picture} where id = (select max(id) from video where cameraId = {cameraId} and event = {event})").on(
        'picture -> picture,
        'cameraId -> cameraId,
        'event -> event
      ).executeUpdate()
    }
  }
}
