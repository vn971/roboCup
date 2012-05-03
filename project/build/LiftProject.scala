import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
  val liftVersion = property[Version]

  // uncomment the following if you want to use the snapshot repo
  //  val scalatoolsSnapshot = ScalaToolsSnapshots

  // If you're using JRebel for Lift development, uncomment
  // this line
  // override def scanDirectories = Nil

  lazy val JavaNet = "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
  lazy val typesafe = "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

//  libraryDependencies += 

  override def libraryDependencies = Set(
  	"net.liftweb" %% "lift-webkit" % liftVersion.value.toString % "compile->default,sources",
    "net.liftweb" %% "lift-mapper" % liftVersion.value.toString % "compile->default",
    "net.liftweb" %% "lift-actor" % liftVersion.value.toString % "compile->sources",
    "net.liftweb" %% "lift-common" % liftVersion.value.toString % "compile->sources",
//    "com.typesafe.akka" % "akka-actor" % "2.0" % "compile->default,sources",
//    "com.typesafe.akka" % "akka-actor" % "2.0" % "compile->default,sources",
    "com.typesafe.akka" % "akka-actor" % "2.0" % "compile->default",
    "org.mortbay.jetty" % "jetty" % "6.1.26" % "test",
    "junit" % "junit" % "4.7" % "test",
    "ch.qos.logback" % "logback-classic" % "0.9.26",
    "org.scala-tools.testing" %% "specs" % "1.6.8" % "test",
    "com.h2database" % "h2" % "1.2.147"
  ) ++ super.libraryDependencies
}
