package com.toledo.influencer.detector.model

import org.apache.spark.graphx.VertexId
import org.apache.spark.rdd.RDD
 
case class NodeRow(
    id: Long, 
    name: String, 
    categories: Seq[String]
)

case class EdgeRow(
    source: Long, 
    destination: Long
)