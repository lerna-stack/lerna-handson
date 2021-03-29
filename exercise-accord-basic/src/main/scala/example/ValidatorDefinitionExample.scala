package example

import com.wix.accord._
import com.wix.accord.dsl._

object ValidatorDefinitionExample extends App {

  // 例: 対象とするクラス
  // 緯度,経度 を表す
  case class Location(latitude: Double, longitude: Double)

  // バリデータ定義
  // 型は Validator[T], T がバリデーション対象となる型
  val locationValidator: Validator[Location] = validator[Location] { location =>
    // バリデーションが呼ばれたときにここが実行される
    // 複数のルールを記述することができる
    // ルール1: 緯度 は -90 ~ 90 の間
    location.latitude is between(-90.0, 90.0)
    // ルール2: 経度 は -180 ~ 180 の間
    location.longitude is between(-180.0, 180.0)
  }

}
