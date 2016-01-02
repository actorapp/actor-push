package controllers

import play.api.libs.json.{JsNull, Json, JsString, JsValue}
import play.mvc.Controller
import play.mvc.Result
import play.mvc.Results._

class PushController extends Controller {
  def subscribe: Result = {
    val result: JsValue = Json.obj(
      "guid" -> "subscribe_guid_here",
      "server" -> "server_url_here",
      "channel_name" -> "server_channel_here"
    )

    // WTF?
    ok(result.toString())
  }
}