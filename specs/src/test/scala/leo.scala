import fpp.specs._

class Leo extends Meditation {
  
  "Option and Either" in {

    val o1: Option[Int] = Some(3)
    val o2: Option[Int] = None
      
    val e1: Either[String, Int] = Right(5)
    val e2: Either[String, Int] = Left("BOOM")
    
    "Option's toRight needs help with None" ! {
      (o1.toRight("BANG") must_== Right(3)) and
      (o2.toRight("BOOM") must_== Left("BOOM"))
    }
    
    "Option's toLeft is analogous but seldom needed" ! {
      (o1.toLeft("FOO") must_== Left(3)) and
      (o2.toLeft("BAR") must_== Right("BAR"))
    }
    
    "Either's right projection turns easily into Option" ! {
      (e1.right.toOption must_== Some(5)) and
      (e2.right.toOption must_== None)
    }
    
    "Either's left projection works similarly" ! {
      (e1.left.toOption must_== None) and
      (e2.left.toOption must_== Some("BOOM"))
    }
    
    object NetLibraryWithChannels {
    
      case class Connection(n: Int)
      case class Channel(key: String)

      val ValidUrl = """host(\d+)""".r
      val ValidChan = """3chan""".r

      def connect(url: String): Either[String, Connection] = url match {
        case ValidUrl(n) => Right(Connection(n.toInt))
        case _           => Left("Connection refused")
      }
      
      def getChannel(conn: Connection, key: String): Option[Channel] = key match {
        case "2chan" => Some(Channel("2chan"))
        case "3chan" => Some(Channel("3chan"))
        case _       => None
      }

      def read(chan: Channel): Either[String, String] = chan.key match {
        case ValidChan() => Right(s"Channel ${chan.key} contents")
        case _           => Left(s"Error reading channel ${chan.key}")
      }
    }
    
    import NetLibraryWithChannels._
    
    /**
     * Use for yield to compose connect, getChannel and read. Since getChannel
     * returns an Option, you must convert it into Either for the composition
     * to work. Since we're dealing with Either, you must also use the proper
     * projections for the compositions.
     */
    def slurp(url: String, chankey: String): Either[String, String] = for {
      conn <- connect(url).right
      chan <- getChannel(conn, chankey).toRight(s"Channel $chankey not found").right
      cnts <- read(chan).right
    }
    yield cnts
    
    "Slurping with keyed channels" ! {
      (slurp("badhost", "3chan") must_== Left("Connection refused")) and
      (slurp("host2", "4chan") must_== Left("Channel 4chan not found")) and
      (slurp("host3", "5chan") must_== Left("Channel 5chan not found")) and
      (slurp("host4", "2chan") must_== Left("Error reading channel 2chan")) and
      (slurp("host5", "3chan") must_== Right("Channel 3chan contents"))
    }
  }
  
  "Option and List" in {
    
    val xs1: List[String] = List("Good", "Bad")
    val xs2: List[Int] = List()
      
    val o1: Option[String] = Some("Hello")
    val o2: Option[Double] = None
    
    "Maybe getting Some head" ! {
      (xs1.headOption must_== Some("Good")) and
      (xs2.headOption must_== None)
    }
    
    "Option is like one-element or empty list" ! {
      (o1.toList must_== List("Hello")) and
      (o2.toList must_== List())
    }
    
    val xs3 = List(Some(9), None, Some(5), Some(7), None, Some(3))
    val xs4 = List[Option[Int]](None, None, None)
    val xs5 = List[Option[Int]]()
    
    /**
     * We'll borrow NonEmptyList from Scalaz.
     */
    import scalaz.syntax.std.list._
    
    /**
     * Use for yield, converting Option to List. Use List's toNel method from
     * Scalaz to get a Option[NonEmptyList[Int]]. Then map over the Option and
     * take the sum of the elements (found in the NEL's `list` member).
     */
    def someSum(xs: List[Option[Int]]): Option[Int] = (for {
      o <- xs
      n <- o.toList // .toList is optional, Scala predefines an implicit conversion Option => List
    }
    yield n).toNel.map(_.list.sum)
    
    /**
     * This time use flatMap and List's reduceOption method using Int's max
     * method as the reducer to find the maximum, if any.
     */
    def someMax(xs: List[Option[Int]]): Option[Int] =
      xs.flatMap(_.toList).reduceOption(_ max _)
      
    /**
     * Implement here as above but now we'll make the function a higher-order
     * function which takes the reducer function as a parameter.
     */
    def someOp(xs: List[Option[Int]], op: (Int, Int) => Int): Option[Int] =
      xs.flatMap(_.toList).reduceOption(op)
      
    def min = (_: Int) min (_: Int)
    def mod = (_: Int) % (_: Int)
      
    "Some summing with Option and List" ! {
      (someSum(xs3) must_== Some(24)) and
      (someSum(xs4) must_== None) and
      (someSum(xs5) must_== None)
    }
    
    "Some maxing with Option and List" ! {
      (someMax(xs3) must_== Some(9)) and
      (someMax(xs4) must_== None) and
      (someMax(xs5) must_== None)
    }
    
    "Some HOFing with Option and List" ! {
      (someOp(xs3, min) must_== Some(3)) and
      (someOp(xs3, mod) must_== Some(1)) and
      (someOp(xs5, mod) must_== None)
    }
  }
  
  "List and Either" in {
    
    val xs1: List[Int] = List(1, 2, 3)
    val xs2: List[Int] = List()
    
    import scalaz.syntax.std.list._
      
    def noItems = "No items"
      
    /**
     * Sum items, if any. Otherwise, produce Left using the noItems helper
     * function. Use toNel and Option to Either conversion.
     */
    def sumMandatoryItems(xs: List[Int]): Either[String, Int] =
      xs.toNel.map(_.list.sum).toRight(noItems)
    
    /**
     * This function retains the same signature as the above function.
     * However, this time an empty argument list should result in a
     * Right containing zero sum.
     */
    def sumOptionalItems(xs: List[Int]): Either[String, Int] =
      Right(xs.toNel.map(_.list.sum).getOrElse(0))
      
    "Sum of mandatory items with non-empty list succeeds" ! {
      sumMandatoryItems(xs1) must_== Right(6)
    }
    
    "Sum of mandatory items with empty list fails" ! {
      sumMandatoryItems(xs2) must_== Left("No items")
    }
    
    "Sum of optional items with non-empty list succeeds" ! {
      sumOptionalItems(xs1) must_== Right(6)
    }
    
    "Sum of optional items with empty list produces zero" ! {
      sumOptionalItems(xs2) must_== Right(0)
    }
  }
}