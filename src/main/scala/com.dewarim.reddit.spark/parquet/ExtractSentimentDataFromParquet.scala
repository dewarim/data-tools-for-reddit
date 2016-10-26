package com.dewarim.reddit.spark.parquet

import org.apache.spark.sql.SparkSession

/**
  * Extract just the comment id and sentiment data from the whole dataset. 
  */
object ExtractSentimentDataFromParquet {

  def convert(inputPath:String): Unit = {

    val spark = SparkSession.builder()
      .appName("reddit data tools on spark")
      .master("local[4]")
      .getOrCreate()

    val df = spark.read.parquet(inputPath)
    
    val sentiment = df.filter("author != '[deleted]'").filter("body != '[deleted]'").filter("body != '[removed]'")
    .select("id", "roundedFinalScore", "maxPosScore", "maxNegScore")
    sentiment.show()
    sentiment.write.parquet("results/reddit-sentiment-2016-08")
    spark.stop()
  }

  def main(args: Array[String]) {
    val inputPath = if(args.length > 0){
      args(0)
    }
    else{
      "reddit-parquet-sentiment/*/*"
    }
    ExtractSentimentDataFromParquet.convert(inputPath)
  }
}

