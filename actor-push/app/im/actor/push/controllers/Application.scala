package im.actor.push.controllers

import play.api.mvc._

class Application extends Controller {
  def index = Action { request =>
    Redirect("https://actor.im")
  }
}