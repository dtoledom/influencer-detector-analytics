package com.toledo.influencer.detector

import scala.collection.JavaConversions.asScalaBuffer
import scala.util.Try

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Edge
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx.VertexId
import org.apache.spark.rdd.RDD

import com.datastax.spark.connector.toNamedColumnRef
import com.datastax.spark.connector.toSparkContextFunctions
import com.toledo.influencer.detector.model.EdgeRow
import com.toledo.influencer.detector.model.JobAnalysisRequest
import com.toledo.influencer.detector.model.JobAnalysisResult
import com.toledo.influencer.detector.model.NodeRow
import com.toledo.influencer.detector.model.NodeInfo
import com.toledo.influencer.detector.model.RankedNode
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import spark.jobserver.SparkJob
import spark.jobserver.SparkJobInvalid
import spark.jobserver.SparkJobValid
import spark.jobserver.SparkJobValidation
import org.apache.spark.graphx.GraphLoader
import org.apache.spark.graphx.EdgeDirection

object App extends SparkJob {

  val nodesTable = "nodes"
  val edgesTable = "edges"

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[4]").setAppName("WordCountExample")
      .set("spark.cassandra.connection.host", "127.0.0.1")

    val sc = new SparkContext(conf)
    // Default job to test with
    val config = ConfigFactory.parseString("""
      {
      	"input": {
      		"job": {
      			"keyspace": "influencer_detector",
      			"category": "Community",
      			"nodes": [
      				{
      					"id": 1234, "depth": 12
      				}, 
      				{
      					"id": 5678, "depth": 56
      				}
      			]
      		}
      	}
      }
      """)
      
    val results = runJob(sc, config)
    println("Result is " + results)
  }

  def parseConfig(config: Config): JobAnalysisRequest = {
    val nodes = config.getConfigList("input.job.nodes") map (node =>
      NodeInfo(node.getLong("id"), node.getInt("depth")))

    val category = config.getString("input.job.category")
    val keyspace = config.getString("input.job.keyspace")

    JobAnalysisRequest(keyspace, category, nodes.toArray)
  }

  override def validate(sc: SparkContext, config: Config): SparkJobValidation = {
    Try(parseConfig(config))
      .map(x => SparkJobValid)
      .getOrElse(SparkJobInvalid("Failed to parse JobAnalysisRequest"))
  }

  override def runJob(sc: SparkContext, config: Config): JobAnalysisResult = {
    // Retrieve graph data from Cassandra.
    val jobInfo = parseConfig(config)

    val nodeRows = sc.cassandraTable[NodeRow](jobInfo.keyspace, nodesTable).select("id", "name")
    val edgesRows = sc.cassandraTable[EdgeRow](jobInfo.keyspace, edgesTable)

    // Initialize graphx
    val nodes = selectNodes(sc, jobInfo.keyspace)
    val edges = selectEdges(sc, jobInfo.keyspace)
    val defaultNode = ("None", Seq.empty)
    val graph = Graph(nodes, edges, defaultNode)

    // Filter vertices by category
    val categoryNodes = graph.vertices.filter(node => node._2._2.contains(jobInfo.category))

    // Page rank over subgraph
    val pageScores = graph.pageRank(0.001).vertices

    // Arrange to (score:Double, (id:VertexId, name:String))
    val rankingByCategory = categoryNodes.join(pageScores)
      .map(node => RankedNode(node._2._2, node._1))

    JobAnalysisResult(
      jobInfo.category,
      graph.numVertices,
      graph.numEdges,
      rankingByCategory.top(10))

  }

  def selectNodes(sc: SparkContext, keyspace: String): RDD[(VertexId, (String, Seq[String]))] = {
    sc.cassandraTable[NodeRow](keyspace, nodesTable)
      .select("id", "name", "categories")
      .map(nodeRow => (nodeRow.id, (nodeRow.name, nodeRow.categories)))
  }

  def selectEdges(sc: SparkContext, keyspace: String): RDD[Edge[Null]] = {
    sc.cassandraTable[EdgeRow](keyspace, edgesTable)
      .map(edgeRow => Edge(edgeRow.source, edgeRow.destination))
  }

}
