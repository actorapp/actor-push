import Keys._

scalaVersion := "2.11.7"

resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.bintrayRepo("hseeberger", "maven")
resolvers += "SpinGo OSS" at "http://spingo-oss.s3.amazonaws.com/repositories/releases"

val opRabbitV = "1.2.0"
val circeV = "0.2.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % "2.0.1",
  "de.heikoseeberger" %% "akka-http-circe" % "1.4.1",
  "io.circe" %% "circe-core" % circeV,
  "io.circe" %% "circe-generic" % circeV,
  "io.circe" %% "circe-parse" % circeV,
  "com.spingo" %% "op-rabbit-core" % opRabbitV
)

mainClass in Compile := Some("im.actor.push.PushServer")