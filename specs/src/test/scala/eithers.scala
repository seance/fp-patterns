import fpp.specs._

class Eithers extends Meditation {
  
  "Either basics" in {
    
    "Right wraps success value" ! (__ must beRight("Success"))
      
    "Left wraps error value" ! (__ must beLeft("Error"))
  }
  
  "Folding and introspecting" in {
    
    val e1: Either[String, Int] = Right(5)
    val e2: Either[String, Int] = Left("Fail")
      
    "We can check if we have a Right instance" ! (e1.isRight must_== __)
      
    "Or if we have a Left instance" ! (e2.isLeft must_== __)
      
    "Folding produces an unwrapped value" ! {
      e1.fold(left => s"It's Left",
              right => s"It's Right") must_== __
    }
    
    "Folding can be used as a fallback also" ! {
      e2.fold(left => 42,
              right => right) must_== __
    }
  }
  
  "Bias and projections" in {
    
    val e1: Either[String, Int] = Right(5)
    val e2: Either[String, Int] = Left("Fail")
    
    "Use the 'right' method to get a Right projection" ! {
      e1.__ must beAnInstanceOf[Either.RightProjection[String, Int]]
    }
    
    "Left projection is seldom needed in practise" ! {
      e1.__ must beAnInstanceOf[Either.LeftProjection[String, Int]]
    }
  }
  
  "Composition of projections using map" in {
    
    val e1: Either[String, Int] = Right(5)
    val e2: Either[String, Int] = Left("Boom")
    
    def timesThree(x: Int) = x * 3
    def moduloFour(x: Int) = x % 4
    def appendBang(s: String) = s"$s!"
    
    "Mapping over right projection makes Right analogous to Some" ! {
      e1.right.map(moduloFour) must_== __
    }
    
    "Mapping over left projection is similar for Left" ! {
      e2.left.map(appendBang) must_== __
    }
    
    "With a right projection, Left is inert over mapping" ! {
      e2.right.map(timesThree) must_== __
    }
    
    "With a left projection, Right is similarly inert" ! {
      e1.left.map(appendBang) must_== __
    }
    
    "With map's static signature, each map must be preceded by projection" ! {
      e1.__ must_== 3
    }
    
    "This is true for left projection also" ! {
      e2.__ must_== "Bang!!"
    }
  }
  
  /**
   * Example library providing networking utilities. Separated to its own
   * module to allow reuse in the following sections.
   */
  object NetLibrary {
    
    case class Connection(n: Int)
    
    /**
     * Regular expression matching valid URLs for the example.
     */
    val ValidUrl = """host(\d+)""".r

    /**
     * Pattern matching can be used with regexes to extract capture groups.
     */
    def connect(url: String): Either[String, Connection] = url match {
      case ValidUrl(n) => Right(Connection(n.toInt))
      case _           => Left("Connection refused")
    }
    
    /**
     * In reality read errors might occur because of many reasons, here
     * simplified for the example.
     */
    def read(conn: Connection): Either[String, String] =
      if (conn.n % 2 == 0) Right(s"Host ${conn.n} contents")
      else Left(s"Error reading host ${conn.n}")
  }
  
  "Composition of projections using flatMap" in {

    /**
     * Let's import the net utilities into current scope first.
     */
    import NetLibrary._
    
    /**
     * Slurp composes connect and read into a single composite operation that
     * can report errors at each of its stages.
     */
    def slurp(url: String): Either[String, String] = __
    
    "Slurping from a good URL should yield its contents" ! {
      slurp("host0") must beRight("Host 0 contents")
    }
    
    "Slurping from a valid but odd host should result in read error" ! {
      slurp("host1") must beLeft("Error reading host 1")
    }
    
    "Slurping from an invalid URL must result in connect error" ! {
      slurp("invalid") must beLeft("Connection refused")
    }
    
    "What happens if we slurp two URLs?" ! {
      slurp("host2").right.flatMap { c1 =>
        slurp("host4").right.map(c2 => c1 + c2) 
      } must_== __
    }
    
    "What if one of them is bad?" ! {
      slurp("host3").right.flatMap { c1 =>
        slurp("host6").right.map(c2 => c1 + c2) 
      } must_== __
    }
  }
  
  "Composition of projections using for yield" in {
    
    import NetLibrary._
    
    /**
     * Reimplement slurp using the for yield construct.
     */
    def slurp(url: String): Either[String, String] = __
    
    "Slurping from a good URL should yield its contents #2" ! {
      slurp("host0") must beRight("Host 0 contents")
    }
    
    "Slurping from a valid but odd host should result in read error #2" ! {
      slurp("host1") must beLeft("Error reading host 1")
    }
    
    "Slurping from an invalid URL must result in connect error #2" ! {
      slurp("invalid") must beLeft("Connection refused")
    }
    
    /**
     * Let's reimplement the two-URL slurp from the previous section. This
     * time we'll use for yield and abstract the operation into its own
     * function, slurpConcat.
     * 
     * Use slurp and composition with for yield. Remember to use the
     * appropriate projection(s).
     */
    def slurpConcat(url1: String, url2: String): Either[String, String] = __
    
    "Slurping two good URLs with slurpConcat" ! {
      slurpConcat("host2", "host4") must_== __
    }
    
    "Slurping good and bad URL with slurpConcat" ! {
      slurpConcat("host6", "host1") must_== __
    }
    
    "Slurping bad and good URL with slurpConcat" ! {
      slurpConcat("host3", "host8") must_== __
    }
    
    def banana = for {
      x1 <- Right(1).right
      x2 <- Left("BOOM").right
      x3 <- Right(3).right
    }
    yield x3 
    
    "Left gracefully short-circuits contextual composition" ! {
      banana must_== __
    }
  }
  
  "Joining nested Eithers" in {
    
    val ex = new Exception()
    val e1: Either[Throwable, Either[Exception, Int]] = Right(Right(1))
    val e2: Either[Throwable, Either[Exception, Int]] = Right(Left(ex))
    val e3: Either[Throwable, Either[Exception, Int]] = Left(ex)
    
    class Super
    class Sub extends Super
    
    val sub = new Sub
    val e4: Either[Either[String, Sub], Super] = Right(sub)
    val e5: Either[Either[String, Sub], Super] = Left(Right(sub))
    val e6: Either[Either[String, Sub], Super] = Left(Left("Confused?"))
    
    val e7: Either[Exception, Either[Throwable, Int]] = Right(Right(1))
    val e8: Either[String, Int] = Right(1)
    
    "Joining right over Right gives the nested Either" ! {
      e1.joinRight must_== __
    }
    
    "Joining right just as above" ! {
      e2.joinRight must_== __
    }
    
    "Joining right over Left gives the wrapping Either" ! {
      e3.joinRight must_== __
    }
    
    "Joining left over Left gives the nested Either" ! {
      e4.joinLeft must_== __
    }
    
    "Joining left just as above" ! {
      e5.joinLeft must_== __
    }
    
    "Joinining left over Right gives the wrapping Either" ! {
      e6.joinLeft must_== __
    }
    
    // Uncomment to get compile errors
    // e7.joinRight // Exception is not assignable from Throwable
    // e8.joinRight // No nesting, no joining
  }
}
