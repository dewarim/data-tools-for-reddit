package com.dewarim.reddit.spark.parquet

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

/**
  * Partition reddit data by year, so users have an easier time downloading / working on the dataset.
  * Note: this will take a long time and TBytes of disk space. Have not run it successfully yet due to out-of-space
  * problems. But if you need the data pre-partitioned, you can download and convert the raw data as it's already
  * one torrent per year.
  * 
  * You can run this with in the reddit-1.0-SNAPSHOT directory with:   
  * java -cp reddit-1.0-SNAPSHOT.jar:lib/. com.dewarim.reddit.spark.parquet.PartitionByYear /home/ingo/reddit-parquet /home/ingo/time-data /data/reddit-by-year
  */
object PartitionByYear {


  def execute(commentData: String, timeData: String, outputPath: String): Unit = {
    val spark = SparkSession.builder()
      .appName("reddit data tools on spark")
      .master("local[*]")
      .getOrCreate()

    val df = spark.read.parquet(commentData)
    df.createTempView("everything")
    val timeDf = spark.read.parquet(timeData)
    timeDf.createTempView("all_times")
    Range(2007, 2017).foreach(generatePartitionedData(_, outputPath, spark))

    spark.stop()
  }

  def generatePartitionedData(year: Int, outputPath: String, spark: SparkSession): Unit = {
    val yearStr: String = year.toString
    spark.sql(s"select e.* from everything e join all_times a using(id) where a.year=$yearStr")
//      .drop("year","month","day","hour") // no need to keep the timeData, you can always join it anew
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

