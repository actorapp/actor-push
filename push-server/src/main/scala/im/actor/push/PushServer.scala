package im.actor.push

import akka.actor.{ ActorSystem, Props }
import akka.event.Logging
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.spingo.op_rabbit._
import im.actor.push.resource.{ MessageResource, SubscriptionResource }
import org.flywaydb.core.Flyway
import slick.driver.PostgresDriver.api._
import slick.jdbc.hikaricp.HikariCPJdbcDataSource

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object PushServer extends App {
  implicit val system = ActorSystem("push-server")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  val log = Logging(system, getClass)

  val rootLogger = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger]
  rootLogger.setLevel(ch.qos.logback.classic.Level.INFO)

  try {

    val ds =
      HikariCPJdbcDataSource.forConfig(
        system.settings.config.getConfig("sql"),
        null,
        "postgres",
        getClass.getClassLoader
      )

    val flyway = new Flyway()
    flyway.setDataSource(ds.ds)
    flyway.setBaselineOnMigrate(true)
    flyway.migrate()

    val db = Database.forDataSource(ds.ds)

    val rabbitControl = system.actorOf(Props[RabbitControl])

    val subsRes = new SubscriptionResource(system, rabbitControl, db).route
    val msgRes = new MessageResource(system, rabbitControl, db).route

    val route = msgRes ~ subsRes

    val bindFuture = Http(system).bindAndHandle(route, "0.0.0.0", 9000)
    bindFuture onFailure {
      case e ⇒
        system.terminate()
        log.error(e, "Failed to bind")
    }

  } catch {
    case e: Throwable ⇒
      log.error(e, "Failed to start")
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
}
