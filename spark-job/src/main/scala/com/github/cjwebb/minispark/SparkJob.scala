package com.github.cjwebb.minispark

import kafka.serializer.{Decoder, DefaultDecoder}
import kafka.utils.VerifiableProperties
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Decoders {
  class StringDecoder(props: VerifiableProperties) extends Decoder[String] {
    override def fromBytes(bytes: Array[Byte]): String = new String(bytes)
  }
}

object Printer {
  def print(rdd: RDD[(Array[Byte], Array[Byte])]) = {
    rdd.foreachPartition { partition =>
      partition.foreach { case (l, s) =>
        val a = new String(l)
        val b = new String(s)
        println(s"$a $b")
      }
    }
  }
}

object SparkJob extends App {

  val sparkConf = new SparkConf()
  sparkConf.set("spark.akka.frameSize", "1024")
  sparkConf.set("spark.executor.memory", "1g")
  sparkConf.setMaster("spark://127.0.0.1:7077")
  sparkConf.setAppName("MiniSpark")

  val ssc =  new StreamingContext(sparkConf, Seconds(20))
  ssc.checkpoint("mini-checkpoint")

  val kafkaParams = Map(
    "zookeeper.connect" -> "127.0.0.1:2181",
    "group.id" -> "spark",
    "zookeeper.connection.timeout" -> "10000"
  )

  val things = KafkaUtils.createStream[Array[Byte], Array[Byte], DefaultDecoder, DefaultDecoder](
    ssc, kafkaParams, Map("mini-spark" -> 1), StorageLevel.MEMORY_ONLY_SER_2
  )

  things foreachRDD (r => Printer.print(r))

  ssc.start()
}
