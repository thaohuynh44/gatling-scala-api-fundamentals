package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseBodyAndExtract extends Simulation{

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  val scn = scenario(name = "Check with JSON path")

    .exec(http(requestName = "Get specific game")
    .get("/videogame/3")
    .check(jsonPath(path = "$.name").is(expected = "Tetris")))

    .exec(http(requestName = "Get all video games")
    .get("/videogame")
    .check(jsonPath(path = "$[1].id").saveAs(key = "gameId")))
    .exec{session => println(session); session}

    .exec(http(requestName = "Get specifc game")
    .get("/videogame/#{gameId}")
    .check(jsonPath(path = "$.name").is(expected = "Gran Turismo 3"))
    .check(bodyString.saveAs(key = "responseBody")))
    .exec{session => println(session("responseBody").as[String]); session}

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtocol)

}
