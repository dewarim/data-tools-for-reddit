package com.dewarim.reddit.spark

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Column, SQLContext}
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

/**
  * Note: requires a .bz2 data file.
  *
  * Schema:
  * root
  * |-- archived: boolean (nullable = true)
  * |-- author: string (nullable = true)
  * |-- author_flair_css_class: string (nullable = true)
  * |-- author_flair_text: string (nullable = true)
  * |-- body: string (nullable = true)
  * |-- controversiality: long (nullable = true)
  * |-- created_utc: string (nullable = true)
  * |-- distinguished: string (nullable = true)
  * |-- downs: long (nullable = true)
  * |-- edited: string (nullable = true)
  * |-- gilded: long (nullable = true)
  * |-- id: string (nullable = true)
  * |-- link_id: string (nullable = true)
  * |-- name: string (nullable = true)
  * |-- parent_id: string (nullable = true)
  * |-- retrieved_on: long (nullable = true)
  * |-- score: long (nullable = true)
  * |-- score_hidden: boolean (nullable = true)
  * |-- subreddit: string (nullable = true)
  * |-- subreddit_id: string (nullable = true)
  * |-- ups: long (nullable = true)
  */
object UpvoteCountJson {

  def count(): Unit = {

    val conf = new SparkConf()
    val sc = new SparkContext("local[4]", "Reddit Data Tools", conf)
    val sqlContext = new SQLContext(sc)

    val df = sqlContext.read
      .json("data/RC_2007-10.bz2")

    //    df.printSchema()

    val authorsByUpvote = df.filter("author != '[deleted]'").groupBy("author").sum("ups").sort(sum("ups").desc)
    authorsByUpvote.show()
    sc.stop()
  }

  def main(args: Array[String]) {
    UpvoteCountJson.count()
  }
}

