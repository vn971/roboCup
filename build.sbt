import scalariform.formatter.preferences._

name := "robocup"
version := "1.1.6"
organization := "net.pointsgame"
description := "Automatic tournaments for the game Points"
maintainer := "Vasya Novikov <n1dr+robocup@yaaaandex.ru> (remove duplicating aaa)"

scalaVersion := "2.11.8"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xfuture",
	// "-Xlint:help",
	"-Xlint:adapted-args",
	"-Xlint:nullary-unit",
	"-Xlint:inaccessible",
	"-Xlint:nullary-override",
	"-Xlint:infer-any",
	"-Xlint:missing-interpolator",
	"-Xlint:doc-detached",
	"-Xlint:private-shadow",
	"-Xlint:type-parameter-shadow",
	"-Xlint:poly-implicit-overload",
	"-Xlint:option-implicit",
	"-Xlint:delayedinit-select",
	"-Xlint:by-name-right-associative",
	"-Xlint:package-object-classes",
	"-Xlint:unsound-match",
	"-Ywarn-value-discard"
)

wartremoverErrors += Wart.Any2StringAdd
wartremoverErrors += Wart.AsInstanceOf
wartremoverErrors += Wart.EitherProjectionPartial
wartremoverErrors += Wart.IsInstanceOf
wartremoverErrors += Wart.JavaConversions
wartremoverErrors += Wart.ListOps
wartremoverWarnings += Wart.NonUnitStatements
wartremoverErrors in Compile += Wart.Nothing
wartremoverErrors += Wart.Null
wartremoverErrors += Wart.OptionPartial
wartremoverErrors += Wart.Product
wartremoverErrors += Wart.Return
wartremoverErrors += Wart.Serializable
wartremoverErrors += Wart.TryPartial

defaultScalariformSettings
(test in Test) <<= (test in Test) dependsOn (ScalariformKeys.format in Compile)
ScalariformKeys.preferences := ScalariformKeys.preferences.value
	.setPreference(DoubleIndentClassDeclaration, true)
	.setPreference(IndentWithTabs, true)
	.setPreference(MultilineScaladocCommentsStartOnFirstLine, true)
	.setPreference(PreserveDanglingCloseParenthesis, true)

spray.revolver.RevolverPlugin.Revolver.settings.settings
fork in Test := true
EclipseKeys.withSource := true

assemblyJarName := "robocup.jar"

packageDescription <+= description
packageSummary <+= description
serverLoading in Debian := com.typesafe.sbt.packager.archetypes.ServerLoader.SystemV
bashScriptExtraDefines += "addJava '-Drun.mode=production'" // for liftweb
packageBin in Compile <<= (packageBin in Compile) dependsOn (test in Test)
enablePlugins(JavaServerAppPackaging)

resourceGenerators in Compile <+= (resourceManaged, baseDirectory) map { (managedBase, base) =>
	val webappBase = base / "src" / "main" / "webapp"
	for {
		(from, to) <- webappBase ** "*" pair rebase(webappBase, managedBase / "main" / "webapp")
	} yield {
		Sync.copy(from, to)
		to
	}
}

//resolvers += "Scala Tools Releases" at "http://scala-tools.org/repo-releases/"
//resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
//resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Sonatype Releases"  at "http://oss.sonatype.org/content/repositories/releases"
//resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
//resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
//resolvers += "Jetty Eclipse" at "http://repo1.maven.org/maven2/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor"   % "2.3.8"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.8" % Test

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.2.7.v20150116"

libraryDependencies += "net.liftweb" %% "lift-util"   % "2.6.2"
libraryDependencies += "net.liftweb" %% "lift-webkit" % "2.6.2"
libraryDependencies += "net.liftweb" %% "lift-actor"  % "2.6.2"
libraryDependencies += "net.liftweb" %% "lift-common" % "2.6.2"

libraryDependencies += "net.liftmodules" %% "lift-jquery-module_2.6" % "2.8"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.13"
libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.3" % Test
