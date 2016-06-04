package com.dewarim.reddit.spark


import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types._

/**
  * Note: will fail on standard set because of newlines in CSV records.
  */
object UpvoteCountCsv {

  def count(): Unit = {

    val conf = new SparkConf()
    val sc = new SparkContext("local[4]", "Reddit Data Tools", conf)
    val sqlContext = new SQLContext(sc)

    val schema = new StructType(Array(
      StructField("author", StringType, true),
      StructField("name", StringType, true),
      StructField("body", StringType, true),
      StructField("author_flair_text", StringType, true),
      StructField("gilded", StringType, true),
      StructField("score_hidden", BooleanType, true),
      StructField("score", IntegerType, true),
      StructField("link_id", StringType, true),
      StructField("retrieved_on", LongType, true),
      StructField("author_flair_css_class", StringType, true),
      StructField("subreddit", StringType, true),
      StructField("edited", BooleanType, true),
      StructField("ups", IntegerType, true),
      StructField("downs", IntegerType, true),
      StructField("controversiality", IntegerType, true),
      StructField("created_utc", LongType, true),
      StructField("parent_id", StringType, true),
      StructField("archived", BooleanType, true),
      StructField("subreddit_id", StringType, true),
      StructField("id", StringType, true),
      StructField("distinguished", StringType, true)
    ))

    val df = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "false")
      .option("inferSchema", "false")
      .schema(schema)
      .load("data/RC_2007-10.csv.gz")

    df.show()

    sc.stop()
  }

  def main(args: Array[String]) {
    UpvoteCountCsv.count()
  }
}

