package examples

import akka.actor.Actor
import boop.BoopMaster

class WordCountExample extends Actor {

  import boop.BoopMaster._

  val testVector = Vector("one"
    , "two words"
    , "three words words"
    , "four words words words"
    , "five words words words words"
    , "six words words words words words"
    , "seven words words words words words words"
    , "eight words words words words words words words"
    , "nine words words words words words words words words"
    , "ten words words words words words words words words words")

  val testFunction = (x: String) => x.split(" ").length

  val master = context.actorOf(BoopMaster.props[String, Int](testVector.toVector, testFunction, 5))

  master ! begin()

  def receive = {

    case Done(res) => {

      println(res)

      context.stop(self)
    }
  }

}