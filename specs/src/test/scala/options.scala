import fpp.specs._

class Options extends Meditation {
  
  "Option basics" in {
    
    "Some wraps a value" ! (Some(42) must beSome(42))

    "None represents a missing value" ! (None must beNone)
  }
  
  "Unwrapping and introspecting" in {
    
    val optionA: Option[Int] = Some(1)
    val optionB: Option[Int] = None

    "Introspecting reveals a value is present" ! (optionA.isEmpty must_== false)

    "Or it may reveal that there is no value" ! (optionB.isDefined must_== false)

    "Option's value can be retrieved using get" ! (optionA.get must_== 1)

    "But getting from None throws an exception" ! {
      optionB.get must throwA[NoSuchElementException]
    }

    "Best practise is to always provide a fallback" ! {
      (optionA.getOrElse(13) must_== 1) and
      (optionB.getOrElse(13) must_== 13)
    }
  }
  
  "Composition using map" in {
    
    def sqrt(x: Double): Option[Double] =
      if (x >= 0) Some(math.sqrt(x)) else None
      
    def increment(x: Double): Double = x + 1
      
    def triple(x: Double): Double = x * 3
      
    "Mapping over Some produces Some" ! {
      sqrt(9).map(increment) must_== Some(4)
    }
    
    "Mapping over None is fine too, and produces None" ! {
      sqrt(-1).map(increment) must_== None
    }
    
    "Things get interesting when combining compositions" ! {
      (sqrt(9).map(increment).map(triple) must_== Some(12)) and
      (sqrt(-1).map(increment).map(triple) must_== None)
    }
    
    /**
     * Function composition of increment and triple.
     */
    def incrementTriple = (triple _) compose (increment _) // nested calls or `compose`
    
    "Combined composition equals composed combination" ! {
      sqrt(9).map(increment).map(triple) must_== sqrt(9).map(incrementTriple)
    }
    
    "Composed computation need only be unwrapped at the end" ! {
      (sqrt(9).map(increment).map(triple).getOrElse(0) must_== 12) and
      (sqrt(-1).map(triple).map(increment).getOrElse(5) must_== 5)
    }
  }
  
  "Composition using flatMap" in {
    
    def sqrt(x: Double): Option[Double] =
      if (x >= 0) Some(math.sqrt(x)) else None
    
    def increment(x: Double): Double = x + 1
      
    "Composing sqrt with itself" ! {
      sqrt(81).flatMap(sqrt) must_== Some(3)
    }
      
    "Composing the result of the above composition" ! {
      sqrt(81).flatMap(sqrt).map(increment) must_== Some(4)
    }
    
    "Option compositions can be composed as well" ! {
      sqrt(6561).flatMap(sqrt).flatMap(sqrt) must_== Some(3)
    }
  }
  
  "Composition using for yield" in {
    
    def sqrt(x: Double): Option[Double] =
      if (x >= 0) Some(math.sqrt(x)) else None
      
    /**
     * Computes the 4th root of given value.
     */
    def root4(x: Double): Option[Double] = for {
      r2 <- sqrt(x)
      r4 <- sqrt(r2)
    }
    yield r4
    
    "Sqrt can be neatly composed with itself using for yield" ! {
      root4(81) must_== Some(3)
    }
    
    def sqrtSum(a: Double, b: Double) = for {
      r <- sqrt(a)
      s <- sqrt(b)
    }
    yield r + s
      
    "All the intermediate results are in scope at yield" ! {
      (sqrtSum(9,  4) must_== Some(5)) and
      (sqrtSum(9, -1) must_== None) and
      (sqrtSum(-1, 4) must_== None)
    }
    
    def banana = for {
      x1 <- Some(1)
      x2 <- None
      x3 <- Some(3)
    }
    yield x3
    
    "None gracefully short-circuits contextual composition" ! {
      banana must_== None
    }
    
    def foo(x: Int) = Some(x+5)
      
    def bar(x: Int) = if (x > 6) Some(x-3) else None
      
    def zut(x: Option[Int]) = x.map(_*2)
      
    def quux(x: Int) = for {
      a <- foo(x)
      b <- bar(a+1)
      c <- zut(foo(b-1))
    }
    yield a + b + c
      
    "Functions can be combined freely with Option composition" ! {
      (quux(3) must_== Some(34)) and
      (quux(2) must_== Some(30)) and
      (quux(1) must_== Some(26))
    }
  }
  
  "Flattening nested Option instances" in {
    
    type Option2[A] = Option[Option[A]]
    type Option3[A] = Option[Option[Option[A]]]
    
    // Uncomment to get a compile error since there is no nesting
    // Some(1).flatten
      
    "Nested Option instances can be un-nested using flatten" ! {
      (Some(Some(2)): Option2[Int]).flatten must_== Some(2)
    }
    
    "Flatten specifically un-nests only a single layer" ! {
      (Some(Some(Some(3))): Option3[Int]).flatten must_== Some(Some(3))
    }
    
    "Flattening respects flatMap semantics" ! {
      ((Some(None): Option2[Int]).flatten must_== None) and
      ((Some(None): Option3[Int]).flatten must_== None) and
      ((Some(Some(None)): Option3[Int]).flatten must_== Some(None))
    }
  }
}
