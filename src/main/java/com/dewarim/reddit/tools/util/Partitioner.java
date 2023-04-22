package com.dewarim.reddit.tools.util;

import java.util.ArrayList;
import java.util.List;

public class Partitioner<T> {

    int BATCH_SIZE=1000;
    public List<List<T>> partitionList(List<T> items) {
        List<List<T>> partitions  = new ArrayList<>(items.size() / BATCH_SIZE);
        int           requestSize = items.size();
        int           rowCount    = 0;
        while (rowCount < requestSize) {
            int lastIndex = rowCount + BATCH_SIZE;
            if (lastIndex > requestSize) {
                lastIndex = requestSize;
            }
            List<T> partialList = items.subList(rowCount, lastIndex);
            partitions.add(partialList);
            rowCount += BATCH_SIZE;
        }
        return partitions;
    }
}
