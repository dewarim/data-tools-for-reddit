package com.dewarim.reddit;


import com.google.gson.Gson;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

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
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fin);
            CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
            BufferedReader buffy = new BufferedReader(new InputStreamReader(input));
            Gson gson = new Gson();

            buffy.lines().forEach(line -> {
                Comment comment = gson.fromJson(line, Comment.class);
                consumer.consume(comment);
            });


        } catch (CompressorException|IOException e) {
            throw new RuntimeException("An error occurred while trying to read the JSON data: " + e.getMessage(), e);
        }
    }
}
