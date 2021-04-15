package example.adapter

import com.wix.accord._
import com.wix.accord.dsl._
import example.adapter.ConcertError.IllegalConcertIdError

import scala.annotation.nowarn

/** コンサートID
  * シリアライズされる。
  */
final case class ConcertId private (value: String) extends AnyVal

object ConcertId {

  /** コンサートIDのバリデータ
    * - 空ではないこと
    * - 英数字,アンダースコアのみで構成されること
    * - 先頭文字は英字であること
    */
  protected[adapter] val valueValidator: Validator[String] = validator { rawValue =>
    rawValue is notBlank
    rawValue is matchRegexFully("[a-zA-Z][a-zA-Z0-9_]*")
  }

  /** ConcertId を作成する。
    * バリデーション成功の場合に Right[ConcertId], 失敗は Left[ConcertError] になる。
    */
  def fromString(rawValue: String): Either[ConcertError, ConcertId] = {
    validate(rawValue)(valueValidator) match {
      case Success =>
        Right(new ConcertId(rawValue))
      case Failure(violations) =>
        Left(IllegalConcertIdError(s"Invalid Concert ID."))
    }
  }

  // インスタンスの生成は fromString から行うこと
  // デフォルトで生成される apply を不可視にする
  // copy は防げない。
  // https://qiita.com/petitviolet/items/b6af2877f64ebe8fe312#comment-08e2ffe396177bf5a252
  @nowarn("cat=unused-privates")
  private def apply(value: String): ConcertId = new ConcertId(value)
}
