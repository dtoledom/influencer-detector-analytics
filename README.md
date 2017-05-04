# Influencer Detector
Influencer Detector is a system designed with the purpose of minning Facebook pages info and analyzing their relations in order to calculate influence levels within certain category over a predefined graph.

![alt text](https://github.com/dtoledo23/influencer-detector-front/blob/master/src/assets/img/Arquitectura.png?raw=true Influencer Detector Architecture)

- [influencer-detector-front](https://github.com/dtoledo23/influencer-detector-front)
- [influencer-detector-back](https://github.com/dtoledo23/influencer-detector-back)
- [influencer-detector-crawler](https://github.com/dtoledo23/influencer-detector-crawler)
- [influencer-detector-analytics](https://github.com/dtoledo23/influencer-detector-analytics)

### About us
We developed Influencer Detector as a school project in the Advanced Databases course. The team:

- Monserrat Genereux
- Victor Garcia
- Diego Toledo

# influencer-detector-analytics
Core analytics logic. This module uses Page Rank algorithm to calculate the most important nodes in the Facebook Graph the user defined. It uses:
- [Spark](http://spark.apache.org/) to run the algorithm distributed over a cluster
- [Spark Graphx](http://spark.apache.org/graphx/) library utilities to work over graphs
- [Spark Job Server](https://github.com/spark-jobserver/spark-jobserver) to provide a REST API for easy job analytics requests from the backend.

## Requirements
- Cassandra 3.0
- Spark 1.6.2
- Spark Job Server 0.6.2
- scala 2.10
- sbt 0.1

## How to run locally
1. Run Spark Job Server docker container with the Cassandra connector:
`docker run -d -p 8090:8090 velvia/spark-jobserver:0.6.2.mesos-0.28.1.spark-1.6.1 --packages com.datastax.spark:spark-cassandra-connector_2.10:1.6.6`

2. Define a Spark Context connected to Cassandra. Insert your actual Cassandra IP address:
`curl -d "" 'localhost:8090/contexts/cassandra-context?num-cpu-cores=4&memory-per-node=512m&spark.cassandra.connection.host=<cassandra-ip>`

3. Build our analytics fat jar:
`sbt clean assembly`

4. Submit jar to Spark:
`curl --data-binary @./target/scala-2.10/toledo-influencer-detector_0.0.1.jar localhost:8090/jars/influencer_detector`

This sets the following:
- Context: `cassandra-context`
- App: `influencer_detector`

These values are used by the backend. If you decide to change just keep in mind that those need to be changed there also.

## How to deploy to AWS
1. Build our analytics fat jar:
`sbt clean assembly`

2. Follow this awesome AWS tutorial: [Installing and Running JobServer for Apache Spark on Amazon EMR](https://aws.amazon.com/blogs/big-data/installing-and-running-jobserver-for-apache-spark-on-amazon-emr/)

The configurations we used for deploying to EMR are under under `/config`
