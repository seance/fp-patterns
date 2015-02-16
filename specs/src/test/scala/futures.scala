import fpp.specs._
import scala.concurrent._
import scala.concurrent.duration._
  
class Futures extends Meditation {
  
  "Future basics" in {
    
    def printThread(key: String) { println(s"$key: ${Thread.currentThread}") }
    def showThread[A](key: String, x: A = ()) = { printThread(key); x }
    
    showThread("Test runner")
    
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
  
  object WeatherLib {
    import dispatch._
    import play.api.libs.json.{Json, JsValue}
    
    def weatherUrl = :/("api.openweathermap.org") / "data" / "2.5" / "weather"
    
    def weatherRequest(city: String) = weatherUrl <<? Map("q" -> city)
    
    def getWeather(city: String): Future[JsValue] = {
      Http(weatherRequest(city) OK as.String).map(Json.parse)
    }
  }
  
  object CityTemperatureLib {
    import WeatherLib._
    import play.api.libs.json.JsValue
    
    def city: ((String, Double)) => String = {
      case (city, tempC) => city
    }
    
    def tempC: ((String, Double)) => Double = {
      case (city, tempC) => tempC
    }
    
    def kelvinToCelcius(k: Double) = k - 273.15
    
    def pickTempKelvin(json: JsValue) =
      (json \ "main" \ "temp").as[Double]
    
    def getTemperature(city: String): Future[Double] = __
  }
  
  "Composition using map" in {
    import CityTemperatureLib._
    
    "" >> ok
  }
  
  "Composition using flatMap and for yield" in {
    import CityTemperatureLib._
    
    def selectWarmer(cityA: String, cityB: String): Future[(String, Double)] = __
    
    def selectWarmest(cities: List[String]): Future[(String, Double)] = __
    
    "Could Brazil actually not be warmest, ever" ! {
      selectWarmest(List("Sao Paulo", "Vostok", "Oulu")).map(city) must
        be("Sao Paulo").await(0, 3 seconds)
    }
    
    "What's the weather like in Ankh-Morpork" ! {
      ok // Uncomment following line for runtime exception
      //getTemperature("Ankh-Morpork") must be_>(0.0).await
    }
  }
  
  "Future and Either" in {
    
    "" >> ok
  }
}
