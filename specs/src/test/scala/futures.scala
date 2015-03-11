import fpp.specs._
import scala.concurrent._
import scala.concurrent.duration._
  
class Futures extends Meditation {
  
  "Future basics" in {
    
    def printThread(key: String) { println(s"$key: ${Thread.currentThread}") }
    def showThread[A](key: String, x: A = ()) = { printThread(key); x }
    
    showThread("Test runner")
    
    "Token Futures can be built via Future.successful" ! {
      Future.successful(showThread("Future.successful", 1)) must be_==(__).await
    }
    
    "Future() dispatches a new task in an execution context" ! {
      Future(showThread("Future.apply", 2)) must be_==(__).await
    }
  }
  
  object FutureAfter {
    def after[A](d: Duration, x: => A): Future[A] = {
      val p = Promise[A]()
      Future(Await.ready(p.future, d)).onComplete(_ => p.success(x))
      p.future
    }
  }
  
  "Introspection, blocking and callbacks" in {
    import FutureAfter._
    import scala.util.{Try, Success, Failure}
    
    val f1: Future[Int] = Future.successful(5)
    val f2: Future[Int] = Future.failed(new EqualException("POOF"))
    def f3: Future[Int] = after(1 second, 42)
    
    "Token futures are always completed" ! {
      f1.isCompleted must_== __
    }
    
    "Also when they are failed" ! {
      f2.isCompleted must_== __
    }
    
    "This future should be pending still" ! {
      f3.value must_== __
    }
    
    var sideEffect: Int = 0
    
    "Callbacks are for side effects" ! {
      f1.onComplete { _try => sideEffect = _try.getOrElse(10) }
      Thread.sleep(1)
      
      sideEffect must_== __
    }
    
    "Futures fire only the appropriate callbacks" ! { // but *you* must not forget mutable state!
      f1.onFailure { case t: Throwable => sideEffect = 2 }
      f2.onSuccess { case _ => sideEffect = 3 }
      Thread.sleep(1)
      
      sideEffect must_== __
    }
    
    "Await can be used to block a future until ready" ! {
      Await.ready(f3, 2 seconds).value must_== __
    }
    
    "Await can also just pick the result when available" ! {
      Await.result(f3, 2 seconds) must_== __
    }
    
    "Awaiting for the result of a failed future will re-throw" ! {
      Await.result(f2, 1 second) must throwA[__]
    }
  }
  
  trait WeatherInterface {
    import dispatch._
    import play.api.libs.json.{Json, JsValue}
    
    def weatherUrl = :/("api.openweathermap.org") / "data" / "2.5" / "weather"
    
    def weatherRequest(city: String) = weatherUrl <<? Map("q" -> city)
    
    def getWeather(city: String): Future[JsValue] = {
      Http(weatherRequest(city) OK as.String).map(Json.parse)
    }
  }
  
  trait CityTempHelper {
    import play.api.libs.json.JsValue
    
    def city: ((String, Double)) => String = {
      case (city, tempC) => city
    }
    
    def tempC: ((String, Double)) => Double = {
      case (city, tempC) => tempC
    }
    
    def kelvinToCelcius(k: Double) = k - 273.15
  }
  
  object CityTempLib extends WeatherInterface with CityTempHelper {
    import play.api.libs.json.JsValue
    
    def pickTempKelvin(json: JsValue) =
      (json \ "main" \ "temp").as[Double]
    
    /**
     * Use getWeather and map with pickTempKelvin to get city temperature in
     * Kelvin. Then, continue the transformation pipeline via kelvinToCelcius.
     */
    def getTemperature(city: String): Future[Double] = __
  }
  
  "Composition using map" in {
    import CityTempLib._
    
    val f1 = Future.successful(1)
    val f2 = Future.successful((_: String) + "!")
    
    "Simply incrementing the result" ! {
      f1.__ must be_==(2).await
    }
    
    "We can also put functions in any contexts" ! {
      f2.map(f => f("Hello")) must be_==(__).await
    }
    
    "Transformations can be chained one after another" ! {
      f1.map(2*).map(3*).map(4*) must be_==(__).await
    }
    
    "Let's make sure we have temperatures in Celcius" ! {
      getTemperature("Tampere") must be_<(40.0).await(0, 3 seconds)
    }
  }
  
  "Composition using flatMap and for yield" in {
    import CityTempLib._
    import FutureAfter._
    
    def sleepy(x: Int) = after(2 seconds, x)
    
    /**
     * Return a Future which will contain the sum of the arguments, each passed
     * through the `sleepy` function. Make sure your futures run in parallel.
     * Use for yield or flatMap.
     */
    def parallelSleepySum(x: Int, y: Int): Future[Int] = __
    
    /**
     * Get temperatures for both cities, then return the (city, temperature)
     * pair for the warmer city. Use for yield.
     */
    def selectWarmer(cityA: String, cityB: String): Future[(String, Double)] = __
    
    /**
     * Get temperatures for each city, resulting in List[Future[Double]]. Then,
     * use Future.sequence to turn this into Future[List[Double]]. We can then
     * take advantage of this operation being stable, i.e. it will retain the
     * order of the elements, and `zip` the cities with the temperatures to
     * obtain (city, temperature) pairs when mapping over the Future. Find the
     * maximum pair using `maxBy` and the `tempC` helper (partial) function.
     */
    def selectWarmest(cities: List[String]): Future[(String, Double)] = __
    
    "Let's make sure our futures run in parallel" ! {
      parallelSleepySum(1, 2) must be_==(3).await(0, 2.5 seconds)
    }
    
    "Hot or cold" ! {
      selectWarmer("Sao Paulo", "Vostok").map(city) must
        be("Sao Paulo").await(0, 6 seconds)
    }
    
    "Could Brazil actually not be warmest, ever" ! {
      selectWarmest(List("Sao Paulo", "Vostok", "Tampere")).map(city) must
        be("Sao Paulo").await(0, 9 seconds)
    }
    
    "What's the weather like in Ankh-Morpork" ! {
      ok // Uncomment following line for runtime exception
      //getTemperature("Ankh-Morpork") must be_>(0.0).await
    }
  }
  
  trait WeatherInterface2 {
    import dispatch._
    import play.api.libs.json.{Json, JsValue}
    
    def weatherUrl = :/("api.openweathermap.org") / "data" / "2.5" / "weather"
    
    def weatherRequest(city: String) = weatherUrl <<? Map("q" -> city)
    
    /**
     * Since the real weather service is unlikely to return many errors, let's
     * make some ourselves. Tampere's weather has been a little off lately, eh?
     */
    def getWeather: String => Future[Either[Throwable, JsValue]] = {
      case "Tampere" => Future.successful(Left(new EqualException("BOOM")))
      case city => Http(weatherRequest(city) OK as.String).either.right.map(Json.parse)
    }
  }
  
  object CityTempLib2 extends WeatherInterface2 with CityTempHelper {
    import dispatch._
    import play.api.libs.json.JsValue
    
    def invalidCity(city: String) = new EqualException(s"Invalid city: $city")
    def invalidJson(errs: Seq[_]) = new EqualException(s"Invalid JSON")

    /**
     * Parameter validation for this example is simply limiting the cities we
     * allow temperature checks for. In real use cases, this could be anything.
     * Notice how the right hand side is a Unit, since we only use the left
     * hand side for validation errors.
     */
    def isValidCity: String => Either[Throwable, Unit] = {
      case "Sao Paulo" | "Vostok" | "Tampere" | "Oulu" | "Singapore" => Right(())
      case city => Left(invalidCity(city))
    }
    
    /**
     * Now we use the Play JSON library's `validate` functionality, which will
     * result in a Either-like structure instead of throw exceptions. This
     * structure is then transformed to Either[Throwable, Double]. 
     */
    def pickTempKelvin(json: JsValue): Either[Throwable, Double] =
      (json \ "main" \ "temp").validate[Double].asEither.left.map(invalidJson)
    
    /**
     * We'll implement getTemperature as above, but now we'll validate the city
     * before fetching temperatures. We'll use Future.successful to lift the
     * validation function, which produces Either[...] to Future[Either[...]].
     * Similarly, picking the temperature now needs to be lifted. We'll further
     * use Dispatch's enriched future helpers to do a right projection over the
     * future wrapped either. Make sure to convert the Kelvin temperature to
     * Celcius at the end.
     */
    def getTemperature(city: String): Future[Either[Throwable, Double]] = __
  }
  
  "Future and Either" in {
    import CityTempLib2._
    import org.specs2.matcher._
    import dispatch._
    import scalaz.std.either._
    import scalaz.std.list._
    import scalaz.syntax.traverse._
    
    /**
     * This type is a bit of work to type so we'll define a simple type alias
     * for it. CT stands for City, Temperature.
     */
    type CT = Future[Either[Throwable, (String, Double)]]
    
    /**
     * Some type-fu to satisfy the matcher library. Ignore.
     */
    def beLeft[A](x: Any) = super.beLeft(x).asInstanceOf[Matcher[Either[Throwable, A]]]
    
    /**
     * Select warmer city works as earlier, but now we have to use projection
     * to be able to map while respecting Either semantics.
     */
    def selectWarmer(cityA: String, cityB: String): CT = __
    
    /**
     * Here's where things get a bit tricky. We'll start as before with
     * Future.sequence. Only, this time we're left afterwards with a
     * Future[List[Either[Throwable, Double]]].
     * 
     * We can't choose the warmest city without having proper values for each
     * city, but we can't get rid of the Either completely either, since any
     * call separately could be invalid or result in an error. What we'd need
     * is a structure like Future[Either[Throwable, List[Double]]]. Here, the
     * Either would be a Right if every call succeeded, and Left otherwise.
     * 
     * Unfortunately, there's no List.sequence method that could do such a
     * task for us. However, Scalaz offers a utility method `sequenceU` that
     * operates on any two nested contexts, such as List and Either (with some
     * restrictions that we will get familiar with later). The `scalaz` imports
     * above introduce this method for our use.
     * 
     * So, we'll use that helper and then map over the result's right
     * projection with the old `zip` trick and finally `maxBy` with `tempC` for
     * the maximum pair.
     */
    def selectWarmest(cities: List[String]): CT = __
    
    "What's the weather like in Ankh-Morpork now then" ! {
      getTemperature("Ankh-Morpork") must
        beLeft[Double](invalidCity("Ankh-Morpork")).await(0, 3 seconds)
    }
    
    "We're still in Celcius, right" ! {
      getTemperature("Oulu") must beRight((c: Double) => c must be_<(40.0)).await
    }
    
    "Tampere's weather should go BOOM, but not really" ! {
      getTemperature("Tampere") must beLeft(new EqualException("BOOM")).await
    }
    
    "Man it's cold in Vostok" ! {
      selectWarmer("Singapore", "Vostok").right.map(city) must
        beRight("Singapore").await(0, 6 seconds)
    }
    
    "Oulu should be safe enough competition for Brazil" ! {
      selectWarmest(List("Sao Paulo", "Vostok", "Oulu")).right.map(city) must
        beRight("Sao Paulo").await(0, 9 seconds)
    }
  }
}
