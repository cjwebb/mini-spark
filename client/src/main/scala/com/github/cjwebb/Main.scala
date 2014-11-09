package com.github.cjwebb

import java.util.{Properties, UUID}

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}

object Main extends App {
  val props = new Properties()
  props.put("client.id", UUID.randomUUID().toString)
  props.put("request.required.acks", "1")
  props.put("metadata.broker.list", "127.0.0.1:9092")
  props.put("producer.sync", "async")

  val producer = new Producer[Array[Byte], Array[Byte]](new ProducerConfig(props))

  def stuff(i: Int) = {
    new KeyedMessage[Array[Byte], Array[Byte]]("mini-spark", i.toString.getBytes, s"Hello $i".getBytes)
  }

  1 to 100 foreach { i =>
    producer.send(stuff(i))
    println(s"sent $i")
  }

}
