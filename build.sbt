import Dependencies._
import Versions._

mainClass in Compile := Some("com.toledo.influencer.detector.App")
mainClass in assembly := Some("com.toledo.influencer.detector.App")
assemblyJarName in assembly := "toledo-influencer-detector_0.0.1.jar"

lazy val commonSettings: Seq[Def.Setting[_]] = Defaults.coreDefaultSettings ++ Seq(
  organization := "com.toledo",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.10.6",
  resolvers ++= Dependencies.repos,
  dependencyOverrides += "org.scala-lang" % "scala-library" % scalaVersion.value,
  parallelExecution in Test := false,
  scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature"),
  // We need to exclude jms/jmxtools/etc because it causes undecipherable SBT errors  :(
  ivyXML :=
    <dependencies>
    <exclude module="jms"/>
    <exclude module="jmxtools"/>
    <exclude module="jmxri"/>
    </dependencies>
)

lazy val rootSettings = Seq(
  // Must run Spark tests sequentially because they compete for port 4040!
  parallelExecution in Test := false,

  // disable test for root project
  test := {}
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    rootSettings,
    name := "influencer-detector-analytics",
    libraryDependencies ++= sparkDeps ++ typeSafeConfigDeps ++ sparkExtraDeps ++ coreTestDeps
      ++ jobserverDeps ++ cassandraDeps,
    test in assembly := {},
    fork in Test := true
)

assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "application.conf"                                  => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}