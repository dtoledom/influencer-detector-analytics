name := "influencer-detector-analytics"
version := "0.0.1"

lazy val root = (project in file("."))

scalaVersion := "2.11.8"

mainClass in Compile := Some("com.toledo.influencer_detector.App")

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.0.0",
  "org.apache.spark" %% "spark-sql" % "2.0.0", 
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0"
)