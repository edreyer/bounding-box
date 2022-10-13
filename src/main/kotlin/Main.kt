/**
 * Main entry point of the app
 */
fun main(args: Array<String>) {

  val boxes = initializeState()
    .let { findBoundingBoxes(it) }
    .let { processOverlaps(it) }
    .let { findLargestBoxes(it.bbs) }

  boxes.forEach { println(it) }
}

/**
 * Reads in each line from StdIn and processes them one by one
 * until an empty line is encountered
 */
private fun initializeState(): State = generateSequence(::readln)
  .takeWhile { it != "" }
  .foldIndexed(State()) { i, stateAccum, line ->
    processInputLine(
      stateAccum.copy(
        currentLine = line,
        currentLineNum = i+1 // i is zero-based, so add 1
      )
    )
  }

/**
 * For each found '*', create a Star instance, and add to State in a set of
 * unprocessed Stars, for processing later
 */
fun processInputLine(state: State): State {
  val stars: Set<Star> = state.currentLine.foldIndexed<Set<Star>>(setOf()) { i, set, char ->
    if (char == '-') { set }
    else {
      set + Star(state.currentLineNum, i+1) // i is zero-based, so add 1
    }
  }.toMutableSet()
  state.unprocessedStars.addAll(stars)
  return state
}

/**
 *
 */
fun findBoundingBoxes(state: State): State {
  // 1) Grab the first unprocessed Star, and process it
  var newState = state
  while (newState.unprocessedStars.size > 0) {
    newState = newState.unprocessedStars.first().let {
      processPoint(it, state)
    }
  }
  return newState
}

/**
 * A new Star! Create a new Bounding Box and
 * pass to recursive algorithm to find all connected Stars
 */
fun processPoint(star: Star, state: State): State {
  val box = BoundingBox(topLeft = star, bottomRight = star)
  return processPointRec(star, box, state).apply {
    bbs.add(box)
  }
}

/**
 * Recursively finds all neighbor Stars,
 * updating the bounds of our 'box' as we go,
 * and removing the processed Star from State
 */
fun processPointRec(star: Star, box: BoundingBox, state: State): State {
  if (!state.unprocessedStars.contains(star)) {
    return state
  }

  state.unprocessedStars.remove(star)
  box.modifyBounds(star)

  // navigate graph
  val right = Star(star.line, star.col + 1)
  val left = Star(star.line, star.col - 1)
  val top = Star(star.line - 1, star.col)
  val bottom = Star(star.line + 1, star.col)

  var newState = processPointRec(right, box, state)
  newState = processPointRec(bottom, box, newState)
  newState = processPointRec(left, box, newState)
  newState = processPointRec(top, box, newState)
  return newState
}

/**
 * Given all the boxes,
 * iterate through them, while removing the overlapping boxes as we go
 */
fun processOverlaps(state: State): State {
  var head = state.bbs.head
  var tail = state.bbs.tail
  while (head != null && tail.isNotEmpty()) {
    val overlaps = tail.filter { it.overlaps(head!!) }
    state.bbs.removeAll(overlaps)
    if (overlaps.isNotEmpty()) {
      state.bbs.remove(head)
      head = state.bbs.head
      tail = state.bbs.tail
    } else {
      head = tail.head
      tail = tail.tail
    }
  }
  return state
}

/**
 * Given a list of boxes, find the largest.
 * Largest = all boxes that share the same largest area.
 */
fun findLargestBoxes(bbs: List<BoundingBox>): List<BoundingBox> {
  val sorted = bbs.sortedByDescending { it.area }
  return when {
    sorted.isEmpty() || sorted.size == 1 -> sorted
    else -> sorted.filter { it.area == sorted[0].area }
  }
}

/**
 * State object used keep track of
 */
data class State(
  // Helps keep track of which asterisks (stars) we've already seen/procesed
  val unprocessedStars: MutableSet<Star> = mutableSetOf(),
  // The final set of bounding boxes
  var bbs: MutableList<BoundingBox> = mutableListOf(),

  // These are used during input processing
  val currentLineNum: Int = 0,
  val currentLine: String = "",
)

data class Star(val line: Int, val col: Int) {
  override fun toString() = "($line,$col)"
}

data class BoundingBox(
  var topLeft: Star,
  var bottomRight: Star,
  var overlapping: Boolean = false) {

  init {
    require(topLeft.line <= bottomRight.line)
    require(topLeft.col <= bottomRight.col)
  }

  fun modifyBounds(star: Star) {
    topLeft = Star(
      line = if (topLeft.line > star.line) { star.line } else { topLeft.line },
      col = if (topLeft.col > star.col) { star.col } else { topLeft.col },
    )
    bottomRight = Star(
      line = if (bottomRight.line < star.line) { star.line } else { bottomRight.line },
      col = if (bottomRight.col < star.col) { star.col } else { bottomRight.col },
    )
  }

  fun overlaps(other: BoundingBox): Boolean =
    edgeOverlaps(this.topLeft.line, this.bottomRight.line, other.topLeft.line, other.bottomRight.line)
      && edgeOverlaps(this.topLeft.col, this.bottomRight.col, other.topLeft.col, other.bottomRight.col)

  private fun edgeOverlaps(b1Min: Int, b1Max: Int, b2Min: Int, b2Max: Int) =
    (b1Max > b2Min && b2Max > b1Min) // edges overlap
      || (b1Min < b2Min && b1Max > b2Max) // b1 swallows b2
      || (b1Min > b2Min && b1Max < b2Max) // b2 swallows b1

  override fun toString() = "$topLeft$bottomRight"

  val area
    get() = (1 + bottomRight.col - topLeft.col) * (1 + bottomRight.line - topLeft.line)
}


