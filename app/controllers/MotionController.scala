package controllers

import play.api.mvc.Controller
import play.Logger
import java.io._
import models._
import scala.sys.process._
import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits._
import models.Camera

class MotionController(nodeCamerasService: NodeCamerasService) extends Controller {
  val homeDirectory = new File(System.getenv("HOME"))
  val motionDirectory = new File(homeDirectory, play.Play.application().configuration().getString("motion.config"))
  val videoDirectory = new File(homeDirectory, play.Play.application().configuration().getString("motion.videos"))
  val motionConf = new File(motionDirectory, "motion.conf")
  val motionPid = new File(motionDirectory, "motion.pid")

  def startMotion(): Unit = {
    writeConfig()
    if(motionConf.exists()) {
      Logger.info("Starting motion")
      ("motion -c " + motionConf).!
    }
  }

  def stopMotion(): Unit = {
    if(motionPid.exists()) {
      Logger.info("Stopping motion")
      val pids = Source.fromFile(motionPid).getLines().toList.map(Integer.parseInt)
      pids.foreach(pid => {
        ("kill " + pid).!
      })
    }
  }

  def restartMotion(): Unit = {
    global.execute(new Runnable() {
      override def run(): Unit = {
        stopMotion()
        Thread.sleep(5000)
        startMotion()
      }
    })
  }

  def writeConfig(): Unit = {
    motionDirectory.mkdirs()
    deleteConfigFiles()

    val cameraFile = nodeCamerasService.nodeCameras(1).fold(List[(Camera, File)]())(nodeCameras => {
      nodeCameras.cameras.map(camera => (camera, new File(motionDirectory, "camera" + camera.id + ".conf")))
    })

    if(!cameraFile.isEmpty) {
      cameraFile.foreach(c => writeThreadConf(c._1, c._2))

      Logger.info("Writing " + motionConf)
      val writer = new PrintWriter(motionConf)
      writer.write(
        "daemon on\n" +
        "process_id_file " + motionPid + "\n" +
        "input 8\n" +
        "width 640\n" +
        "height 480\n" +
        "framerate 25\n" +
        "minimum_frame_time 0\n" +
        "threshold 1500\n" +
        "despeckle EedDl\n" +
        "minimum_motion_frames 5\n" +
        "pre_capture 5\n" +
        "post_capture 5\n" +
        "gap 60\n" +
        "max_mpeg_time 60\n" +
        "output_normal best\n" +
        "ffmpeg_cap_new on\n" +
        "ffmpeg_cap_motion off\n" +
        "ffmpeg_bps 400000\n" +
        "ffmpeg_variable_bitrate 0\n" +
        "ffmpeg_video_codec swf\n" +
        "ffmpeg_deinterlace off\n" +
        "text_right %Y-%m-%d\\n%T-%q\n" +
        "text_changes off\n" +
        "text_event %Y%m%d%H%M%S\n" +
        "jpeg_filename %Y-%m-%d_%H:%M:%S\n" +
        "movie_filename %Y-%m-%d_%H:%M:%S\n" +
        "webcam_quality 50\n" +
        "webcam_motion off\n" +
        "webcam_maxrate 1\n" +
        "webcam_localhost off\n" +
        "webcam_limit 0\n" +
        cameraFile.map(c => "thread " + c._2 + "\n").mkString)
      writer.close()
    }
  }

  def writeThreadConf(camera: Camera, threadFile: File): Unit = {
    Logger.info("Writing " + threadFile)
    val writer = new PrintWriter(threadFile)
    writer.write(
      "videodevice " + camera.device.getAbsolutePath + "\n" +
      "target_dir " + new File(videoDirectory, "camera" + camera.id) + "\n" +
      "webcam_port " + camera.port + "\n" +
      "on_movie_start curl -X POST -d \"time=%s&video=%f&event=%v&camera_id=" + camera.id + "\" http://localhost:9000/newVideo\n" +
      "on_picture_save curl -X POST -d \"picture=%f&event=%v&camera_id=" + camera.id + "\" http://localhost:9000/newPicture\n")
    writer.close()
  }

  def deleteConfigFiles(): Unit = {
    for {
      files <- Option(motionDirectory.listFiles())
      file <- files if file.getName.endsWith(".conf")
    } file.delete()
  }

}

object MotionController extends MotionController(new ConcreteNodeCamerasService(new ConcreteNodeService(), new ConcreteCameraService()))
