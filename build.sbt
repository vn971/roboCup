
name := "RoboCup tournaments"

version := "0.56"

scalaVersion := "2.10.1"

organization := "ru.ya.vn91.roboTour"

description := "Automatic tournaments for the game Points"

retrieveManaged := true

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

seq(com.github.siasia.WebPlugin.webSettings :_*)

net.virtualvoid.sbt.graph.Plugin.graphSettings

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
		"net.liftmodules" %% "lift-jquery-module" % "2.5-RC2-2.2",
		"net.liftweb" %% "lift-util" % liftVersion,
		"net.liftweb" %% "lift-webkit" % liftVersion,
		"net.liftweb" %% "lift-actor" % liftVersion,
		"net.liftweb" %% "lift-common" % liftVersion
	)
}

libraryDependencies ++= Seq(
	"ch.qos.logback" % "logback-classic" % "1.0.6",
	"org.eclipse.jetty" % "jetty-webapp"        % "8.1.7.v20120910"  % "container,test",
	"org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
	"com.typesafe.akka" % "akka-actor_2.10" % "2.1.2"
)

