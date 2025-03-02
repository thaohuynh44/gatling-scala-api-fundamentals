Gatling fundamentals for API Testing - Scala
=============================================

### Commandline: 
mvn gatling:test -Dgatling.simulationClass=videogamedb.commandline.RuntimeParameters
### Running commandline with runtime params:
mvn gatling:test -Dgatling.simulationClass=videogamedb.commandline.RuntimeParameters -DUSERS=10 -DRAMP_DURATION=20 -DTEST_DURATION-30