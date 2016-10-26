package com.dewarim.reddit.spark.parquet

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

/**
  * Find the 100 users with the highest average sentiment score and write their stats to CSV files in /tmp.
  * Group by 10,100,1000 minimum comments - the higher the minimum comment treshold, the more likely
  * you are to find bots and spammers.
  *
  * Expected: path to comment files in parquet format as argument.
  *
  */
object FindMostPositiveUsers {

  def search(inputPath: String): Unit = {
    val conf = new SparkConf()
    // local[4] means: use 4 threads.
    val sc = new SparkContext("local[4]", "Reddit Data Tools", conf)
    sc.hadoopConfiguration.set("mapreduce.input.fileinputformat.input.dir.recursive", "true")

    val sqlContext = new SQLContext(sc)

    val df = sqlContext.read.parquet(inputPath)

     df.filter("author != '[deleted]'")
      .filter("body != '[deleted]'")
      .filter("body != '[removed]'")
      .select("author", "roundedFinalScore", "score")
      .createTempView("filteredDf")
    
    val limits = List(10,100,1000)
    limits.foreach{limit =>
      val counting = sqlContext.sql(s"select author, avg(roundedFinalScore) as average_sentiment, avg(score) as average_score, count(*) as comments from filteredDf group by author having count(*) > $limit order by average_sentiment desc")
      //    counting.show(100)
      counting.limit(100).write.format("org.apache.spark.sql.execution.datasources.csv.CSVFileFormat").option("header","true").save(s"/tmp/reddit-sentiment-$limit.txt")
    }

    sc.stop()
  }

  def main(args: Array[String]) {
    val inputPath = if (args.length > 0) {
      args(0)
    }
    else {
      // the asterisk slash asterisk will load files from one folder level below the given folder.
      "reddit-parquet-sentiment/*/*"
    }
    FindMostPositiveUsers.search(inputPath)
  }
}

