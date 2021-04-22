package example.entrypoint

import example.ActorSpecBase

final class MainDIDesignSpec extends ActorSpecBase() {
  "MainDiDesign should resolve all dependencies" in {
    // withProductionMode を使用することで、
    // Session を作成できたら、シングルトンの依存関係はすべて解決できていることをテストできる
    val session = MainDIDesign.design(system).withProductionMode.newSession
    session.close()
  }
}
