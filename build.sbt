name := "SecureCam2"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

scalacOptions ++= Seq("-feature", "-language:postfixOps")

play.Project.playScalaSettings
