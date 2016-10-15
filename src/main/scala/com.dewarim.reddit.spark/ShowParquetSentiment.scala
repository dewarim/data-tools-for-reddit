package com.dewarim.reddit.spark

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.{StructField, _}

/**
  * Note: this shows the first 20 lines of a parquet-with-sentiment data set.
  */
object ShowParquetSentiment {

  def show(inputPath: String): Unit = {

    val conf = new SparkConf()
    val sc = new SparkContext("local[1]", "Reddit Data Tools", conf)
    val sqlContext = new SQLContext(sc)

    val df = sqlContext.read.parquet(inputPath)
    df.show()

    sc.stop()
  }

  def main(args: Array[String]) {
    val inputPath = if (args.length > 0) {
      args(0)
    }
    else {
      "reddit-parquet-sentiment/RC_2007-10"
    }
    ShowParquetSentiment.show(inputPath)
  }
}

