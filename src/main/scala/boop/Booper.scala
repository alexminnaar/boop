package boop

import akka.actor.Actor

object Booper {

  case class BoopThis[T, S](id: Int, elem: T, f: T => S)

  case class FinishedBoop[S](id: Int, boop: S)

}

/**
 * Simply apply the function to the element
 */
class Booper extends Actor {

  import boop.Booper._

  def receive = {

    case BoopThis(id, elem, f) => {

      val booped = f(elem)

      sender ! FinishedBoop(id, booped)
    }
  }
}
