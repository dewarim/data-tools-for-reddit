package com.dewarim.reddit.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.util.LongAccumulator

/**
  * Iterate over all rows in the dataset and count something with an Accumulator 
  *
  */
object CountStuff {

  def count(inputPath: String): Unit = {

    val spark = SparkSession.builder()
      .appName("reddit data tools on spark")
      .master("local[*]")
      .getOrCreate()

    // those settings won't help with a bug: mixed parquet column type of created_utc field. 
    //    spark.conf.set("spark.sql.parquet.enableVectorizedReader", "false")
    //    spark.conf.set("spark.sql.sources.partitionColumnTypeInference.enabled", "false")
    val sc = spark.sparkContext
    val charCount = sc.longAccumulator("charCount")

    // coment out parts of the list to reduce working set size
    val years = List(
      "07", "08", "09", "10", "11", "12", "13", "14",
      "15",
      "16"
    )
    years.foreach(year =>
      countPart(spark, charCount, inputPath + year + "*/*")
    )

    println(s"Total comment size: ${charCount.value} chars") // Total comment size: 441'659'151'923 chars

    spark.stop()
  }

  def countPart(spark: SparkSession, charCount: LongAccumulator, inputPath: String): Unit = {
    val df = spark.read.parquet(inputPath)
    df.select("body")
      .foreachPartition(itera => {
        itera.foreach { row =>
          charCount.add(row.getAs("body").toString.length)
        }
      }
      )
  }

  def main(args: Array[String]) {
    val inputPath = if (args.length > 0) {
      args(0)
    }
    else {
      //      "reddit-parquet-sentiment/*/*"
      //      "/home/ingo/data/reddit-parquet-sentiment/*/*" 
      "/home/ingo/data/reddit-parquet-sentiment/RC_20"
    }
    CountStuff.count(inputPath)
  }
}

