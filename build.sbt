import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys
import com.earldouglas.xsbtwebplugin.WebPlugin._
import com.earldouglas.xsbtwebplugin._

name := "robocup"
version := "0.115"
organization := "net.pointsgame"
description := "Automatic tournaments for the game Points"

scalaVersion := "2.10.4"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xfatal-warnings")


com.earldouglas.xsbtwebplugin.WebPlugin.webSettings
PluginKeys.port in container.Configuration := 8989

fork := true
Keys.`package` <<= (Keys.`package` in Compile) dependsOn (test in Test)

EclipseKeys.withSource := true

resolvers ++= Seq(
	"Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
	"Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
	"Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
	"Sonatype Releases"  at "http://oss.sonatype.org/content/repositories/releases",
	"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
	"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
	"Jetty Eclipse" at "http://repo1.maven.org/maven2/"
)

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.13"
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.2.0"
libraryDependencies += "com.typesafe.akka" % "akka-testkit_2.10" % "2.2.0" % Test
libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"
libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.1.0.v20131115" % "container"
libraryDependencies += "org.eclipse.jetty" % "jetty-plus"   % "9.1.0.v20131115" % "container"
libraryDependencies += "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar")
libraryDependencies += "org.scalatest" %% "scalatest" % "2.0" % Test

libraryDependencies += "net.liftweb" %% "lift-util"   % "2.5.1"
libraryDependencies += "net.liftweb" %% "lift-webkit" % "2.5.1"
libraryDependencies += "net.liftweb" %% "lift-actor"  % "2.5.1"
libraryDependencies += "net.liftweb" %% "lift-common" % "2.5.1"

libraryDependencies += "net.liftmodules" %% "lift-jquery-module_2.5" % "2.6"
