package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class FixedDurationLoadSimulation extends Simulation {

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  def getAllVideoGames() = {
    exec(http(requestName = "Get all video games")
      .get("/videogame"))
  }

  def getSpecificVideoGame() = {
    exec(http(requestName = "Get specific video game")
      .get("/videogame/2"))
  }

  val scn = scenario("Fixed Duration Load Simulation")
    .forever {
        exec(getAllVideoGames())
        .pause(duration = 5)
        .exec(getSpecificVideoGame())
        .pause(duration = 5)
        .exec(getAllVideoGames())
    }

  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers(users = 10),
      rampUsers(20).during(30)
    )
  ).protocols(httpProtocol)
    .maxDuration(duration = 60)

}
