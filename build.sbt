name := "SecureCam2"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

libraryDependencies += "io.github.nremond" %% "pbkdf2-scala" % "0.2"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5"

scalacOptions ++= Seq("-feature", "-language:postfixOps")

play.Project.playScalaSettings
