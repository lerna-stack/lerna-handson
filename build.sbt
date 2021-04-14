ThisBuild / organization := "com.lerna-stack"
ThisBuild / organizationName := "Lerna Project"
ThisBuild / organizationHomepage := Some(url("https://lerna-stack.github.io/"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/lerna-stack/lerna-handson"),
    "scm:git@github.com:lerna-stack/lerna-handson.git",
  ),
)
ThisBuild / developers := List(
  Developer(
    id = "lerna",
    name = "Lerna Team",
    email = "go-reactive@tis.co.jp",
    url = url("https://lerna-stack.github.io/"),
  ),
)
ThisBuild / description := "A hands on materials to lern the Lerna Stack"
ThisBuild / version := "1.0.0"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/lerna-stack/lerna-handson"))

// このビルドで使う scala version
// サブプロジェクトで上書きしない限り適用される。
ThisBuild / scalaVersion := "2.13.3"
ThisBuild / scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-Wconf:any:warning",
  "-deprecation",
  "-feature",
  "-Xlint",
) ++ sys.env.get("lerna.enable.discipline").filter(_ == "true").map(_ => "-Xfatal-warnings").toSeq

// See: https://www.scalatest.org/user_guide/using_the_runner
ThisBuild / Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oT")

lazy val ExerciseScalaBasic = (project in file("exercise-scala-basic"))
  .settings(
    name := "exercise-scala-basic",
    libraryDependencies ++= Seq(
      Dependencies.ScalaTest.wordspec       % Test,
      Dependencies.ScalaTest.shouldmatchers % Test,
    ),
  )

lazy val ExerciseAkkaBasic = (project in file("exercise-akka-basic"))
  .settings(
    name := "exercise-akka-basic",
    libraryDependencies ++= Seq(
      Dependencies.ScalaTest.wordspec       % Test,
      Dependencies.ScalaTest.shouldmatchers % Test,
      Dependencies.Akka.actor,
      Dependencies.Akka.actorTestKit % Test,
    ),
  )

lazy val ExerciseAkkaHttpBasic = (project in file("exercise-akka-http-basic"))
  .settings(
    name := "exercise-akka-http-basic",
    scalacOptions ++= Seq(
      // 演習問題はunsedな宣言を事前にしているため警告をださない
      "-Wconf:cat=unused&src=exercise/.*\\.scala:silent",
    ),
    libraryDependencies ++= Seq(
      Dependencies.Akka.stream,
      Dependencies.AkkaHttp.http,
      Dependencies.AkkaHttp.sprayJson,
      Dependencies.AkkaHttp.httpTestKit % Test,
    ),
  )

lazy val ExerciseAccordBasic = (project in file("exercise-accord-basic"))
  .settings(
    name := "exercise-accord-basic",
    libraryDependencies ++= Seq(
      Dependencies.Accord.core,
    ),
  )

lazy val ExerciseSlickBasic = (project in file("exercise-slick-basic"))
  .settings(
    name := "exercise-slick-basic",
    libraryDependencies ++= Seq(
      Dependencies.Slick.slick,
      Dependencies.Slick.hikaricp,
      Dependencies.H2.h2,
    ),
  )

lazy val SampleApp = (project in file("sample-app"))
  .settings(
    name := "sample-app",
    scalacOptions ++= Seq(
      // 演習問題はunsedな宣言を事前にしているため警告をださない
      "-Wconf:cat=unused&src=(MyConcertActor|MyBoxOfficeService|MyBoxOfficeResource)\\.scala:silent",
    ),
    libraryDependencies ++= Seq(
      Dependencies.ScalaTest.wordspec       % Test,
      Dependencies.ScalaTest.shouldmatchers % Test,
      Dependencies.Akka.actor,
      Dependencies.Akka.actorTestKit % Test,
      Dependencies.Akka.cluster,
      Dependencies.Akka.clusterSharding,
      Dependencies.Akka.persistence,
      Dependencies.Akka.persistenceTestKit % Test,
      Dependencies.Akka.persistenceQuery,
      Dependencies.Akka.stream,
      Dependencies.Akka.streamTestKit % Test,
      Dependencies.AkkaProjection.eventsourced,
      Dependencies.AkkaProjection.slick,
      Dependencies.AkkaHttp.http,
      Dependencies.AkkaHttp.sprayJson,
      Dependencies.AkkaHttp.httpTestKit % Test,
      Dependencies.AkkaPersistenceCassandra.akkaPersistenceCassandra,
      Dependencies.AkkaKryoSerialization.akkakryoSerialization,
      Dependencies.Slick.slick,
      Dependencies.Slick.hikaricp,
      Dependencies.Slick.codegen,
      Dependencies.MariaDB.connectorJ,
      Dependencies.H2.h2,
      Dependencies.Airframe.airframe,
      Dependencies.Accord.core,
      Dependencies.Logback.classic,
      Dependencies.MockitoScala.mockitoScala % Test,
    ),
  )

// すべてのテストを実行する
// ただし演習向けのテストは除く
addCommandAlias(
  "testAll",
  """
    |compile;
    |ExerciseAccordBasic/test;
    |ExerciseAkkaBasic/test;
    |ExerciseAkkaHttpBasic/test;
    |ExerciseScalaBasic/test;
    |ExerciseSlickBasic/test;
    |SampleApp/testOnly -- -l example.testing.tags.ExerciseTest;
    |""".stripMargin,
)

// 演習で使うコマンドたち
addCommandAlias(
  "testMyConcertActorBinding",
  "SampleApp/testOnly example.model.MyConcertActorBindSpec",
)
addCommandAlias(
  "testMyConcertActor",
  "SampleApp/testOnly example.model.concert.actor.MyConcertActorSpec",
)
addCommandAlias(
  "testMyBoxOfficeServiceBinding",
  "SampleApp/testOnly example.model.MyBoxOfficeServiceBindSpec",
)
addCommandAlias(
  "testMyBoxOfficeService",
  "SampleApp/testOnly example.model.concert.service.MyBoxOfficeServiceSpec",
)
addCommandAlias(
  "testMyBoxOfficeResourceBinding",
  "SampleApp/testOnly example.application.http.MyBoxOfficeResourceBindSpec",
)
addCommandAlias(
  "testMyBoxOfficeResource",
  "SampleApp/testOnly example.application.http.controller.MyBoxOfficeResourceSpec",
)
