#!/usr/bin/env bash

set -e

# Adjust the path the first for loop below to match the location of your reddit data set.
# Use "mvn clean package" in the reddit-data-tools directory to create the target jar.
# Create a 'work' directory
# From the 'target' directory copy the reddit-1.0-SNAPSHOT-distribution.zip to 'work'
# Inside 'work' use "unzip reddit-1.0-SNAPSHOT-distribution.zip" 
# Put this script in the 'work' directory and run it.

cd reddit-1.0-SNAPSHOT

for path in /home/ingo/downloads/reddit_data/20*; do
        #echo $path                                                                                                                                                                                                                                                                                                                                                                           

        for file in ${path}/*.bz2; do
            echo $file
            java -cp reddit-1.0-SNAPSHOT.jar:lib/. com.dewarim.reddit.spark.AddSentimentToParquetFiles $file
        done

done







