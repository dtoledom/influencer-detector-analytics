package com.toledo.influencer.detector.model

case class JobAnalysisRequest(
  keyspace: String,
  category: String,
  nodes: Array[NodeInfo])
  
case class JobAnalysisResult(
    category: String, 
    numberNodes: Long, 
    numberEdges: Long, 
    topFive: Array[RankedNode]
)

case class NodeInfo(
  id: Long,
  depth: Int)

case class RankedNode(
    score: Double,
    id: Long) extends Ordered[RankedNode] {
  import scala.math.Ordered.orderingToOrdered

  def compare(that: RankedNode): Int = this.score compare that.score
}