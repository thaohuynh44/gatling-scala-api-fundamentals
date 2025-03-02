package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CodeReuse extends Simulation {

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  def getAllVideoGames() = {
    repeat(times = 3) {
      exec(http(requestName = "Get all video games")
        .get("/videogame")
        .check(status.is(expected = 200)))
    }
  }

  def getSpecificGame() = {
    repeat(times = 5, counterName = "counter") {
      exec(http(requestName = "Get specific game")
        .get("/videogame/#{counter}")
        .check(status.in(expected = 200 to 210)))
    }
  }

  val scn = scenario(name = "Code reuse")
    .exec(getAllVideoGames())
    .pause(duration = 5)
    .exec(getSpecificGame())
    .pause(duration = 3)
    .repeat(times = 2) {
      getAllVideoGames()
    }

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)

}
