package models

import org.joda.time.{Interval, DateTime}

case class Video(id: Long, time: DateTime, video: String, picture: String, flagged: Boolean, camera_id: Long);

object Video {
  def all(): List[Video] = ???

  def range(interval: Interval, flagged: Option[Boolean] = None): List[Video] = ???

  def before(time: DateTime, flagged: Option[Boolean] = None): List[Video] = ???

  def after(time: DateTime, flagged: Option[Boolean] = None): List[Video] = ???
}
