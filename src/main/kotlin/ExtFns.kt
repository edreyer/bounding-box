
val  <T> List<T>.head: T?
  get() = firstOrNull()

val <T> List<T>.tail: List<T>
  get() = drop(1)
