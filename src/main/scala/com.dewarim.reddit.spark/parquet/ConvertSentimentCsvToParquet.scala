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
object ConvertSentimentCsvToParquet {

  def convert(inputPath: String): Unit = {
    val inputFile = new File(inputPath)
    val outputFoldername =inputFile.getName.replace(".gz","") 
    val outputPath = "reddit-parquet/"+outputFoldername
    val conf = new SparkConf()
    val sc = new SparkContext("local[*]", "Reddit Data Tools", conf)
    sc.hadoopConfiguration.set("mapreduce.input.fileinputformat.input.dir.recursive", "true")

    val sqlContext = new SQLContext(sc)
    
    val df = sqlContext.read.parquet(outputPath)

    val sentimentFile = inputPath.replace(".gz", ".sentiment")
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
    
    sentimentDf.write.mode(SaveMode.Overwrite).parquet("sentiment-for-reddit-comments/"+outputFoldername)
    
  }

  def main(args: Array[String]) {
    val inputPath = if (args.length > 0) {
      args(0)
    }
    else {
      "data/RC_2007-10.gz"
    }
    ConvertSentimentCsvToParquet.convert(inputPath)
  }
}

