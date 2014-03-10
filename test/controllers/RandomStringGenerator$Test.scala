package controllers

import org.specs2.mutable.Specification

class RandomStringGenerator$Test extends Specification {
  "apply" should {
    "contain 10 characters" in {
      RandomStringGenerator(10) must have size(10)
    }

    "contain 20 characters" in {
      RandomStringGenerator(20) must have size(20)
    }

    "contain 100 characters" in {
      RandomStringGenerator(100) must have size(100)
    }
  }
}
