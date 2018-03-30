package com.bigterminal.example.concurrent.atomic

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

class GenericAccumulatorSpec extends FlatSpec with Matchers with TableDrivenPropertyChecks with GivenWhenThen {

  private[this] val key = "key"

  "A GenericAccumulator" should "be used for counting" in {
    val genericAccumulator = GenericAccumulator[String, Int]()
    genericAccumulator.add(key, 1)
    genericAccumulator.add(key, 2)

    val result = genericAccumulator.get(key)

    result shouldBe 3
  }

  it should "be able to subtract values" in {
    val genericAccumulator = GenericAccumulator[String, Int]()
    genericAccumulator.add(key, 10)
    genericAccumulator.subtract(key, 5)

    val result = genericAccumulator(key)

    result shouldBe 5
  }

  it should "work with multiple types of keys" in {
    val accumulator = GenericAccumulator[Any, Int]()

    case object CustomObject { val x = 10; val y = 20.5 }

    val entries =
      Table(
        (        "key", "value to add", "value to subtract"),
        (  "stringKey",             10,                   5),
        (List(1, 2, 3),              4,                   5),
        (          42L,              1,                   1),
        ( CustomObject,             -2,                  -2),
        (         None,             12,                  11),
        (         Unit,              0,                   0),
        (         null,    Int.MaxValue,       Int.MinValue)
      )

    forAll(entries) { (key: Any, valueToAdd: Int, valueToSubtract: Int) =>
      accumulator.add(key, valueToAdd)
      accumulator.subtract(key, valueToSubtract)

      accumulator(key) shouldBe valueToAdd - valueToSubtract
    }
  }

  it should "work with *any* Numeric type without losing precision" in {
    GenericAccumulator[String, BigDecimal]().get(key) shouldBe a[BigDecimal]
    GenericAccumulator[String, BigInt]()    .get(key) shouldBe a[BigInt]
    GenericAccumulator[String, Byte]()      .get(key) shouldBe a[java.lang.Byte]
    GenericAccumulator[String, Char]()      .get(key) shouldBe a[java.lang.Character]
    GenericAccumulator[String, Double]()    .get(key) shouldBe a[java.lang.Double]
    GenericAccumulator[String, Float]()     .get(key) shouldBe a[java.lang.Float]
    GenericAccumulator[String, Int]()       .get(key) shouldBe a[java.lang.Integer]
    GenericAccumulator[String, Long]()      .get(key) shouldBe a[java.lang.Long]
    GenericAccumulator[String, Short]()     .get(key) shouldBe a[java.lang.Short]

    "GenericAccumulator[String, List[Int]]().get(key)" shouldNot typeCheck
  }

  it should "take an optional default value" in {
    val accumulator = GenericAccumulator[String, Int](9000)
    accumulator.get("non existing key") shouldBe 9000
  }

  it should "be atomic and thread safe" in {
    for (nKeys <- 1 to 5) {
      info("-----")
      Given("an Empty Accumulator")
      val accumulator = GenericAccumulator[String, Int]()

      def getKey(value: Int): String = s"key${value % nKeys + 1}"

      def addAllToAccumulatorInParallel(from: Seq[Int]): Unit = {
        from.par.foreach(n => accumulator.add(getKey(n), n))
      }

      def sumOfAccumulateResults(): Int = {
        (1 to nKeys).map(i => accumulator(s"key$i")).sum
      }

      val whenCondition = s"all  numbers from 1 to 10000 are added in parallel distributed between $nKeys keys"
      When(whenCondition)
      addAllToAccumulatorInParallel(1 to 10000)

      Then("values in the accumulator should add up to the sum of consecutive numbers")
      sumOfAccumulateResults() shouldBe 50005000
    }
  }

  "A GenericAccumulator (with no default value)" should "default to zero" in {
    GenericAccumulator[String, BigDecimal]().get(key) shouldBe 0
    GenericAccumulator[String, Int]().get(key) shouldBe 0
  }

}
