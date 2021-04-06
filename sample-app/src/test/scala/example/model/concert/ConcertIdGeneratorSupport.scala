package example.model.concert

trait ConcertIdGeneratorSupport {
  private val _concertIdGenerator            = new ConcertIdGenerator()
  def concertIdGenerator: ConcertIdGenerator = _concertIdGenerator
  def newConcertId(): ConcertId              = concertIdGenerator.nextId()
}
