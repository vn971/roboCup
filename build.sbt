
name := "roboCup"

version := "0.56"

scalaVersion := "2.9.1"

organization := "ru.ya.vn91.roboTour"

description := "Automatic tournaments for the game Points"

retrieveManaged := true

scalacOptions ++= Seq("-deprecation","-unchecked")

seq(webSettings: _*)

port in container.Configuration := 8989

// using 0.2.4+ of the sbt web plugin
scanDirectories in Compile := Nil

resolvers ++= Seq(
  "Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
	"Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
	"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
	"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
	"Jetty Eclipse" at "http://repo1.maven.org/maven2/"
)

libraryDependencies ++= {
	val liftVersion = "2.4" // Put the current/latest lift version here
	Seq(
		"net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
		"net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
		"net.liftweb" %% "lift-wizard" % liftVersion % "compile->default",
		"net.liftweb" %% "lift-actor" % liftVersion % "compile->default",
		"net.liftweb" %% "lift-common" % liftVersion % "compile->default",
		"net.liftweb" %% "lift-mongodb" % liftVersion % "compile->default"
	)
}

	// jivesoftware:smack:jar:3.1.0
	// see https://oss.sonatype.org/content/groups/jetty/org/ for Jetty versions
	// "org.eclipse.jetty" % "jetty-webapp" % "8.1.0.v20120127" % "container",
	// "org.eclipse.jetty" % "jetty-webapp" % "7.6.2.v20120308" % "container", // Does not work
	// "org.mortbay.jetty" % "jetty" % "6.1.26" % "container",
	// "com.h2database" % "h2" % "1.2.147"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "0.9.26",
	"org.eclipse.jetty" % "jetty-webapp" % "7.5.4.v20111024" % "container",
	"org.mortbay.jetty" % "jetty" % "6.1.26" % "test",
	"org.scala-tools.testing" %% "specs" % "1.6.9" % "test",
	"junit" % "junit" % "4.7" % "test",
	"com.typesafe.akka" % "akka-actor" % "2.0" % "compile->default"
)

