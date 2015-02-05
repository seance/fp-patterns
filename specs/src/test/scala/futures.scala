import fpp.specs._
import scala.concurrent._
  
class Futures extends Meditation {
  
  "Future basics" in {
    
    def printThread(key: String) { println(s"$key: ${Thread.currentThread}") }
    def showThread[A](key: String, x: A) = { printThread(key); x }
    
    showThread("Test runner", ())
    
    "Token Futures can be built via Future.successful" ! {
      Future.successful(showThread("Future.successful", 1)) must be_==(1).await
    }
    
    "Future() dispatches a new task in an execution context" ! {
      Future(showThread("Future.apply", 2)) must be_==(2).await
    }
  }
  
  "Introspection, blocking and callbacks" in {
    "" >> ok
  }
  
  "Composition using map" in {
    "" >> ok
  }
  
  "Composition using flatMap and for yield" in {
    "" >> ok
  }
}