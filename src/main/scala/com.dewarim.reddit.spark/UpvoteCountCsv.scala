package com.dewarim.reddit.spark


import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.types._

/**
 */
object UpvoteCountCsv {

  def count(): Unit = {

    val conf = new SparkConf()
    val sc: SparkContext = new SparkContext("local[4]", "Reddit Data Tools", conf)
    val sqlContext = SparkSession.builder().config(conf).getOrCreate()

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
      .format("json")
      .option("header", "false")
      .option("inferSchema", "false")
      .option("treatEmptyValuesAsNulls", "true")
      .schema(schema)
      // not working due to missing native zstd lib in hadoop dependency:
      // .load("data/*.zst")
      // expects: files with one-json-object-per-row
      .load("data/*.json")

    df.show()

    sc.stop()
  }

  def main(args: Array[String]) {
    UpvoteCountCsv.count()
  }
}

