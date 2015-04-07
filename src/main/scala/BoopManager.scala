import akka.actor.{PoisonPill, Props, ActorRef, Actor}


object BoopManager {

  case class StartBooping(f: Double => Double)

  case class DoneBooping(fullyBooped: Array[Double], startIdx: Int)

  def props(boopThese:Array[Double],startIdx:Int)=Props(classOf[BoopManager],boopThese,startIdx)

}


class BoopManager(boopThese: Array[Double], startIdx: Int) extends Actor {

  import BoopManager._
  import Booper._

  var boopsSent: Int = 0
  var boopsReceived: Int = 0
  var booped: Array[Double] = Array.fill(boopThese.size)(0.0)


  val myBooper: ActorRef = context.actorOf(Props[Booper], "booper")

  def receive = {

    case StartBooping(f) => {

      boopThese.foreach { el =>
        myBooper ! BoopThis(boopsSent, el, f)
        boopsSent += 1
      }
    }


    case FinishedBoop(id, boop) => {

      boopsReceived += 1
      booped(id) = boop

      //All elements have been transformed
      if (boopsSent == boopsReceived) {

        context.parent ! DoneBooping(booped, startIdx)

        myBooper ! PoisonPill
      }
    }


  }

}
