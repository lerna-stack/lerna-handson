package example

import com.typesafe.config.ConfigFactory

final class MainDiDesignSpec extends ActorSpecBase(ConfigFactory.load("test-akka-cluster")) {
  "MainDiDesign should resolve all dependencies" in {
    // withProductionMode を使用することで、
    // Session を作成できたら、シングルトンの依存関係はすべて解決できていることをテストできる
    val session = MainDiDesign.design(system).withProductionMode.newSession
    session.close()
  }
}
