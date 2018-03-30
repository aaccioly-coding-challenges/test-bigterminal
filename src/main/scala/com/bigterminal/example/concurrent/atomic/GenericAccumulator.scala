package com.bigterminal.example.concurrent.atomic

import scala.concurrent.stm.{TMap, atomic}

object GenericAccumulator {

  def apply[K, V: Numeric](defaultValue: V): GenericAccumulator[K, V] = new GenericAccumulator[K, V](defaultValue)

  def apply[K, V: Numeric](): GenericAccumulator[K, V] = new GenericAccumulator[K, V]()

}

class GenericAccumulator[-K, V: Numeric](defaultValue: V) {

  import Numeric.Implicits.infixNumericOps

  private[this] val entries = TMap[K, V]().single.withDefaultValue(defaultValue)

  def this() = this(implicitly[Numeric[V]].zero)

  def add(key: K, value: V): Unit = atomic { implicit txn => entries(key) += value }

  def subtract(key: K, value: V): Unit = atomic { implicit txn => entries(key) -= value }

  def get(key: K): V = entries(key)

  def apply(key: K): V = entries(key)

}
