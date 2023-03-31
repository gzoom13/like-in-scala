ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "like-in-Scala",
    libraryDependencies ++= Seq(
      "org.typelevel"                 %% "cats-effect"         % "3.4.8",
      "dev.optics"                    %% "monocle-core"        % "3.1.0",
      "dev.optics"                    %% "monocle-macro"       % "3.1.0",
      "com.softwaremill.sttp.client3" %% "http4s-backend"      % "3.8.12",
      "org.http4s"                    %% "http4s-blaze-client" % "0.23.13"
    )
  )
