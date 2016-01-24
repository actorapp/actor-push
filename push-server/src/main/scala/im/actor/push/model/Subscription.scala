package im.actor.push.model

import java.net.URLEncoder
import java.util.Base64

import im.actor.push.util.ThreadLocalSecureRandom

final case class Subscription(appId: Int, id: String) {
  def topic = s"actor.$appId.subscription.$id"

  def endpoint(baseUri: String) = s"$baseUri/apps/$appId/subscriptions/${URLEncoder.encode(id, "UTF-8")}"
}

object Subscription {
  def generate(appId: Int): Subscription = {
    val ba = new Array[Byte](32)
    ThreadLocalSecureRandom.current().nextBytes(ba)
    val id = Base64.getEncoder.encodeToString(ba).replace("=", "").replace("/", "")
    Subscription(appId, id)
  }
}