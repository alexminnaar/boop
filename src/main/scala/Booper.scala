import akka.actor.Actor

object Booper {

  case class BoopThis(id: Int, elem: Double, f: Double => Double)

  case class FinishedBoop(id: Int, boop: Double)

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
