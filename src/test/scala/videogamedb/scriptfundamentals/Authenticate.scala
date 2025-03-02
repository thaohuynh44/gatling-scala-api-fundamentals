package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class Authenticate extends Simulation {

  val httpProtol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  def authenticate() = {
    exec(http(requestName = "Authenticate")
    .post("/authenticate")
    .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
    .check(jsonPath(path = "$.token").saveAs(key = "jwtToken")))
  }

  def createNewGame() = {
    exec(http(requestName = "Create new game")
    .post("/videogame")
      .header(name = "Authorization", value="Bearer #{jwtToken}")
    .body(StringBody("{\n  \"category\": \"Platform\",\n  \"name\": \"Mario\",\n  \"rating\": \"Mature\",\n  \"releaseDate\": \"2012-05-04\",\n  \"reviewScore\": 85\n}"
    )))
  }

  val scn = scenario(name = "Authenticate")
    .exec(authenticate())
    .exec(createNewGame())

  setUp(
    scn.inject(atOnceUsers(users = 1))
  ).protocols(httpProtol)

}
