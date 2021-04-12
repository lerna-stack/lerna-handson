package example

final class MainDiDesignSpec extends ActorSpecBase() {
  "MainDiDesign should resolve all dependencies" in {
    // withProductionMode を使用することで、
    // Session を作成できたら、シングルトンの依存関係はすべて解決できていることをテストできる
    val session = MainDiDesign.design(system).withProductionMode.newSession
    session.close()
  }
}
