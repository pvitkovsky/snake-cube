import scala.collection.immutable.HashMap

case class Point(val x: Int, val y: Int, val z: Int) {
  private def byElement(p:Point, f:(Int, Int) => Int) = new Point(f(x, p.x), f(y, p.y), f(z, p.z))
  def +(p:Point) = byElement(p, _ + _)
  def -(p:Point) = byElement(p, _ - _)
  def min(p:Point) = byElement(p, math.min)
  def max(p:Point) = byElement(p, math.max)
  def toList = List(x, y, z)
}

object Point {
  val zero = Point(0, 0, 0)
  val dirRight = Point(1, 0, 0)
  val dirDown = Point(0, 1, 0)
}

val norm = List((0, 1), (1, 0), (0, -1), (-1, 0))
val xnorm = for (n <- norm) yield Point(0, n._1, n._2)
val ynorm = for (n <- norm) yield Point(n._1, 0, n._2)
val znorm = for (n <- norm) yield Point(n._1, n._2, 0)

def normals(vector: Point): List[Point] = vector match {
  case Point(x, 0, 0) if x != 0 => xnorm
  case Point(0, y, 0) if y != 0 => ynorm
  case Point(0, 0, z) if z != 0 => znorm
  case _ => throw new IllegalArgumentException("Invalid direction vector " + vector)
}

type Result = Map[Point, Int]

class State private (
    val input: List[Char],
    private val index: Int = 0,
    private val pos: Point = Point.zero,
    private val rdir: Point = Point.dirRight,
    private val ddir: Point = Point.dirDown,
    private val minPos:Point = Point.zero,
    private val maxPos:Point = Point.zero,
    private val pset:Result = HashMap(Point.zero -> 0)) {

  private def next(newPos:Point, rdir:Point, ddir:Point):Option[State] = {
    val newMinPos = minPos.min(newPos)
    val newMaxPos = maxPos.max(newPos)
    val maxDim = (newMaxPos - newMinPos).toList.map(math.abs).max
    val isValid = !pset.contains(newPos) && maxDim < 3

    val newIndex = index + 1
    val newPset = pset + (newPos -> newIndex)

    Option(isValid).collect {
      case true => new State(input.tail, newIndex, newPos, rdir, ddir, newMinPos, newMaxPos, newPset)
    }
  }

  def goRight():Iterable[State] = for (n <- next(pos + rdir, rdir, ddir)) yield n

  def goDown():Iterable[State] = for (n <- next(pos + ddir, rdir, ddir)) yield n

  def goRightAndRotate():Iterable[State] =
    for (
      d <- normals(rdir);
      n <- next(pos + rdir, rdir, d)
    ) yield n

  def goDownAndRotate():Iterable[State] =
    for (
      d <- normals(ddir);
      n <- next(pos + ddir, d, ddir)
    ) yield n

  def result:Result = {
    // normalize result to 0..2 coordinates range
    def coordMap(f:Point => Int) = (f(minPos) to f(maxPos)).zipWithIndex.toMap
    val (xs, ys, zs) = (coordMap(_.x), coordMap(_.y), coordMap(_.z))

    for ((p, i) <- pset) yield (Point(xs(p.x), ys(p.y), zs(p.z)) -> i)
  }

  def endOfInput:Boolean = input.isEmpty

}

object State {
  def apply(input:List[Char]):State = new State(input)
}

def symmetric(res: Result):Boolean = {
  val basePoints = Set(Point(0, 0, 0), Point(1, 0, 0), Point(1, 1, 0), Point(1, 1, 1))
  val firstPoint = (for ((p, 0) <- res) yield p).head
  basePoints.contains(firstPoint)
}

def traverse(state:State):Iterable[Result] = {
  if (state.endOfInput) return List(state.result).filter(symmetric)

  val nextStates = state.input match {
    case 'R' :: 'R' :: _ => state.goRight
    case 'D' :: 'D' :: _ => state.goDown
    case 'R' :: _ => state.goRightAndRotate
    case 'D' :: _ => state.goDownAndRotate
    case _ => throw new IllegalArgumentException("Wrong input character " + state.input)
  }
  nextStates.flatMap(state => traverse(state))
}

def prettyPrint(res:Result):String = {
  val indexes = 0 until 3
  val result = StringBuilder.newBuilder
  result ++= "Found result:\n"
  for (z <- indexes) {
    for (y <- indexes) {
      for (x <- indexes) {
        val index = res(Point(x, y, z))
        result ++= f"$index%3d"
      }
      result += '\n'
    }
    result += '\n'
  }
  result.mkString
}

val input:List[Char] = io.Source.stdin.mkString.trim.toList

if (input.size != 26) {
  println("Error: Input string has to have exactly 26 characters.")
  System.exit(1)
}

val time:Long = System.currentTimeMillis()
val results = traverse(State(input))
      .map(prettyPrint)
      .toList
      .distinct

println(results.mkString)
println(f"Total ${results.length} results")
println(f"Run time ${System.currentTimeMillis()-time}ms")
