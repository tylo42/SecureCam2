name := "SecureCam2"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

libraryDependencies += "io.github.nremond" %% "pbkdf2-scala" % "0.2"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "0.8.0"

scalacOptions ++= Seq("-feature", "-language:postfixOps")

play.Project.playScalaSettings
