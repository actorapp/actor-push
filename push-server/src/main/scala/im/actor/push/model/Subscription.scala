package im.actor.push.model

import java.net.URLEncoder
import java.util.UUID

final case class Subscription(appId: Int, id: String) {
  def topic = s"actor/$appId/subscription/$id"

  def endpoint(baseUri: String) = s"$baseUri/apps/$appId/subscriptions/${URLEncoder.encode(id, "UTF-8")}"
}

object Subscription {
  def generate(appId: Int): Subscription = Subscription(appId, UUID.randomUUID().toString)
}