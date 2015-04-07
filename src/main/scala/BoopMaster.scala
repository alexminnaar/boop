import BoopManager.{DoneBooping, StartBooping}
import akka.actor.{PoisonPill, Props, Actor}

class BoopMaster extends Actor {

  val getBooped = Array.fill(100)(0.0)

  def f(x: Double) = x + 1

  val numWorkers = 5


  var startIdx: Int = 0
  var boopedArray: Array[Double] = Array.empty
  var groupsFinished: Int = 0

  val boopPartition = getBooped.grouped(numWorkers).toArray


  boopPartition.foreach { boopGroup =>

    val groupManager = context.actorOf(BoopManager.props(boopGroup, startIdx))

    groupManager ! StartBooping(f)

    startIdx += boopGroup.size
  }

  def receive = {

    case DoneBooping(booped, startIdx) => {

      boopedArray ++= booped
      groupsFinished += 1

      sender ! PoisonPill

      if (groupsFinished == boopPartition.size) {
        println("Done Booping!")
        println(boopedArray.toList)
        context.stop(self)
      }

    }
  }

}
