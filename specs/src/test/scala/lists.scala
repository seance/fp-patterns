import fpp.specs._

class Lists extends Meditation {
  
  "List basics" in {
    
    "Lists can be constructed using Scala's List constructor" ! {
      __[List[Int]] must not be empty
    }
    
    "Seq also works but gives static type Seq with List implementation" ! {
      __[Seq[Int]] must have length(3)
    }
    
    "Bounded ranges can be constructed using to" ! {
      (1 to 6).toList must_== __
    }
    
    "And using until when we want a range with exclusive end" ! {
      (1 until 6).toList must_== __
    }
      
    "List.range works similarly as until" ! {
      List.range(1, 3) must_== __
    }
    
    "Bounded ranges can also vary by different steps" ! {
      (1.0 to 3.0 by 0.5).toList must_== __
    }
  }
  
  "Reducing, folding and introspection" in {
    
    val xs1: List[Int] = List.range(1, 4)
    val xs2: List[Int] = List()
    
    "Head is the first element of a list" ! {
      xs1.head must_== __
    }
    
    "Tail is the list without head" ! {
      xs1.tail must_== __
    }
    
    "Appending to an immutable list via :+ produces a new list" ! {
      (xs1 :+ 4) must_== __
    }
    
    "Prepending can be done using +: which also produces a new list" ! {
      (0 +: xs1) must_== __
    }
    
    "We can query if a list contains an element via contains" ! {
      (xs1.contains(2) must_== __) and
      (xs1.contains(4) must_== __) and
      (xs2.contains(1) must_== __)
    }
    
    "Reduce always produces a value of same type as the list elements" ! {
      xs1.reduce(_+_) must_== __
    }
    
    "Reduce can only be used on non-empty lists" ! {
      xs2.reduce(_*_) must throwA[__]
    }
    
    "Folding can change the result type and works on empty lists as well" ! {
      (xs1.foldLeft("0") { case (acc, x) => acc + x.toString } must_== __) and
      (xs2.foldLeft(List[String]()) { case (acc, x) => List("Hi!") } must_== __)
    }
  }
  
  "Composition using map" in {
    
    val names = List("Benjamin", "Samuel")
    val nickNames = Map("Benjamin" -> "Ben", "Samuel" -> "Sam")
    def nameLength(name: String) = name.length
    
    "Mapping over a list transforms it element-wise" ! {
      names.map(name => nickNames(name)) must_== __
    }
    
    "We can chain compositions as before" ! {
      names.map(nickNames).map(nameLength) must_== __
    }
  }
  
  /**
   * The chess board, white at the bottom. Ranks 1-8, files A-H.
   * 
   * K = King
   * Q = Queen
   * R = Rook
   * B = Bishop
   * N = Knight
   * P = Pawn
   * 
   *   +---+---+---+---+---+---+---+---+
   * 8 | R | N | B | Q | K | B | N | R |
   *   +---+---+---+---+---+---+---+---+
   * 7 | P | P | P | P | P | P | P | P |
   *   +---+---+---+---+---+---+---+---+
   * 6 |   |   |   |   |   |   |   |   |
   *   +---+---+---+---+---+---+---+---+
   * 5 |   |   |   |   |   |   |   |   |
   *   +---+---+---+---+---+---+---+---+
   * 4 |   |   |   |d4 |   |   |   |   |
   *   +---+---+---+---+---+---+---+---+
   * 3 |   |   |   |   |e3 |   |   |   |
   *   +---+---+---+---+---+---+---+---+
   * 2 | P | P | P | P | P | P | P | P |
   *   +---+---+---+---+---+---+---+---+
   * 1 | R | N | B | Q | K | B | N | R |
   *   +---+---+---+---+---+---+---+---+
   *     A   B   C   D   E   F   G   H
   */
  object Chess {
    
    /**
     * Computes the chess board positions a Knight can move to from its current
     * position, given as list of integer tuples (column, row) with zero based
     * indexing. Assumes an empty board.
     * 
     * Simulates nondeterminism by a backtracking search.
     */
    def knightStep: ((Int, Int)) => List[(Int, Int)] = { case (col, row) =>
      
      val deltaStep = List(-2, -1, 1, 2)
      val isOnBoard = List.range(0, 8).contains(_: Int)
      
      def validMove(deltaCol: Int, deltaRow: Int) =
        math.abs(deltaCol) != math.abs(deltaRow) &&
        isOnBoard(col + deltaCol) &&
        isOnBoard(row + deltaRow)
      
      for {
        deltaCol <- deltaStep
        deltaRow <- deltaStep if validMove(deltaCol, deltaRow)
      } 
      yield (col + deltaCol, row + deltaRow)
    }
    
    object Position {
      val ColRow = """([a-hA-H])([1-8])""".r
        
      def parse: String => (Int, Int) = { case ColRow(c, r) =>
        (c.toLowerCase.toList.head - 'a', r.toInt - 1)
      }
      
      def print: ((Int, Int)) => String = { case (c, r) =>
        s"${(c + 'a').toChar}${r + 1}"
      }
    }
    
    /**
     * Implicitly converts strings of the suitable format to (col, row) integer
     * tuples.
     */
    implicit def parsePosition(pos: String) = Position.parse(pos)
  }
  
  "Composition using flatMap" in {
      
    "Constructing a plain sequence with flatMap" ! {
      List(1, 2, 3).__ must_== List(1, 1, 2, 1, 2, 3)
    }
    
    import Chess._
    
    /**
     * Computes whether a Knight can reach the end position within two moves
     * from the start position. Use knightStep and flatMap for the
     * implementation.
     */
    def canKnightReachIn2(start: (Int, Int), end: (Int, Int)): Boolean = __
    
    "The White King's Knight can reach d4 in two moves" ! {
      canKnightReachIn2("g1", "d4") must beTrue
    }
      
    "But the White Queen's Knight cannot" ! {
      canKnightReachIn2("b1", "d4") must beFalse
    } 
  }
  
  "Composition using for yield" in {
    
    import Chess._
    
    /**
     * Computes whether a Knight can reach the end position within three moves
     * from the start position. Use knightStep and for yield for the
     * implementation.
     */
    def canKnightReachIn3(start: (Int, Int), end: (Int, Int)): Boolean = __
    
    "The White Queen's Knight can reach e3 in three moves" ! {
      canKnightReachIn3("b1", "e3") must beTrue
    }
      
    "But the White King's Knight cannot" ! {
      canKnightReachIn3("g1", "e3") must beFalse
    }
    
    def banana = for {
      x1 <- List(1)
      x2 <- List()
      x3 <- List(3)
    }
    yield x3
    
    "Nondeterministically, the empty list represents a dead end" ! {
      banana must_== __
    }
  }
  
  "Flattening nested Lists" in {
    
    type List2[A] = List[List[A]]
    type List3[A] = List[List[List[A]]]
    
    val xs1: List2[Int] = List(List(1, 2), List(3))
    val xs2: List3[Int] = List(List(List(1)), List(List(2)))
    val xs3: List3[Int] = List(List())
    val xs4: List3[Int] = List()
    
    "Nested lists can be un-nested using flatten" ! {
      xs1.flatten must_== __
    }
    
    "Flattening un-nests specifically only one layer" ! {
      xs2.flatten must_== __
    }
    
    "Flattening respects flatMap semantics" ! {
      (xs3.flatten must_== __) and
      (xs4.flatten must_== __)
    }
  }
}
