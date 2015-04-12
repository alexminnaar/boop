import akka.actor.{PoisonPill, Props, ActorRef, Actor}

import scala.reflect.ClassTag


object BoopManager {

  case object StartBooping

  case class DoneBooping[S](fullyBooped: Vector[S],idx:Int)

  def props[T, S](boopThese: Vector[T], f: T => S,idx :Int): Props = Props(new BoopManager[T, S](boopThese, f,idx))

}


class BoopManager[T, S](boopThese: Vector[T], f: T => S, idx:Int) extends Actor {

  import BoopManager._
  import Booper._

  var boopsSent: Int = 0
  var boopsReceived: Int = 0
  var booped =  Vector.empty[S]


  val myBooper: ActorRef = context.actorOf(Props[Booper], "booper")

  def receive = {

    case StartBooping => {

      boopThese.foreach { el =>
        myBooper ! BoopThis(boopsSent, el, f)
        boopsSent += 1
      }
    }


    case FinishedBoop(id, boop) => {

      boopsReceived += 1
      booped :+= boop.asInstanceOf[S]


      if (boopsSent == boopsReceived) {

        context.parent ! DoneBooping(booped,idx)

        myBooper ! PoisonPill
      }
    }

  }

}
