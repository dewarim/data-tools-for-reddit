package com.dewarim.reddit;


import com.google.gson.Gson;

import java.io.*;
import java.util.function.Consumer;

/**
 * Read the data from a given json file and give them to a CommentConsumer.
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

            buffy.lines().forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    Comment comment = gson.fromJson(s, Comment.class);
                    consumer.consume(comment);
                }
            });


        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to read the JSON data: " + e.getMessage(), e);
        }
    }
}
