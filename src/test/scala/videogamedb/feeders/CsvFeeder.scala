package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeeder extends Simulation {

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  val csvFeeder = csv("data/gameCsvFile.csv").circular

  def getSpecificVideoGame() = {
    repeat(times = 10) {
      feed(csvFeeder)
      .exec(http(requestName = "Get specific video game - #{gameName}")
      .get("/videogame/#{gameId}")
      .check(jsonPath(path = "$.name").is(expected = "#{gameName}"))
      .check(status.is(expected = 200)))
        .pause(duration=1)
    }
  }

  val scn = scenario(name = "Csv Feeder test")
    .exec(getSpecificVideoGame())

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)

}
