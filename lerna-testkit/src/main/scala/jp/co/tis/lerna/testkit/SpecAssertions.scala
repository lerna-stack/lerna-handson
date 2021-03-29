package jp.co.tis.lerna.testkit

import com.eed3si9n.expecty.Expecty

trait SpecAssertions {

  /** 条件が満たされているかチェックします。
    * 条件に違反している場合は [[AssertionError]] をスローします。
    */
  val expect = new Expecty {
    override val failEarly = false
  }
}
