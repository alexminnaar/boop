package boop

import akka.actor.{Actor, ActorRef, PoisonPill, Props}


object BoopManager {

  case object StartBooping

  case class DoneBooping[S](fullyBooped: Vector[S], idx: Int)

  def props[T, S](boopThese: Vector[T], f: T => S, idx: Int): Props = Props(new BoopManager[T, S](boopThese, f, idx))

}

/**
 * Apply function to partition and return the result to BoopMaster parent
 * @param boopThese partition to process
 * @param f function to apply to partition
 * @param idx index of parition
 */
class BoopManager[T, S](boopThese: Vector[T], f: T => S, idx: Int) extends Actor {

  import boop.BoopManager._
  import boop.Booper._

  var boopsSent: Int = 0
  var boopsReceived: Int = 0
  var booped = Vector.empty[S]


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

        context.parent ! DoneBooping(booped, idx)

        myBooper ! PoisonPill
      }
    }

  }

}
