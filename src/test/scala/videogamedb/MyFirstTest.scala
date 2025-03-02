package videogamedb

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MyFirstTest extends Simulation {

  // 1 Http Configuration
  var httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  // 2 Scenario Definition
  val scn = scenario(name = "My First test")
    .exec(http(requestName = "Get all games")
    .get("/videogame"))


  // 3 Load Scenario
  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)

}
