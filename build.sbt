import sbt.plugins.IvyPlugin.buildSettings

name := "redis"

version := "0.1"

scalaVersion := "2.13.6"

lazy val main = (project in file("."))
  .settings(
    buildSettings,
    libraryDependencies ++= Seq(
      "net.debasishg" %% "redisclient" % "3.30",
      "org.slf4j" % "slf4j-log4j12" % "1.7.32",
      "org.apache.logging.log4j" % "log4j-core" % "2.14.1",
      "org.apache.logging.log4j" % "log4j-api" % "2.14.1"

    ),
    mainClass in assembly := Some("RedisLock")
  )
