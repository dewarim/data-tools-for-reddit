package com.dewarim.reddit.spark.parquet

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * During conversion from .json.bz2 to parquet something went wrong and
  * the column type fo created_utc was mostly "long", but sometimes "string", 
  * which will cause read-ahead problems - parquet can write this, but it cannot read it.
  *
  * Note: this uses hard-coded paths, you have to adapt the code to your file system layout.
  * 
  * See: http://stackoverflow.com/questions/34384200/read-parquet-file-having-mixed-data-type-in-a-column
  * (where a complex solution is given, but for our purposes it seems easier to recreate the data from
  * original input)
  */
object FixCreatedField {

  /**
    * The path to the reddit-parquet-sentiment dataset.
    */
  val sourceRedditParquet = "/var/lib/transmission-daemon/downloads/reddit-parquet-sentiment/RC*/*.parquet"

  /**
    * Storage place for "id,fixed created_utc" parquet columns to be joined with the existing sourceRedditParquet
    */
  val tempPathForFixedColumn = "/home/ingo/reddit-created"
  
  /**
   * Where to put the fixed data files:
   */
  val fixedRedditParquetPath = "/home/ingo/fixed_reddit"
  
  /**
   * Source reddit comment data in JSON .bz2 format:
   */
  val sourceRedditJson =       "/var/lib/transmission-daemon/downloads/reddit_data/*/*.bz2"

  def convert(inputPath: String): Unit = {
    val spark = SparkSession.builder()
      .appName("reddit data tools on spark")
      .master("local[1]")
      .getOrCreate()
   

    val df = spark.read.json(inputPath)

    val idAndCreated = df.selectExpr("id", "cast(created_utc as long) created_utc")
    idAndCreated.show()
    idAndCreated.write.mode(SaveMode.Overwrite).parquet(tempPathForFixedColumn)
    
    val reddit = spark.read.parquet(sourceRedditParquet)
    reddit.drop("created_utc")
    val fixedReddit = reddit.join(idAndCreated, "id")
    fixedReddit.write.mode(SaveMode.Overwrite).parquet(fixedRedditParquetPath)
    spark.stop()
  }

  def main(args: Array[String]) {
    val inputPath = if (args.length > 0) {
      args(0)
    }
    else {
//      "data/RC_2007-10.bz2"
      sourceRedditJson
    }
    FixCreatedField.convert(inputPath)
  }
}

