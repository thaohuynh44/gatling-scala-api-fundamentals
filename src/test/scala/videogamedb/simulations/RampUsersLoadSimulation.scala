package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RampUsersLoadSimulation extends Simulation {

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

  val scn = scenario("Ramp Users Load Simulation")
    .exec(getAllVideoGames())
    .pause(duration = 5)
    .exec(getSpecificVideoGame())
    .pause(duration = 5)
    .exec(getAllVideoGames())

  setUp(
    scn.inject(
      nothingFor(5),
      constantUsersPerSec(10).during(10),
      rampUsersPerSec(1).to(2).during(20)
    )
  ).protocols(httpProtocol)

}
