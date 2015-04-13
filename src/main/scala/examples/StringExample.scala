package examples

import akka.actor.Actor
import boop.BoopMaster


class StringExample extends Actor {

  import boop.BoopMaster._

  val testVector = Vector("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten")

  val testFunction = (x: String) => x + " booped"

  val master = context.actorOf(BoopMaster.props[String, String](testVector.toVector, testFunction, 5))

  master ! begin()

  def receive = {

    case Done(res) => {

      println(res)

      context.stop(self)
    }
  }

}
