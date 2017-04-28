import sbt._

object Dependencies {

  import Versions._

  //NettyIo is very bad one, the organization name is different from the jar name for older versions
  val excludeNettyIo = ExclusionRule(organization = "org.jboss.netty")

  lazy val sparkDeps = Seq(
    // Should be provided
    "org.apache.spark" %% "spark-core" % spark % "provided" excludeAll (excludeNettyIo),
    // Force netty version.  This avoids some Spark netty dependency problem.
    "io.netty" % "netty-all" % netty
  )

  lazy val sparkExtraDeps = Seq(
    // Should all be provided
    "org.apache.spark" %% "spark-graphx" % spark % "provided" excludeAll (excludeNettyIo),
    "org.apache.spark" %% "spark-sql" % spark % "provided" excludeAll (excludeNettyIo),
    "org.apache.spark" %% "spark-streaming" % spark % "provided" excludeAll (excludeNettyIo)
  )

  lazy val jobserverDeps = Seq(
     // Should be provided
    "spark.jobserver" %% "job-server-api" % jobServer % "provided"
  )
  
  lazy val cassandraDeps = Seq (
     // Should be provided
    "com.datastax.spark" %% "spark-cassandra-connector" % cassandra % "provided"
  )

  // This is needed or else some dependency will resolve to 1.3.1 which is in jdk-8
  lazy val typeSafeConfigDeps = Seq("com.typesafe" % "config" % typesafeConfig force())
  
  lazy val coreTestDeps = Seq()

  val repos = Seq(
    "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
    "Job Server Bintray" at "https://dl.bintray.com/spark-jobserver/maven",
    "jitpack" at "https://jitpack.io"
  )
}
