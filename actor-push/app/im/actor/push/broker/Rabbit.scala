package im.actor.push.broker

import com.spingo.op_rabbit.RabbitControl
import akka.actor.Props

object Rabbit {
   val rabbitControl = play.api.Play.current.actorSystem.actorOf(Props[RabbitControl])
}