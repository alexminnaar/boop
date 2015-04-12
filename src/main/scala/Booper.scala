import akka.actor.Actor

object Booper {

  case class BoopThis[T,S](id: Int, elem: T, f: T => S)

  case class FinishedBoop[S](id: Int, boop: S)

}

class Booper extends Actor {

  import Booper._

  def receive = {

    case BoopThis(id, elem, f) => {

      val booped = f(elem)

      sender ! FinishedBoop(id, booped)
    }
  }
}
