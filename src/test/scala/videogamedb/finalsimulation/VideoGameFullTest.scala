package videogamedb.finalsimulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class VideoGameFullTest extends Simulation {

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  /*** VARIABLE FOR FEEDERS ***/
  // runtime variables

  def USERCOUNT: Int = System.getProperty("USERS", "5").toInt
  def RAMPDURATION: Int = System.getProperty("RAMP_DURATION", "10").toInt
  def TESTDURATION: Int = System.getProperty("TEST_DURATION", "30").toInt

  /*** CUSTOM FEEDERS ***/

  val csvFeeder = csv("data/gameCsvFile.csv").random

  before{
    println(s"Running our test with ${USERCOUNT} users")
    println(s"Ramping users over ${RAMPDURATION} seconds")
    println(s"Total test duration: ${TESTDURATION} seconds")
  }

  /*** HTTP CALLS ***/
  def getAllVideoGames() = {
    exec(http(requestName = "Get all video games")
    .get("/videogame")
    .check(status.is(expected = 200)))
  }

  def authenticate() = {
    exec(http(requestName = "Authenticate")
    .post("/authenticate")
    .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
    .check(jsonPath(path = "$.token").saveAs("jwtToken")))
  }

  def createNewGame() = {
    feed(csvFeeder)
      .exec(http(requestName = "Create new game - #{name}")
        .post("/videogame")
        .header("Authorization", "Bearer #{jwtToken}")
      .body(ElFileBody("bodies/newGameTemplate.json")).asJson)
  }

  def getSingleGame() = {
    exec(http(requestName = "Get single name - #{name}")
    .get("/videogame/#{gameId}")
    .check(jsonPath("$.name").is(expected = "#{name}")))
  }

  def deleteGame() = {
    exec(http(requestName = "Delete game - #{name}")
    .delete("/videogame/#{gameId}")
    .header("Authorization", "Bearer #{jwtToken}")
    .check(bodyString.is(expected = "Video game deleted")))
  }
  /*** SCENARIO DESIGN ***/

  // using the http calls, create the scenario that does the following:
  // 1. Get all games
  // 2. Create a new game (remember to authenticate first!)
  // 3. Get details of single game
  // 4. Delete game

  val scn = scenario(name = "Video Game DB Final Script")
    .forever{
      exec(getAllVideoGames())
        .pause(2)
        .exec(authenticate())
        .pause(2)
        .exec(createNewGame())
        .pause(2)
        .exec(getSingleGame())
        .pause(2)
        .exec(deleteGame())
    }

  /*** SETUP LOAD SIMULATIONS ***/

  // create a simulation that has runtime parameters
  // 1. Users
  // 2. Ramp up time
  // 3. Test duration

  setUp(
    scn.inject(
      nothingFor(5),
      rampUsers(USERCOUNT).during(RAMPDURATION)
    )
  ).protocols(httpProtocol).maxDuration(TESTDURATION)
    .assertions(
      global.responseTime.max.lt(2),
      global.successfulRequests.percent.gt(99)
    )

  after(
    print("Stress test completed")
  )

}
