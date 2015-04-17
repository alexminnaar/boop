package boop

import akka.actor.{Actor, PoisonPill, Props}
import boop.BoopManager.{DoneBooping, StartBooping}

object BoopMaster {

  case class begin()

  case class Done[S](res: Vector[S])

  def props[T, S](getBooped: Vector[T], f: T => S, numWorkers: Int): Props =
    Props(new BoopMaster[T, S](getBooped, f, numWorkers))
}

/**
 * Partition the input vector and receive partition results
 * @param getBooped vector to process
 * @param f function to apply to vector
 * @param numWorkers number of partitions
 */
class BoopMaster[T, S](getBooped: Vector[T], f: T => S, numWorkers: Int = 5) extends Actor {

  import boop.BoopMaster._

  var boopedArray = Vector.empty[(Int, Vector[S])]
  var groupsFinished: Int = 0
  val boopPartition = getBooped.grouped(numWorkers).toArray
  var idx = 0

  def receive = {

    case begin() => {

      boopPartition.foreach { boopGroup =>

        val groupManager = context.actorOf(BoopManager.props[T, S](boopGroup.toVector, f, idx))

        groupManager ! StartBooping

        idx += 1
      }
    }

    case DoneBooping(booped, idx) => {

      val realBooped = booped.asInstanceOf[Vector[S]]

      boopedArray :+= ((idx, realBooped))
      groupsFinished += 1

      sender ! PoisonPill

      if (groupsFinished == boopPartition.size) {

        //partitions could have arrived out of order so sort it just in case.
        val finalVector = boopedArray.sortBy(_._1).map(_._2).flatten

        context.parent ! Done(finalVector)

        context.stop(self)
      }

    }
  }

}
