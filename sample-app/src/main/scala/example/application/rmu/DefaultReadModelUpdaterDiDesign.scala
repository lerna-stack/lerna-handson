package example.application.rmu

import wvlet.airframe.Design

/** ReadModelUpdater のデフォルトDIデザイン
  */
object DefaultReadModelUpdaterDiDesign {
  lazy val design: Design =
    Design.newDesign
      .bind[ConcertEventSourceFactory].to[CassandraConcertEventSourceFactory]
}
