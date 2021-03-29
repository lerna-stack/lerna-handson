package jp.co.tis.lerna.testkit

import org.scalactic.TypeCheckedTripleEquals

object Equals extends Equals

trait Equals extends TypeCheckedTripleEquals {

  override def convertToEqualizer[T](left: T): Equalizer[T] = new Equalizer(left) {
    override def toString: String = Option(left).fold("null")(_.toString)
  }

  import scala.language.implicitConversions

  implicit override def convertToCheckingEqualizer[T](left: T): CheckingEqualizer[T] = new CheckingEqualizer(left) {
    override def toString: String = Option(left).fold("null")(_.toString)
  }
}
