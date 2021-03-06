package fpp.specs

import org.specs2.mutable._
import org.specs2.reporter._
import org.specs2.time._
import akka.actor._

class Meditation extends Specification with NoTimeConversions {
  
  sequential
  stopOnFail
  
  args.report(notifier = "fpp.specs.AkkaNotifier")
  
  def __[A]: A = ???
    
  type __ = PlaceholderException
    
  class PlaceholderException extends Exception(
    "Change the __ to the proper exception type in the example"
  )
  
  implicit class PlaceholderMethod(x: Any) {
    def __[A]: A = ???
  }
}

class EqualException(val message: String) extends Exception with Equals {
  def canEqual(other: Any) = {
    other.isInstanceOf[fpp.specs.EqualException]
  }

  override def equals(other: Any) = {
    other match {
      case that: fpp.specs.EqualException => that.canEqual(EqualException.this) && message == that.message
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime + message.hashCode
  }
} 

class AkkaNotifier extends Notifier {
  
  var system: ActorSystem = _
  var actor: ActorSelection = _
  
  def specStart(title: String, location: String): Unit = {
    system = ActorSystem("specs")
    actor = system.actorSelection("akka.tcp://slides-ws@localhost:2552/user/ws")
  }
  
  def exampleSuccess(name: String, duration: Long) = {
    println(s"exampleSuccess: $name")
    actor ! ("success", name)
  }
  
  def exampleFailure(name: String, message: String, location: String, f: Throwable, details: org.specs2.execute.Details, duration: Long): Unit = {
    println(s"exampleFailure: $name, $message")
    actor ! ("failure", name, message)
  }
  
  def specEnd(title: String, location: String): Unit = {
    Thread.sleep(1000)
    system.shutdown
  }
  
  def contextEnd(text: String, location: String): Unit = ()
  def contextStart(text: String, location: String): Unit = ()
  def exampleError(name: String, message: String, location: String, f: Throwable, duration: Long): Unit = ()
  
  def examplePending(name: String, message: String, duration: Long): Unit = ()
  def exampleSkipped(name: String, message: String, duration: Long): Unit = ()
  def exampleStarted(name: String, location: String): Unit = ()
  def text(text: String, location: String): Unit = ()
}