import Dependencies._

ThisBuild / scalaVersion     := "2.12.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.tudux"
ThisBuild / organizationName := "tudux"

val http4sVersion = "0.23.18"
val http4sBlaze = "0.23.13"

lazy val root = (project in file("."))
  .settings(
    name := "http4s-intro",

    libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.8",
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sBlaze,
    "org.http4s" %% "http4s-blaze-client" % http4sBlaze,
    "dev.profunktor" %% "http4s-jwt-auth" % "1.2.0"
  )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
