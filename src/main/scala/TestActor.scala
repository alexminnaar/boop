import akka.actor.Actor

object TestActor{
  case class begin()
}

class TestActor extends Actor{

  import BoopMaster._
  import TestActor._

  val testVector=Vector(1.0,2.0,4.5,3.2,2.0,4.5,3.2,2.0,4.5,3.2,2.0,4.5,3.2,2.0,4.5,3.2,2.0,4.5,3.2,2.0,4.5,3.2,2.0,4.5,3.2)

  val testFunction=(x:Double)=>x+1

  val master = context.actorOf(BoopMaster.props[Double,Double](testVector.toVector,testFunction,5))

  master ! begin()

  def receive={

    case Done(res)=> {

      println(res)

      context.stop(self)
    }
  }

}
