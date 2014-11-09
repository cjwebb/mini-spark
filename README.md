mini-spark
=========

This has a fully-working spark-streaming-with-kafka job. It contains two projects, `client` and `spark-job`.

Unsurprisingly, `spark-job` is the actual spark job. `client` generates simple strings and puts them onto Kafka for `spark-job` to retrieve and send to stdout.

To create the jars:

    sbt assembly

This should then generate two jars

    spark-job/target/scala-2.10/spark-job-assembly-0.0.1.jar
    client/target/scala-2.10/client-assembly-0.0.1.jar

On an appropriate server, you can run each like this:

    /opt/spark/bin/spark-submit --jars /opt/spark-streaming-kafka_2.10-1.1.0.jar --class com.github.cjwebb.minispark.SparkJob spark-job-assembly-0.1.1.jar

    java -cp client-assembly-0.0.1.jar com.github.cjwebb.minispark.Main

Note that the `spark-streaming-kafka_2.10-1.1.0.jar` needs to be present and added to the Spark classpath. In this project, it was assumed that jar would be available in the `/opt` directory, along with an installation of Spark.

It is also generally recommended that you generate messages using `client` after `spark-job` has started and is connected to Kafka.

## Configuration
Config is hardcoded into both `client` and `spark-job` projects. Unless your Spark and Kafka setup is identical, please change the appropriate values.
