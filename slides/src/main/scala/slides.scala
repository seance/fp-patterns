package foo.slides

import akka.io._
import akka.actor._
import spray.can._
import spray.can.server._
import spray.can.websocket._
import spray.can.websocket.frame._
import spray.routing._
import spray.http._
import com.typesafe.config._
  
object Slides extends App {
  
  val config = ConfigFactory.load()
  val systemHttp = ActorSystem("slides-http", config.getConfig("slides-http"))
  val systemWs = ActorSystem("slides-ws",  config.getConfig("slides-ws"))
  
  val http = systemHttp.actorOf(Props[HttpSlides], "http")
  val ws = systemWs.actorOf(Props[WsSlides], "ws")
  
  IO(Http)(systemHttp) ! Http.Bind(http, interface="localhost", port=8000)
  IO(UHttp)(systemWs)  ! Http.Bind(ws, interface="localhost", port=8001)
}

class HttpSlides extends Actor with HttpService {
  def actorRefFactory = context
  def receive = runRoute {
    pathPrefix("assets") {
      getFromDirectory("assets")
    } ~
    pathEndOrSingleSlash {
      redirect("/assets/html/index.html", StatusCodes.PermanentRedirect)
    }
  }
}

class WsSlides extends Actor {
  var workers = Seq[ActorRef]()
  
  def receive = {
    case Http.Connected(remoteAddr, localAddr) =>
      val worker = context.actorOf(Props(classOf[WsSlidesWorker], sender()))
      workers = workers :+ worker
      sender() ! Http.Register(worker)
      
    case ("success", name) =>
      println(s"Success: $name")
      workers.foreach(_ ! Push(s"""{"success": true, "name": "$name"}"""))
      
    case ("failure", name, message) =>
      println(s"Failure: $name")
      workers.foreach(_ ! Push(s"""{"success": false, "name": "$name", "message": "$message"}"""))
  }
}

class WsSlidesWorker(val serverConnection: ActorRef)
  extends HttpServiceActor
  with WebSocketServerWorker
{
  def businessLogic: Receive = {
    case Push(m) => send(TextFrame(m))
  }
}

case class Push(message: String)