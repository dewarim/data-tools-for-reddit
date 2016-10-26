package com.dewarim.reddit.spark.parquet

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SQLContext, SaveMode}

/**
  * Convert .json.bz2 input to parquet columnar storage format. 
  */
object ConvertToParquet {

  def convert(inputPath:String): Unit = {

    val conf = new SparkConf()
    // note: if you use Scala 1.6.1,
    // use one thread to prevent https://issues.apache.org/jira/browse/SPARK-1861 which causes:
    // java.lang.ArrayIndexOutOfBoundsException: 18002
    // at org.apache.hadoop.io.compress.bzip2.CBZip2InputStream.recvDecodingTables(CBZip2InputStream.java:730)
    // when using local[4]
    val sc = new SparkContext("local[4]", "Reddit Data Tools", conf)
    sc.hadoopConfiguration.set("mapreduce.input.fileinputformat.input.dir.recursive","true")

    val sqlContext = new SQLContext(sc)

    sqlContext.read.json(inputPath).write.mode(SaveMode.Overwrite).parquet("reddit-parquet")

    val df = sqlContext.read.parquet("reddit-parquet")

    val authorsByUpvote = df.filter("author != '[deleted]'").filter("body != '[deleted]'").filter("body != '[removed]'").groupBy("author").sum("ups").sort(sum("ups").desc)
    authorsByUpvote.show()
    sc.stop()
  }

  def main(args: Array[String]) {
    val inputPath = if(args.length > 0){
      args(0)
    }
    else{
      "data/RC_2007-10.bz2"
    }
    ConvertToParquet.convert(inputPath)
  }
}

