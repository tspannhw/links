package com.dataflowdeveloper.links

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.serializer.{KryoSerializer}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.serializer.{KryoSerializer, KryoRegistrator}
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SQLContext
import com.databricks.spark.avro._
import org.apache.spark.sql.hive.orc._

/**
  * Created by timothyspann on 12/21/2016
  */

case class LinkRecord( link: String, descr: String )

object Links {

  //// Main Spark Program
  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.apache.spark.storage.BlockManager").setLevel(Level.ERROR)
    Logger.getLogger("com.dataflowdeveloper.links").setLevel(Level.INFO)

    val log = Logger.getLogger("com.dataflowdeveloper.links")
    log.info("Started Links Analysis")

    val sparkConf = new SparkConf().setAppName("Links")

    sparkConf.set("spark.cores.max", "2")
    sparkConf.set("spark.serializer", classOf[KryoSerializer].getName)
    sparkConf.set("spark.sql.tungsten.enabled", "true")
    sparkConf.set("spark.eventLog.enabled", "true")
    sparkConf.set("spark.app.id", "Links")
    sparkConf.set("spark.io.compression.codec", "snappy")
    sparkConf.set("spark.rdd.compress", "false")
    sparkConf.set("spark.suffle.compress", "true")

    val sc = new SparkContext(sparkConf)

      try {
        // output to JSON and PARQUET
        val sqlContext = new SQLContext(sc)
        import sqlContext.implicits._
        sqlContext.setConf("spark.sql.orc.filterPushdown", "true")

        // Reference: https://spark.apache.org/docs/1.6.2/sql-programming-guide.html#json-datasets
        val df1 = sqlContext.read.json("hdfs://tspanndev10.field.hortonworks.com:8020/linkprocessor/379875e9-5d99-4f88-82b1-fda7cdd7bc98.json")
        df1.printSchema()

        df1.write.format("parquet").mode(org.apache.spark.sql.SaveMode.Append).parquet("parquetresults")
        df1.write.format("avro").mode(org.apache.spark.sql.SaveMode.Append).avro("avroresults")
        df1.write.format("orc").mode(org.apache.spark.sql.SaveMode.Append).json("orcresults")
        println("After writing results")
      } catch {
        case e: Exception =>
          log.error("Writing files after job. Exception:" + e.getMessage);
          e.printStackTrace();
      }

    sc.stop()
  }
}
