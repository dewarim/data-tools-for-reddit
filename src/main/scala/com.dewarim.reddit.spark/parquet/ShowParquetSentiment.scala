package com.dewarim.reddit.spark.parquet

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

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

