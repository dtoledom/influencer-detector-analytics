# influencer-detector-analytics
Spark module for performing Page Rank algorithm over graph stored in Cassandra

# Requirements
- [Spark 2.1.0](http://spark.apache.org/): `brew install apache-spark`
- [sbt 0.13](http://www.scala-sbt.org/): `brew install sbt`
- [Cassandra 3.1](http://cassandra.apache.org/) `brew install cassandra`

# How to run
- Compile: `sbt package`
- Submit Spark app:
```
spark-submit \
--class com.toledo.influencer_detector.HelloWorld \
--master local[*] \
--jars ~/.ivy2/cache/com.datastax.spark/spark-cassandra-connector_2.11/jars/spark-cassandra-connector_2.11-2.0.0.jar \
target/scala-2.11/influencer-detector-analytics_2.11-0.0.1.jar
```
