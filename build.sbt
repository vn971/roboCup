name := "robocup"
version := "1.1.6"
organization := "net.pointsgame"
description := "Automatic tournaments for the game Points"
maintainer := "Vasya Novikov <n1dr+robocup@yaaaandex.ru> (replace aaaa with one a)"

scalaVersion := "2.12.6"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xfuture",
	// "-Xlint:help",
	"-Xlint:adapted-args",
	"-Xlint:doc-detached",
	"-Xlint:inaccessible",
	"-Xlint:infer-any",
	"-Xlint:nullary-override",
	"-Xlint:nullary-unit",
	"-Xlint:missing-interpolator",
	"-Xlint:private-shadow",
	"-Xlint:poly-implicit-overload",
	"-Xlint:type-parameter-shadow",
	"-Xlint:option-implicit",
	"-Xlint:delayedinit-select",
	"-Xlint:by-name-right-associative",
	"-Xlint:package-object-classes",
	"-Xlint:unsound-match",
	"-Ywarn-unused",
	"-Ywarn-unused-import",
	"-Ywarn-dead-code",
	"-Ywarn-value-discard"
)

wartremoverErrors += Wart.AsInstanceOf
wartremoverErrors += Wart.EitherProjectionPartial
wartremoverErrors += Wart.IsInstanceOf
wartremoverErrors += Wart.JavaConversions
wartremoverWarnings += Wart.NonUnitStatements
wartremoverErrors in Compile += Wart.Nothing
wartremoverErrors += Wart.Null
wartremoverErrors += Wart.OptionPartial
wartremoverErrors += Wart.Product
wartremoverErrors += Wart.Return
wartremoverErrors += Wart.Serializable
wartremoverErrors += Wart.TryPartial

Revolver.settings.settings
fork in Test := true

assemblyJarName := "robocup.jar"

packageDescription := description.value
packageSummary := description.value
serverLoading in Debian := Some(com.typesafe.sbt.packager.archetypes.systemloader.ServerLoader.SystemV)
bashScriptExtraDefines += "addJava '-Drun.mode=production'" // for liftweb
packageBin in Compile := (packageBin in Compile).dependsOn(test in Test).value
enablePlugins(JavaServerAppPackaging)

resourceGenerators in Compile += task {
	val webappBase = sourceDirectory.value / "main" / "webapp"
	val managedBase = resourceManaged.value
	for {
		(from, to) <- webappBase ** "*" pair Path.rebase(webappBase, managedBase / "main" / "webapp")
	} yield {
		Sync.copy(from, to)
		to
	}
}

libraryDependencies += "com.typesafe.akka" %% "akka-actor"   % "2.5.18"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.18" % Test

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.11.v20180605"

libraryDependencies += "net.liftweb" %% "lift-util"   % "3.3.0"
libraryDependencies += "net.liftweb" %% "lift-webkit" % "3.3.0"
libraryDependencies += "net.liftweb" %% "lift-actor"  % "3.3.0"
libraryDependencies += "net.liftweb" %% "lift-common" % "3.3.0"
libraryDependencies += "net.liftmodules" %% "lift-jquery-module_3.3" % "2.10"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "org.dispatchhttp" %% "dispatch-core" % "1.0.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
