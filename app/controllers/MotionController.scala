package controllers

import play.api.mvc.Controller
import play.Logger
import java.io._

class MotionController extends Controller {
  val homeDirectory = new File(System.getenv("HOME"))
  val motionDirectory = new File(homeDirectory, play.Play.application().configuration().getString("motion.config"))
  val motionConf = new File(motionDirectory, "motion.conf")
  val motionPid = new File(motionDirectory, "motion.pid")

  def writeConfig(): Unit = {
    motionDirectory.mkdirs()

    Logger.info("Writing " + motionConf)
    val writer = new PrintWriter(motionConf)
    writer.write(
      "daemon off\n" +
      "process_id_file " + motionPid + "\n" +
      """
input 8

width 640
height 480

framerate 25
minimum_frame_time 0

threshold 1500

despeckle EedDl

minimum_motion_frames 5
pre_capture 5
post_capture 5

gap 60
max_mpeg_time 60

output_normal best

ffmpeg_cap_new on
ffmpeg_cap_motion off
ffmpeg_bps 400000
ffmpeg_variable_bitrate 0
ffmpeg_video_codec swf
ffmpeg_deinterlace off

text_right %Y-%m-%d\n%T-%q
text_changes off
text_event %Y%m%d%H%M%S

jpeg_filename %Y-%m-%d_%H:%M:%S
movie_filename %Y-%m-%d_%H:%M:%S

webcam_quality 50
webcam_motion off
webcam_maxrate 1
webcam_localhost off
webcam_limit 0

control_port 50505
control_localhost off
control_html_output on
control_authentication username:password
""" +
      "thread " + new File(motionDirectory, "thread1.conf"))
    writer.close()
  }

}

object MotionController extends MotionController
