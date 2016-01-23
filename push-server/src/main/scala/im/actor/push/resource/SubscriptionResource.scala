package im.actor.push.resource

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import im.actor.push.model
import com.spingo.op_rabbit._
import de.heikoseeberger.akkahttpcirce.CirceSupport
import im.actor.push.repo.SubscriptionRepo
import slick.driver.PostgresDriver.api._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.Future

final case class Data[T](data: T)
final case class SubscribeResult(endpoint: String)

final class SubscriptionResource(system: ActorSystem, rabbitControl: ActorRef, db: Database) extends CirceSupport {
  import system.dispatcher

  val baseUri = system.settings.config.getString("base-uri")

  val route: Route = pathPrefix("apps" / IntNumber / "subscriptions") { appId ⇒
    post {
      val subscription = model.Subscription.generate(appId)
      onComplete(subscribe(subscription)) { _ ⇒
        complete(Data(SubscribeResult(
          endpoint = subscription.endpoint(baseUri)
        )).asJson)
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