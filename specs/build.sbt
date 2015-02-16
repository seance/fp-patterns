name := "fpp-specs"

scalaVersion := "2.11.2"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.13",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.typesafe.akka" %% "akka-remote" % "2.3.6",
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "com.typesafe.play" %% "play-json" % "2.3.6")
