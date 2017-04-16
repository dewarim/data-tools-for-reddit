# How to convert and release updated files

This document describes the process to go from JSON-per-line bz2 to parquet format with sentiment data. (work-in-progress) 

## Download

The source for the original collection of Reddit JSON data is: [http://files.pushshift.io/reddit/comments/](Pushshift.io)

There currently is no download script as I only need to add a couple of new files to the ones already downloaded.

## Convert .bz2 to .gz

Using the bash script [src/main/bash/bzip2_to_gzip.sh](bzip2_to_gzip.sh), 
I convert the files into a format which should be easier to digest for Spark when running on a single machine.
This allows users to with standalone processes to use all cores on their machine when going over the *.gz data.

### Integrity checking

The script also creates a list of sha256sum hashes which must be compared to the original hash file from Pushshift.io.
I have had multiple downloads with corrupted data. The problem is: you can run Spark jobs just fine, until 
 the code encounters the broken file after several hours. Then you start to debug and get frustrated because
 "the code looks okay" - but the data is broken.
 

## Convert .gz to .parquet
 
Parquet is the better format when using Spark / Hadoop compared to raw gzipped JSON. It is a columnar data format which
allows you to just work on the fields you are interested in instead of parsing/reading all the JSON data on each run.
It is also easier for the backend to distribute Parquet data across several machines if you are using a cluster.
  
One problem that I encountered previously was: While reading the JSON files, the timestamped fields where not always converted
  correctly. This resulted in almost correct data, so you could run Spark queries against the dataset, but if you
  chose to filter by date, the process would fail. 
  So the new [src/main/scala/com/dewarim/reddit/spark/parquet/ConvertToParquet3.scala](ConvertToParquet3.scala) will
  try to do the right thing and cast the fields to long/int. 
 
    mvn clean package
    # copy target/reddit-1.0-SNAPSHOT-distribution.zip to target machine
    # unzip  unzip reddit-1.0-SNAPSHOT-distribution.zip 
    # cd  reddit-1.0-SNAPSHOT-distribution 
    java -cp reddit-1.0-SNAPSHOT.jar:lib/. com.dewarim.reddit.spark.parquet.ConvertGzToParquet /home/ingo/reddit-gz /home/ingo/reddit-parquet   
 
 
## Generate sentiment data

### Create sentiment data

The python script [src/main/python/scoreCommentsJson.py](scoreCommentsJson.py) generates sentiment data as extra files.
The script is based on the .bz2 files. To score all files at once,
the bash script [src/main/bash/score_all.sh](score_all.sh) should be used.

Dependencies:
* NLTK corpus (see: nltkDownload.py)
* Python (anaconda python distribution works fine)
 
### Convert sentiment data to parquet format



TODO: convert sentiment.csv to .parquet, but keep separate (join)
 
 