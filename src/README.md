#Parallel and Concurrent Maps with boop!

If you want to apply a function to each element of a Scala vector this can be done sequentially (i.e. with a map).
But if you have multiple threads at your disposal then why not parallelize it?  This can be done with the help of
Akka.  

Specifically, the vector is broken up into a number of partitions (which is done in ```BoopMaster```) and in each of
these partitions the function is applied in parallel to each vector element (which is done in ```BoopManager``` and
```Booper```).  When the partitions are finished, they return their processed vector back to ```BoopMaster``` which 
combines them and returns the result.  This is particularly easy to do in Scala due to the Akka actor model (to
asynchronously process the vector partitions) and its functional programming capabilities (to pass the function that
will be applied to the vector partitions).

Consider the example where you have a vector of strings and you want to perform a word count on each element.  This can
be done by creating a ```BoopMaster``` actor and passing it the required parameters which are

* The vector itself.
* The function to be applied to each element.
* The number of partitions to make (i.e. the level of parallelism).

This example is implemented in the below code

```scala
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
```

Note: You are only likely to see speed-ups for large vectors and/or computationally intense functions.  Otherwise the
overhead created by partitioning, creating actors, etc. might actually make things slower