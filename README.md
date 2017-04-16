# influencer-detector-analytics
Spark module for performing Page Rank algorithm over graph stored in Cassandra

# How to run
- Compile: `sbt compile`
- Submit Spark app:
```
spark-submit \
--class com.toledo.influencer_detector.HelloWorld \
--master local[*] \
--jars ~/.ivy2/cache/com.datastax.spark/spark-cassandra-connector_2.11/jars/spark-cassandra-connector_2.11-2.0.0.jar \
target/scala-2.11/influencer-detector-analytics_2.11-0.0.1.jar
```
