package im.actor.push.model

import java.net.URLEncoder
import java.util.UUID

final case class Subscription(appId: Int, id: String) {
  def topicMqtt = topic('/')
  def topicAmqp = topic('.')
  def topic(sep: Char) = s"actor${sep}$appId${sep}subscription${sep}$id"

  def endpoint(baseUri: String) = s"$baseUri/apps/$appId/subscriptions/${URLEncoder.encode(id, "UTF-8")}"
}

object Subscription {
  def generate(appId: Int): Subscription = Subscription(appId, UUID.randomUUID().toString)
}