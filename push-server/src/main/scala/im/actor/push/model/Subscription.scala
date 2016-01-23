package im.actor.push.model

import java.net.URLEncoder
import java.security.SecureRandom
import java.util.Base64

final case class Subscription(appId: Int, id: String) {
  def topic = s"actor.$appId.subscription.$id"

  def endpoint(baseUri: String) = s"$baseUri/v1/apps/$appId/subscriptions/${URLEncoder.encode(id, "UTF-8")}"
}

object Subscription {
  def generate(appId: Int): Subscription = {
    val ba = new Array[Byte](32)
    SecureRandom.getInstanceStrong.nextBytes(ba)
    val id = Base64.getEncoder.encodeToString(ba)
    Subscription(appId, id)
  }
}