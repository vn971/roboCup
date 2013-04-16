
name := "RoboCup tournaments"

version := "0.56"

scalaVersion := "2.10.0"

organization := "ru.ya.vn91.roboTour"

description := "Automatic tournaments for the game Points"

retrieveManaged := true

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

seq(com.github.siasia.WebPlugin.webSettings :_*)

Keys.`package` <<= (Keys.`package` in Compile) dependsOn (test in Test)

port in container.Configuration := 8989

// using 0.2.4+ of the sbt web plugin
scanDirectories in Compile := Nil


resolvers ++= Seq(
	"Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
	"Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
	"Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
	"Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
	"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
	"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
	"Jetty Eclipse" at "http://repo1.maven.org/maven2/"
)

libraryDependencies ++= {
	val liftVersion = "2.5-RC2"
	Seq(
		"net.liftweb" %% "lift-util" % liftVersion,
		"net.liftweb" %% "lift-webkit" % liftVersion,
		"net.liftweb" %% "lift-actor" % liftVersion,
		"net.liftweb" %% "lift-common" % liftVersion,
		"net.liftmodules" %% "lift-jquery-module" % "2.5-RC2-2.2"
	)
}

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "0.9.26",
	"org.eclipse.jetty" % "jetty-webapp" % "7.5.4.v20111024" % "container",
	"org.mortbay.jetty" % "jetty" % "6.1.26" % "test",
	"org.scala-tools.testing" %% "specs" % "1.6.9" % "test",
	"junit" % "junit" % "4.7" % "test",
	"com.typesafe.akka" % "akka-actor_2.10" % "2.1.2"
)

