package im.actor.push.resource

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.util.FastFuture
import com.spingo.op_rabbit._
import com.typesafe.config._
import im.actor.push.model
import de.heikoseeberger.akkahttpcirce.CirceSupport
import im.actor.push.repo.SubscriptionRepo
import slick.driver.PostgresDriver.api._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.collection.JavaConversions._
import scala.concurrent.Future

final case class Data[T](data: T)
final case class SubscribeResult(
  endpoint: String,
  mqtt:     MQTT
)
final case class MQTT(
  hosts:       Seq[String],
  virtualHost: String,
  username:    String,
  password:    String,
  port:        Int
)
object MQTT {
  def fromConfig(config: Config): MQTT = {
    MQTT(
      hosts = config.getStringList("op-rabbit.connection.hosts").toSeq,
      virtualHost = config.getString("op-rabbit.connection.virtual-host"),
      username = config.getString("op-rabbit.connection.username"),
      password = config.getString("op-rabbit.connection.password"),
      port = config.getInt("op-rabbit.connection.port")
    )
  }
}

final class SubscriptionResource(system: ActorSystem, rabbitControl: ActorRef, db: Database) extends CirceSupport {
  import system.dispatcher

  val baseUri = system.settings.config.getString("base-uri")
  val mqtt = MQTT.fromConfig(system.settings.config)

  val route: Route = pathPrefix("apps" / IntNumber / "subscriptions") { appId ⇒
    pathEndOrSingleSlash {
      post {
        val subscription = model.Subscription.generate(appId)
        onSuccess(subscribe(subscription)) {
          complete(Data(SubscribeResult(
            endpoint = subscription.endpoint(baseUri),
            mqtt = mqtt
          )).asJson)
        }
      }
    }
  }

  private def subscribe(subscription: model.Subscription): Future[Unit] = {
    for {
      _ ← db.run(SubscriptionRepo.create(subscription))
    } yield {
      val topicName = subscription.topic
      rabbitControl ! Message.topic("create", routingKey = topicName)
      ()
    }
  }
}