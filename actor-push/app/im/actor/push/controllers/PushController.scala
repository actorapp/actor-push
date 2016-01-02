package im.actor.push.controllers

import akka.util.Timeout
import com.spingo.op_rabbit.Message
import com.spingo.op_rabbit.Message.ConfirmResponse
import im.actor.push.broker.Rabbit
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results._
import play.api.mvc._
import play.mvc.Controller
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.pattern.ask

class PushController extends Controller {

  def send(channel: String) = Action { request =>

    implicit val timeout = Timeout(5 seconds)
    val received = (
      Rabbit.rabbitControl ? Message.queue("ping",
        queue = channel)
      ).mapTo[ConfirmResponse]

    def res = Await.result(received, timeout.duration)

    Ok(Json.obj(
      "result" -> "ok",
      "channel" -> channel
    ))
  }
}