name := """actor-push"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"
val opRabbitVersion = "1.2.0"

resolvers += "SpinGo OSS" at "http://spingo-oss.s3.amazonaws.com/repositories/releases"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"
libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  json,

  "com.typesafe.akka" %% "akka-slf4j" % "2.4.1",
  "com.spingo" %% "op-rabbit-core"        % opRabbitVersion
//  "com.spingo" %% "op-rabbit-play-json"   % opRabbitVersion,
//  "com.spingo" %% "op-rabbit-json4s"      % opRabbitVersion,
//  "com.spingo" %% "op-rabbit-airbrake"    % opRabbitVersion,
//  "com.spingo" %% "op-rabbit-akka-stream" % opRabbitVersion
)

// libraryDependencies += "com.thenewmotion.akka" %% "akka-rabbitmq" % "2.2"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
