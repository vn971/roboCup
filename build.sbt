
name := "RoboCup tournaments"

version := "0.56"

scalaVersion := "2.10.2"

organization := "ru.ya.vn91.roboTour"

description := "Automatic tournaments for the game Points"


seq(com.github.siasia.WebPlugin.webSettings :_*)

net.virtualvoid.sbt.graph.Plugin.graphSettings


retrieveManaged := true

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

Keys.`package` <<= (Keys.`package` in Compile) dependsOn (test in Test)

port in container.Configuration := 8989

scanDirectories in Compile := Nil // using 0.2.4+ of the sbt web plugin


resolvers ++= Seq(
	"Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
	"Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
	"Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
	"Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
	"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
	"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
	"Jetty Eclipse" at "http://repo1.maven.org/maven2/"
)


libraryDependencies ++= Seq(
	"ch.qos.logback" % "logback-classic" % "1.0.13",
	"com.typesafe.akka" % "akka-actor_2.10" % "2.2.0",
	"com.typesafe.akka" % "akka-testkit_2.10" % "2.2.0" % "test",
	"net.liftmodules" %% "lift-jquery-module" % "2.5-RC4-2.3",
	"net.liftweb"     %% "lift-util"   % "2.5.1",
	"net.liftweb"     %% "lift-webkit" % "2.5.1",
	"net.liftweb"     %% "lift-actor"  % "2.5.1",
	"net.liftweb"     %% "lift-common" % "2.5.1",
	"org.eclipse.jetty" % "jetty-webapp"        % "8.1.7.v20120910"  % "container,test",
	"org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
	"org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

