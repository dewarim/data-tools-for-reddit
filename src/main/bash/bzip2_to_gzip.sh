#!/usr/bin/env bash

set -e

# Convert the bz2 data to gz as the bzip2 implementation of Spark 2.0 is still susceptible to
# concurrency problems in standalone deployments.
# CBZip2InputStream is *not* thread-safe, and I am not sure if Spark 2 in standalone mode 
# handles master.local[1] correctly.

# switch to output directory:
cd reddit-gz

for path in /var/lib/transmission-daemon/downloads/reddit_data/20*; do
        #echo $path                                                                                                                                                                                                                                                                                                                                                                           
        for file in ${path}/*.bz2; do
          simple_name=$(basename $file)
          gz_name=${simple_name/bz2/gz}
          if [[ -e $gz_name ]]; then
            echo "$gz_name already exists in target path: do not convert again."
            continue
          fi
          
          # you should compare the hash values with those from 
          # http://files.pushshift.io/reddit/comments/sha256sums
          # to detect possible data corruption.
          echo "create hash value for ${file}"
          sha256sum $file >> reddit-sha265sums.txt
          echo "convert file: ${file} to ${gz_name}"
          bzcat $file | gzip > ${gz_name}
        done

done







