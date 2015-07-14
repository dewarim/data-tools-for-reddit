package com.dewarim.reddit;


import com.google.gson.Gson;

import java.io.*;

/**
 * Read the comment objects from a given json file and hand them over to a CommentConsumer.
 */
public class DataReader {

    File            file;
    CommentConsumer consumer;

    public DataReader(File file, CommentConsumer consumer) {
        this.consumer = consumer;
        this.file = file;
    }

    public void parseFile() {
        try {
            BufferedReader buffy = new BufferedReader(new FileReader(file));
            Gson gson = new Gson();

            buffy.lines().forEach(line -> {
                Comment comment = gson.fromJson(line, Comment.class);
                consumer.consume(comment);
            });


        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to read the JSON data: " + e.getMessage(), e);
        }
    }
}
