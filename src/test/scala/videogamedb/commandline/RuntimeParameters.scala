package videogamedb.commandline

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RuntimeParameters extends Simulation {

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  def USERCOUNT: Int = System.getProperty("USERS", "5").toInt
  def RAMPDURATION: Int = System.getProperty("RAMP_DURATION", "10").toInt
  def TESTDURATION: Int = System.getProperty("TEST_DURATION", "30").toInt

  before{
    println(s"Running test with ${USERCOUNT} users")
    println(s"Ramping users over ${RAMPDURATION} seconds")
    println(s"Total test duration: ${TESTDURATION} seconds")
  }

  def getAllVideoGames() = {
    exec(http(requestName = "Get all video games")
      .get("/videogame"))
      .pause(duration = 1)
  }

  val scn = scenario(name = "Runtime parameters")
    .forever{
      exec(getAllVideoGames())
    }

  setUp(
    scn.inject(
      nothingFor(5),
      rampUsers(USERCOUNT).during(RAMPDURATION)
    )
  ).protocols(httpProtocol)
    .maxDuration(TESTDURATION)
}
