import sbt._

object Dependencies {
  object Versions {
    val akka                     = "2.6.17"
    val akkaHttp                 = "10.2.4"
    val akkaProjection           = "1.1.0"
    val scalaTest                = "3.2.2"
    val mockitoScala             = "1.15.0"
    val akkaPersistenceCassandra = "1.0.1"
    val kryo                     = "1.1.5"
    val airframe                 = "20.9.0"
    val accord                   = "0.7.6"
    val slick                    = "3.3.2"
    val h2                       = "1.4.200"
    val mariadbConnectorJ        = "2.6.2"
    val logback                  = "1.2.3"
    val levelDbJni               = "1.8"
  }

  object Akka {
    val actor              = "com.typesafe.akka" %% "akka-actor-typed"            % Versions.akka
    val actorTestKit       = "com.typesafe.akka" %% "akka-actor-testkit-typed"    % Versions.akka
    val cluster            = "com.typesafe.akka" %% "akka-cluster-typed"          % Versions.akka
    val clusterSharding    = "com.typesafe.akka" %% "akka-cluster-sharding-typed" % Versions.akka
    val persistence        = "com.typesafe.akka" %% "akka-persistence-typed"      % Versions.akka
    val persistenceTestKit = "com.typesafe.akka" %% "akka-persistence-testkit"    % Versions.akka
    val persistenceQuery   = "com.typesafe.akka" %% "akka-persistence-query"      % Versions.akka
    val stream             = "com.typesafe.akka" %% "akka-stream-typed"           % Versions.akka
    val streamTestKit      = "com.typesafe.akka" %% "akka-stream-testkit"         % Versions.akka
  }

  object AkkaProjection {
    val eventsourced = "com.lightbend.akka" %% "akka-projection-eventsourced" % Versions.akkaProjection
    val slick        = "com.lightbend.akka" %% "akka-projection-slick"        % Versions.akkaProjection
  }

  object AkkaHttp {
    val http        = "com.typesafe.akka" %% "akka-http"            % Versions.akkaHttp
    val sprayJson   = "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp
    val httpTestKit = "com.typesafe.akka" %% "akka-http-testkit"    % Versions.akkaHttp
  }

  object ScalaTest {
    val wordspec       = "org.scalatest" %% "scalatest-wordspec"       % Versions.scalaTest
    val shouldmatchers = "org.scalatest" %% "scalatest-shouldmatchers" % Versions.scalaTest
  }

  object MockitoScala {
    val mockitoScala = "org.mockito" %% "mockito-scala" % Versions.mockitoScala
  }

  object AkkaPersistenceCassandra {
    val akkaPersistenceCassandra =
      "com.typesafe.akka" %% "akka-persistence-cassandra" % Versions.akkaPersistenceCassandra
  }

  object AkkaKryoSerialization {
    val akkakryoSerialization = "io.altoo" %% "akka-kryo-serialization" % Versions.kryo
  }

  object Slick {
    val slick    = "com.typesafe.slick" %% "slick"          % Versions.slick
    val codegen  = "com.typesafe.slick" %% "slick-codegen"  % Versions.slick
    val hikaricp = "com.typesafe.slick" %% "slick-hikaricp" % Versions.slick
  }

  object H2 {
    val h2 = "com.h2database" % "h2" % Versions.h2
  }

  object MariaDB {
    val connectorJ = "org.mariadb.jdbc" % "mariadb-java-client" % Versions.mariadbConnectorJ
  }

  object Airframe {
    val airframe = "org.wvlet.airframe" %% "airframe" % Versions.airframe
  }

  object Accord {
    val core = "com.wix" %% "accord-core" % Versions.accord
  }

  object Logback {
    val classic = "ch.qos.logback" % "logback-classic" % Versions.logback
  }

  object LevelDbJni {
    val all = "org.fusesource.leveldbjni" % "leveldbjni-all" % Versions.levelDbJni
  }

}
