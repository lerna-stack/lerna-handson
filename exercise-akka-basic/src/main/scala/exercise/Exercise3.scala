package exercise

import akka.actor._

/*
状態を持つアクターを実装してみよう。
- アクターの名前は MyCounterActor とする
- 任意のInt型数値を受け取り、その数値分カウントアップする
  (負数を受け取った場合はカウントダウンになる)
- メッセージは Int型 のみを処理する
- オーバーフローは気にせずに
- カウンタの初期値は0とする
- 処理したらすぐに更新後のカウンタ値を返送する
- テストしてみよう(別ファイル)
- (任意) Int型以外を受け取ったらカウンタを0にリセットしてみよう
 */

// このクラスを実装しよう
final class MyCounterActor extends Actor {
  override def receive: Receive = ???
}
