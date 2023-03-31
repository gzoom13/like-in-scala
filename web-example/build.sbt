val tapirVersion = "1.2.9"

lazy val rootProject = (project in file(".")).settings(
  Seq(
    name := "web-example",
    version := "0.1.0-SNAPSHOT",
    organization := "ru.tinkoff.likeinscala",
    scalaVersion := "3.2.2",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-vertx-server-cats" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-cats" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "ch.qos.logback" % "logback-classic" % "1.4.6",
      "io.getquill" %% "quill-jasync-postgres" % "4.6.0.1",
      "org.typelevel" %% "cats-effect" % "3.4.8",
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "com.softwaremill.sttp.client3" %% "circe" % "3.8.13" % Test
    )
  )
)
