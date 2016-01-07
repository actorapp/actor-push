package im.actor.push.resource

import java.util.UUID

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.spingo.op_rabbit._
import de.heikoseeberger.akkahttpcirce.CirceSupport
import io.circe.generic.auto._
import io.circe.syntax._

import scala.collection.JavaConversions._

final case class Data[T](data: T)

final case class CreateResult(topic: String, endpoint: Endpoint)

final case class Endpoint(
  hosts:    Seq[String],
  username: String,
  password: String
)

final class TopicResource(system: ActorSystem, rabbitControl: ActorRef) extends CirceSupport {
  val hosts = system.settings.config.getStringList("op-rabbit.connection.hosts").toList
  val username = system.settings.config.getString("op-rabbit.connection.username")
  val password = system.settings.config.getString("op-rabbit.connection.password")

  val route: Route = pathPrefix("v1" / "topics") {
    post {

      val uuid = UUID.randomUUID().toString
      val topicName = "actor.client." + uuid
      rabbitControl ! Message.topic("create", routingKey = topicName)
      complete(Data(CreateResult(topicName, Endpoint(hosts, username, password))).asJson)
    }
  }
}