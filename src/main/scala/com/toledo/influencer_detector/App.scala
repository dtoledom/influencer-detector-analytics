package com.toledo.influencer_detector

import org.apache.spark.sql._
import org.apache.spark._

import com.datastax.spark.connector._
import com.datastax.spark.connector.cql._

object App {
  def main(args: Array[String]): Unit = {

    val keyspace = "influencer_detector"
    val nodesTable = "nodes"
    val edgesTable = "edges"

    val conf = new SparkConf(true)
      .set("spark.cassandra.connection.host", "127.0.0.1")

    val sc = new SparkContext("local[*]", "Influencer Destector", conf)

    val nodesRDD = sc.cassandraTable(keyspace, nodesTable)
    val edgesRDD = sc.cassandraTable(keyspace, edgesTable)

    println("Number of nodes:" + nodesRDD.count())
    println("Number of edges:" + edgesRDD.count())

  }
}