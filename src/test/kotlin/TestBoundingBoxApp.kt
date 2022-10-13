
import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized
import com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestBoundingBoxApp {

  val tests = mapOf(
    "test1.txt" to "(2,2)(3,3)\n",
    "test2.txt" to "(1,1)(2,2)\n",
    "test3.txt" to "",
    "test4.txt" to "(1,2)(5,6)\n",
    "test5.txt" to """
      (1,1)(1,1)
      (1,9)(1,9)
      (9,1)(9,1)
      (9,9)(9,9)
      
    """.trimIndent(),
    "test6.txt" to "(1,10)(1,13)\n",
    "test7.txt" to """
      (1,2)(1,4)
      (2,1)(4,1)
      
    """.trimIndent(),
    "test8.txt" to "(3,4)(4,5)\n"
  )

  @Test
  fun runAllTests() {

    tests.forEach {
      val file = File(this.javaClass.classLoader.getResource(it.key)!!.file)
      assertTrue(file.exists());
      val input = file.readLines().toTypedArray()

      withTextFromSystemIn(*input).execute {
        val output = tapSystemOutNormalized {
          main(emptyArray())
        }
        assertEquals(output, it.value)
      }
    }
  }


}
