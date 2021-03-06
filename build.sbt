name := "AudioActors"

version := "1.0"

scalaVersion := "2.10.3"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.1"

scalacOptions ++= Seq("-target:jvm-1.7", "-deprecation", "-feature", "-unchecked")

fork := true

outputStrategy := Some(StdoutOutput)

connectInput in run := true
