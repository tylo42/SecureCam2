name := "SecureCam2"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

libraryDependencies += "io.github.nremond" %% "pbkdf2-scala" % "0.2"

scalacOptions ++= Seq("-feature", "-language:postfixOps")

play.Project.playScalaSettings
