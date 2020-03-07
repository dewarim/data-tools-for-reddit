#!/usr/bin/env bash

set -e

# Convert all types of source formats to xz and copy them to target folder.

# switch to output directory:
export TARGET_PATH=/home/ingo/data/reddit-xz
cd ${TARGET_PATH}

export SOURCE_PATH=/home/ingo/data2/reddit-torrent

# handle bzip2
for file in ${SOURCE_PATH}/*.bz2; do
  simple_name=$(basename $file)
  xz_name=${simple_name/bz2/xz}
  if [[ -e $xz_name ]]; then
    echo "$xz_name already exists in target path: do not convert again."
    continue
  fi
  
  echo "convert file: ${file} to ${TARGET_PATH}/${xz_name}"
  bzcat $file | xz -9 -T 0 > ${xz_name}
done

# handle zstd
for file in ${SOURCE_PATH}/*.zst; do
  simple_name=$(basename $file)
  xz_name=${simple_name/zst/xz}
  if [[ -e $xz_name ]]; then
    echo "$xz_name already exists in target path: do not convert again."
    continue
  fi

  echo "convert file: ${file} to ${TARGET_PATH}/${xz_name}"
  zstcat $file | xz -9 -T 0 > ${xz_name}
done

# copy existing xz
for file in ${SOURCE_PATH}/*.xz; do
    simple_name=$(basename $file)
    echo "copy ${file} to ${TARGET_PATH}/${simple_name}"
    cp $file .
done







