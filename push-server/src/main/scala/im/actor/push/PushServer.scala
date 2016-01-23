package im.actor.push

import akka.actor.{ ActorSystem, Props }
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.spingo.op_rabbit._
import im.actor.push.resource.SubscriptionResource
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

  val ds =
    HikariCPJdbcDataSource.forConfig(
      system.settings.config.getConfig("postgresql"),
      null,
      "postgres",
      getClass.getClassLoader
    )

  val flyway = new Flyway()
  flyway.setDataSource(ds.ds)
  flyway.migrate()

  val db = Database.forDataSource(ds.ds)

  val rabbitControl = system.actorOf(Props[RabbitControl])

  val topicRes = new SubscriptionResource(system, rabbitControl, db).route

  val route = topicRes

  val bindFuture = Http(system).bindAndHandle(route, "0.0.0.0", 9000)

  bindFuture onFailure {
    case e ⇒
      log.error(e, "Failed to bind")
  }

  Await.result(system.whenTerminated, Duration.Inf)
}
