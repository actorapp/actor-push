import Keys._

scalaVersion := "2.11.7"

resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.bintrayRepo("hseeberger", "maven")
resolvers += "SpinGo OSS" at "http://spingo-oss.s3.amazonaws.com/repositories/releases"
resolvers += Resolver.sonatypeRepo("snapshots")

val opRabbitV = "1.2.0"
val circeV = "0.2.1"
val slickV = "3.1.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.1",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.0.2",
  "de.heikoseeberger" %% "akka-http-circe" % "1.4.1",
  "io.circe" %% "circe-core" % circeV,
  "io.circe" %% "circe-generic" % circeV,
  "io.circe" %% "circe-parse" % circeV,
  "com.spingo" %% "op-rabbit-core" % opRabbitV,
  "com.typesafe.slick" %% "slick" % slickV,
  "com.typesafe.slick" %% "slick-hikaricp" % slickV,
  "org.flywaydb" % "flyway-core" % "3.2.1",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "ch.qos.logback" % "logback-classic" % "1.1.3"
)

mainClass in Compile := Some("im.actor.push.PushServer")

enablePlugins(JavaServerAppPackaging)
enablePlugins(DockerPlugin)

bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/server.conf""""
bashScriptExtraDefines += """addJava""" "-Djava.security.egd=file:/dev/urandom"

packageName in Docker := "actor-push"
version in Docker := (version in ThisBuild).value
dockerExposedPorts := Seq(9000)
dockerUpdateLatest := true
dockerRepository := Some("actor")

import ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(releaseStepCommand("docker:publish")),
  setNextVersion,
  commitNextVersion,
  pushChanges
)