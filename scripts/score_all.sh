#!/usr/bin/env bash

# Add a sentiment score by using the python script sentimentScoreJson.py

# Prerequisites: 
#  - ntlk toolkit data already in place (see: nltkDownload.py)
#  - comments as JSON data in bz2 compressed archives.
#  - you need to adapt the path in line 12 to match your system's layout. 

#!/bin/bash

for path in /home/ingo/reddit_data/20*; do

        for file in ${path}/*; do
                echo $file
                /home/ingo/bin/anaconda3/bin/python scoreCommentsJson.py $file
        done

done
