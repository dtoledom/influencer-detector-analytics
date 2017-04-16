package com.toledo.influencer_detector

import org.apache.spark.sql._
import org.apache.spark._

import org.apache.spark._
import org.apache.spark.graphx.{ Graph, Edge, VertexId, VertexRDD }
// To make some of the examples work we will also need RDD
import org.apache.spark.rdd.RDD

import com.datastax.spark.connector._
import com.datastax.spark.connector.cql._

object App {

  case class NodeRow(id: Long, name: String)
  case class EdgeRow(source: Long, destination: Long)

  def main(args: Array[String]): Unit = {

    val keyspace = "influencer_detector"
    val nodesTable = "nodes"
    val edgesTable = "edges"

    val conf = new SparkConf(true)
      .setAppName("Influencer Destector")
      .setMaster("local[*]")
      .set("spark.cassandra.connection.host", "127.0.0.1")

    val sc = new SparkContext(conf)

    // Retrieve graph data from Cassandra.
    val nodeRows = sc.cassandraTable[NodeRow](keyspace, nodesTable).select("id", "name")
    val edgesRows = sc.cassandraTable[EdgeRow](keyspace, edgesTable)

    // Initialize graphx
    val nodes: RDD[(VertexId, (String))] =
      nodeRows.map(nodeRow => (nodeRow.id, (nodeRow.name)))

    val edges: RDD[Edge[Null]] = edgesRows.map(edgeRow => Edge(edgeRow.source, edgeRow.destination))
    val defaultNode = ("None")

    val graph: Graph[String, _] = Graph(nodes, edges, defaultNode)
    graph.vertices.foreach(println)
    graph.edges.foreach(println)
  }
}
