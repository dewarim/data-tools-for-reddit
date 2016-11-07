#!/usr/bin/env bash

set -e

# Convert the bz2 data to gz as the bzip2 implementation of Spark 2.0 is still susceptible to
# concurrency problems in standalone deployments.
# CBZip2InputStream is *not* thread-safe, and I am not sure if Spark 2 in standalone mode 
# handles master.local[1] correctly.

cd reddit-gz

for path in /var/lib/transmission-daemon/downloads/reddit_data/20*; do
        #echo $path                                                                                                                                                                                                                                                                                                                                                                           
        for file in ${path}/*.bz2; do
            echo "convert: $file to "
            simple_name=$(basename $file)
            gz_name=${simple_name/bz2/gz}
            echo $gz_name
            bzcat $file | gzip > ${gz_name}
        done

done







