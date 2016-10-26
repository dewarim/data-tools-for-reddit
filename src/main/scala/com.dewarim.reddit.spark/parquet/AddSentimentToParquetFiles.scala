package com.dewarim.reddit.spark.parquet

import java.io.File

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{SQLContext, SaveMode}

/**
  * Add sentiment data to comment data while converting to parquet. 
  *
  * Expected: each source *.bz2 file has an accompanying *.sentiment file.
  */
object AddSentimentToParquetFiles {

  def convert(inputPath: String): Unit = {
    val inputFile = new File(inputPath)
    val outputFoldername =inputFile.getName.replace(".bz2","") 
    val outputPath = "reddit-parquet/"+outputFoldername
    val conf = new SparkConf()
    val sc = new SparkContext("local[1]", "Reddit Data Tools", conf)
    sc.hadoopConfiguration.set("mapreduce.input.fileinputformat.input.dir.recursive", "true")

    val sqlContext = new SQLContext(sc)
    
    sqlContext.read.json(inputPath).write.mode(SaveMode.Overwrite).parquet(outputPath)
    
    val df = sqlContext.read.parquet(outputPath)

    val sentimentFile = inputPath.replace(".bz2", ".sentiment")
    val schema = new StructType(Array(
      StructField("roundedFinalScore", FloatType, true),
      StructField("maxPosScore", FloatType, true),
      StructField("maxNegScore", FloatType, true),
      StructField("id", StringType, true)
    ))

    val sentimentDf = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "false")
      .option("inferSchema", "false")
      .option("delimiter", "\t")
      .option("treatEmptyValuesAsNulls", "true")
      .schema(schema)
      .load(sentimentFile)
    
    val dfs = df.join(sentimentDf, "id")
//    println("Rows in joined dataset: "+dfs.count())
    dfs.write.mode(SaveMode.Overwrite).parquet("reddit-parquet-sentiment/"+outputFoldername)
    
//    val authorsByUpvote = df.filter("author != '[deleted]'").filter("body != '[deleted]'").filter("body != '[removed]'").groupBy("author").sum("ups").sort(sum("ups").desc)
//    authorsByUpvote.show()
//    sc.stop()
  }

  def main(args: Array[String]) {
    val inputPath = if (args.length > 0) {
      args(0)
    }
    else {
      "data/RC_2007-10.bz2"
    }
    AddSentimentToParquetFiles.convert(inputPath)
  }
}

