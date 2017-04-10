package com.dewarim.reddit.spark.parquet

import java.util.TimeZone

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

/**
  * Extract the month and day columns from comment data.  
  * This allows you to later on join the comments with the date data sets to easily restrict your
  * search by month or day for faster analysis.
  *
  * Going over the whole dataset each time and converting the unix timestamps to dates is more expensive
  * than generating this dataset once.
  *
  * CAVEAT: If you do this, be aware that a comment's timestamp is anchored 
  * to a moment in time and space calculated from (1970-01-01 00:00:00 UTC). 
  *
  * Example:
  * You want to see all comments in /r/politics for the whole day after your president declared war against someone,
  * which was on "2017-05-01 0:00 CEST", so you write a query
  * "join the data set with the extracted date set and give me all comments from 2017-05-01",
  * then you will actually get the comments from "2017-05-01 02:00 GMT" to "2017-05-02 02:00 GMT" 
  *
  * You can work around this by changing the TimeZone before runnint the Spark SQL statement.
  *
  * Expected: path to comment files in parquet format as argument.
  * Output: parquet files with fields (id,month*,day**,time) which can be joined with comment data set.
  * (*) month is actually YYYY-MM, so 2017-11
  * (**) day is YYYY-MM-dd, so 2017-04-10
  *
  */
object ExtractDateAndTime {

  /**
    * Compute and store the datum field for all comments (so we do not have to cast the created_utc field to date(YYYY-MM) every time)
    */
  def createDatumFieldForComments(df: DataFrame, spark: SparkSession): DataFrame = {
    df.select("id", "created_utc").createTempView("comments_datum")
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    val commentsDatum = spark.sql(
      """
        |select 
        | id,
        | from_unixtime(created_utc, 'YYYY') as year, 
        | from_unixtime(created_utc, 'YYYY-MM') as month, 
        | from_unixtime(created_utc, 'YYYY-MM-dd') as day, 
        | from_unixtime(created_utc, 'hh:mm') as time
        |from comments_datum  
      """.stripMargin)
    commentsDatum.show()
    commentsDatum
  }

  def execute(inputPath: String, outputPath: String): Unit = {
    val spark = SparkSession.builder()
      .appName("reddit data tools on spark")
      .master("local[*]")
      .getOrCreate()

    val df = spark.read.parquet(inputPath)
    val resultDf = createDatumFieldForComments(df, spark)
    resultDf.repartition(100).write.mode(SaveMode.Overwrite).parquet(outputPath)
    spark.stop()
  }

  def main(args: Array[String]) {
    if (args.length != 2) {
      println("Usage requires three parameters: path_to_comment_data, output_path_for_time_data")
    }
    else {
      val commentData = args(0)
      val outputPath = args(1)
      ExtractDateAndTime.execute(commentData, outputPath)
    }
  }
}

