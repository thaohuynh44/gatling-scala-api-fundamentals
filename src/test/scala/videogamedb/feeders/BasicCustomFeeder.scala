package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicCustomFeeder extends Simulation{

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  val idNumbers = (1 to 10).iterator

  val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getSpecificVideoGame() = {
    repeat(times = 10) {
        feed(customFeeder)
          .exec(http(requestName = "Get video game with id - #{gameId}")
            .get("/videogame/#{gameId}")
            .check(status.is(expected = 200)))
          .pause(duration = 1)
      }
    }

  val scn = scenario(name = "Basic Custom Feeder")
    .exec(getSpecificVideoGame())

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)
}
