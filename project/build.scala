import com.typesafe.sbt.SbtStartScript
import sbt._
import sbtassembly.Plugin._
import AssemblyKeys._
import Keys._


object MiniSpark extends Build {
  val sparkVersion = "1.1.0"

  val sharedSettings: Seq[Def.Setting[_]] = Seq(
    exportJars := true,
    organization := "com.github.cjwebb",
    version := "0.0.1",
    scalaVersion := "2.10.4",
    resolvers ++= Seq(
      "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases",
      "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/"
    ),
    javaOptions ++= Seq(
      "-Dibm.cl.verbose=*DataType"
    )
  )

  lazy val client = Project(
    id = "client",
    base = file("client"),
    settings = Defaults.coreDefaultSettings ++
      sharedSettings ++ assemblySettings
    ).settings(
      name := "client",
      fork in Test := true,
      libraryDependencies ++= Seq(
        "log4j" % "log4j" % "1.2.16",
        "org.apache.kafka" %% "kafka" % "0.8.1.1"
      ),
      mergeStrategy in assembly <<= (mergeStrategy in assembly) {
        (old) => {
          case f if f contains "cmdline.arg.info.txt.1" => MergeStrategy.first
          case x => old(x)
        }
      }
    )

  lazy val spark_job = Project(
    id = "spark-job",
    base = file("spark-job"),
    settings = Defaults.coreDefaultSettings ++
      sharedSettings ++ assemblySettings ++
      net.virtualvoid.sbt.graph.Plugin.graphSettings
  ).settings(
    name := "spark-job",
    fork in Test := true,
    run in Compile <<= Defaults.runTask(
      fullClasspath in Compile,
      mainClass in (Compile, run),
      runner in (Compile, run)
    ),
    libraryDependencies ++= Seq(
      "log4j" % "log4j" % "1.2.16",
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
        exclude("org.apache.zookeeper", "zookeeper")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.slf4j", "slf4j-log4j12")
        exclude("org.slf4j", "jul-to-slf4j")
        exclude("org.slf4j", "jcl-over-slf4j")
        exclude("com.twitter", "chill_2.10")
        exclude("log4j", "log4j"),
      "org.apache.spark" %% "spark-streaming-kafka" % sparkVersion % "provided"
        exclude("org.apache.zookeeper", "zookeeper"),
      "org.apache.kafka" %% "kafka" % "0.8.1.1"
        exclude("javax.jms", "jms")
        exclude("com.sun.jdmk", "jmxtools")
        exclude("com.sun.jmx", "jmxri")
        exclude("org.slf4j", "slf4j-simple")
        exclude("log4j", "log4j")
        exclude("org.apache.zookeeper", "zookeeper")
        exclude("com.101tec", "zkclient"),
      "com.101tec" % "zkclient" % "0.3"
        exclude("org.apache.zookeeper", "zookeeper")
    ),
    mergeStrategy in assembly <<= (mergeStrategy in assembly) {
      (old) => {
        case f if f contains "META-INF/ECLIPS" => MergeStrategy.discard
        case f if f contains "META-INF/mailcap" => MergeStrategy.discard
        case x => old(x)
      }
    }
  ) 
}
