package jp.co.tis.lerna.testkit

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

trait StandardSpec extends AnyWordSpecLike with SpecAssertions with Equals with Matchers
