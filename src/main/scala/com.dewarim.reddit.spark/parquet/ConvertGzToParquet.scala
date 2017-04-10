package com.dewarim.reddit.spark.parquet

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.types.{IntegerType, LongType}
import org.apache.spark.sql.{SQLContext, SaveMode}

/**
  * Convert .json.bz2 input to parquet columnar storage format. 
  * 
  */
object ConvertGzToParquet {

  /**
    * Source reddit comment data in JSON .gz format:
    */
  //  val sourceRedditJson = "/home/ingo/data/reddit-json/2015/RC_2015-10.gz"
  val sourceRedditJson = "/home/ingo/code/reddit-data-tools/data/RC_2007-10.gz"
  val defaultOutputPath = "/home/ingo/reddit-parquet"

  def convert(inputPath: String, outputPath : String): Unit = {

    val conf = new SparkConf()
    val sc = new SparkContext("local[*]", "Reddit Data Tools", conf)
    
    val sqlContext = new SQLContext(sc)

    val df = sqlContext.read.json(inputPath)
    val df2 = df
      .withColumn("created_utc2", df.col("created_utc").cast(LongType)).drop("created_utc").withColumnRenamed("created_utc2", "created_utc")
      .withColumn("gilded2", df.col("gilded").cast(IntegerType)).drop("gilded").withColumnRenamed("gilded2", "gilded")
      .withColumn("score2", df.col("score").cast(IntegerType)).drop("score").withColumnRenamed("score2", "score")
      .withColumn("ups2", df.col("ups").cast(IntegerType)).drop("ups").withColumnRenamed("ups2", "ups")
    df2.show()  
    df2.write.mode(SaveMode.Overwrite).parquet(outputPath)

    // testing new data:
    sqlContext.read.parquet(outputPath).select("id", "created_utc", "gilded", "score", "ups", "subreddit").show()
    sqlContext.read.parquet(outputPath).orderBy("created_utc") show()

    sc.stop()
  }

  def main(args: Array[String]) {
    val inputPath = if (args.length > 0) {
      args(0)
    }
    else {
      sourceRedditJson
    }
    val outputPath = if(args.length > 1){
      args(1)
    }
    else{
      defaultOutputPath
    }
    ConvertGzToParquet.convert(inputPath, outputPath)
  }
}

