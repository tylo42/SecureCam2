package controllers

import scala.util.Random

object RandomStringGenerator {
  private val randomGenerator = new Random()
  
  def apply(length: Int): String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
    randomStringFromCharList(length, chars)
  }

  private def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
    (for (i <- 1 to length)
      yield chars(randomGenerator.nextInt(chars.length))
    ).mkString
  }
}
