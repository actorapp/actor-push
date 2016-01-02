package controllers;

import play.mvc.*;

public class Application extends Controller {

    public Result index() {
        return redirect("https://actor.im");
    }
}
