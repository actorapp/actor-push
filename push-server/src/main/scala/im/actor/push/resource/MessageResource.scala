package im.actor.push.resource

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.util.FastFuture
import com.spingo.op_rabbit.{ Message, properties }
import de.heikoseeberger.akkahttpcirce.CirceSupport
import im.actor.push.model
import im.actor.push.repo.{ SubscriptionRepo, TokenRepo }
import io.circe.JsonObject
import io.circe.generic.auto._
import io.circe.syntax._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

final case class MessageEntity(data: JsonObject)

final class MessageResource(system: ActorSystem, rabbitControl: ActorRef, db: Database) extends CirceSupport {
  import system.dispatcher

  val log = Logging(system, getClass)

  val route: Route = pathPrefix("apps" / IntNumber / "subscriptions" / Segment) { (appId, subId) ⇒
    pathEndOrSingleSlash {
      (post & entity(as[MessageEntity])) { entity ⇒
        authenticateOAuth2Async("Actor Push", authenticator(appId)) { tokAppId ⇒
          onSuccess(checkSubscription(appId, subId)) {
            case Some(subscription) ⇒
              if (subscription.appId == tokAppId) {
                val topicName = subscription.topicAmqp
                val message = entity.asJson.noSpaces

                log.debug("Sending: {} with routingKey: {}", message, topicName)
                rabbitControl !
                  Message.topic(
                    message,
                    routingKey = topicName,
                    properties = Seq(properties.DeliveryModePersistence(persistent = false))
                  )
                complete(StatusCodes.Created)
              } else {
                log.warning("Subscription id does not match token id")
                complete(StatusCodes.Forbidden)
              }
            case None ⇒
              log.warning("Subscription not found")
              complete(StatusCodes.NotFound)
          }
        }
      }
    }
  }

  private def checkSubscription(appId: Int, subId: String): Future[Option[model.Subscription]] =
    db.run(SubscriptionRepo.find(appId, subId))

  private def authenticator(id: Int): AsyncAuthenticator[Int] = {
    case p @ Credentials.Provided(_) ⇒
      db.run(TokenRepo.find(id)) map {
        case Some(token) ⇒
          if (p.verify(token.token)) Some(token.appId) else None
        case None ⇒ None
      }
    case _ ⇒ FastFuture.successful(None)
  }
}