
case class Point(x:Int, y:Int, z:Int) {
  def +(p:Direction):Point = Point(x + p.x, y + p.y, z + p.z)
}

sealed abstract class Direction(val x:Int, val y:Int, val z:Int) {
  def normals:Set[Direction] =
    this match {
      case Up | Down => Set(Right, Left, Forward, Back)
      case Right | Left => Set(Up, Down, Forward, Back)
      case Forward | Back => Set(Right, Left, Up, Down)
    }
}

case object Up extends Direction(0, 0, -1)
case object Down extends Direction(0, 0, 1)
case object Right extends Direction(0, 1, 0)
case object Left extends Direction(0, -1, 0)
case object Forward extends Direction(1, 0, 0)
case object Back extends Direction(-1, 0, 0)

case class Directions(right:Direction = Right, down:Direction = Down) {
  def rotateAroundRight:Set[Directions] = for (down <- right.normals) yield Directions(right, down)
  def rotateAroundDown:Set[Directions] = for (right <- down.normals) yield Directions(right, down)
}

type Input = List[Char]
type Path = List[Point]
type Result = Seq[Int]

def isSolution(set:Set[Point]) = set.size == 27

def isInRange(p:Point) = {
  val range = 0 to 2
  (range contains p.x) && (range contains p.y) && (range contains p.z)
}

def traverse(input:Input, ds:Directions, path:Path, set:Set[Point]):Set[Path] = {
  val currentPos = path.head
  if (!isInRange(currentPos) || (set contains currentPos)) Set()
  else {
    val nextSet = set + currentPos

    def move(moveDir: Direction, directions: Set[Directions]) = {
      for {
        dir <- directions
        solution <- traverse(input.tail, dir, (currentPos + moveDir) :: path, nextSet)
      } yield solution
    }

    input match {
      case Nil => if (isSolution(nextSet)) Set(path) else Set()
      case 'R' :: 'R' :: _ => move(ds.right, Set(ds))
      case 'D' :: 'D' :: _ => move(ds.down, Set(ds))
      case 'R' :: _ => move(ds.right, ds.rotateAroundRight)
      case 'D' :: _ => move(ds.down, ds.rotateAroundDown)
      case otherChar => throw new Exception(s"Uknown characted in input $otherChar")
    }
  }
}

def toResult(solution:Path):Result = {
  val coords = 0 until 3
  for {
    x <- coords
    y <- coords
    z <- coords
  } yield solution.reverse.indexOf(Point(x, y, z))
}

def format(result:Result):String = {
  val string = result
    .map(index => f"$index%3d")
    .grouped(9)
    .map(level => level.grouped(3).map(_.mkString).mkString("\n"))
    .mkString("\n\n")
  s"Found result:\n\n$string\n"
}

// Main

val input:Input = io.Source.stdin.mkString.trim.toList

if (input.size != 26) {
  println("Error: Input string has to have exactly 26 characters.")
  System.exit(1)
}

val time:Long = System.currentTimeMillis()

// choose starting points and directions to remove symmetrically redundant solutions
val completeSymmetry = Seq((Right, Down))
val twoDirectionSymmetry = Seq((Right, Down), (Down, Right))
val startingPoints = Vector(
  (Point(0,0,0), completeSymmetry),
  (Point(0,0,1), twoDirectionSymmetry),
  (Point(0,1,1), twoDirectionSymmetry),
  (Point(1,1,1), completeSymmetry)
)

val solutions = for {
  (startPoint, directions) <- startingPoints
  (right, down) <- directions
  solution <- traverse(input, Directions(right, down), startPoint :: Nil, Set())
} yield solution

solutions.foreach(toResult _ andThen format andThen println)

println(f"Total ${solutions.size} results")
println(f"Run time ${System.currentTimeMillis()-time}ms")

