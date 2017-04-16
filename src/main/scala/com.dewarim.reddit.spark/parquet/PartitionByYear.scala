package com.dewarim.reddit.spark.parquet

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

/**
  * Partition reddit data by year, so users have an easier time downloading / working on the dataset.
  * Note: this will take a long time and TBytes of disk space. Have not run it successfully yet due to out-of-space
  * problems. But if you need the data pre-partitioned, you can download and convert the raw data as it's already
  * one torrent per year.
  */
object PartitionByYear {


  def execute(commentData: String, timeData: String, outputPath: String): Unit = {
    val spark = SparkSession.builder()
      .appName("reddit data tools on spark")
      .master("local[*]")
      .getOrCreate()

    val df = spark.read.parquet(commentData)
    val timeDf = spark.read.parquet(timeData)
    val combinedDf = df.join(timeDf, "id")
    Range(2005, 2017).foreach(generatePartitionedData(_, combinedDf, outputPath))

    spark.stop()
  }

  def generatePartitionedData(year: Int, df: DataFrame, outputPath: String): Unit = {
    val yearStr: String = year.toString
    df.filter(row => row.getAs("year").equals(yearStr))
      .drop("year","month","day","hour") // no need to keep the timeData, you can always join it anew
      .repartition(12) // so we have roughly month-sized data chunks of < 10GByte. Note: one file != comments of one month 
      .write.mode(SaveMode.Overwrite).parquet(outputPath + "/" + yearStr)
  }

  def main(args: Array[String]) {
    if (args.length != 3) {
      println("Usage requires three parameters: path_to_comment_data, path_to_time_data, output_path")
    }
    else {
      val commentData = args(0)
      val timeData = args(1)
      val outputPath = args(2)
      PartitionByYear.execute(commentData, timeData, outputPath)
    }
  }
}

