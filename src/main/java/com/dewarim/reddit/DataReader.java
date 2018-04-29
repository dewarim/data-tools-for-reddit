package com.dewarim.reddit;


import com.google.gson.Gson;
import org.apache.commons.compress.compressors.CompressorException;
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
            FileInputStream fin         = new FileInputStream(file);
            String[]        dottedParts = file.getName().split("\\.");
            String          extension   = dottedParts[dottedParts.length - 1].toLowerCase();

            CompressorStreamFactory streamFactory = new CompressorStreamFactory();
            InputStream             input         = null;
            switch (extension) {
                case "bz2":
                    input = streamFactory.createCompressorInputStream(CompressorStreamFactory.BZIP2, fin);
                    break;
                case "gz":
                    input = streamFactory.createCompressorInputStream(CompressorStreamFactory.GZIP, fin);
                    break;
                case "xz":
                    input = streamFactory.createCompressorInputStream(CompressorStreamFactory.XZ, fin);
                    break;
                default: // uncompressed json?
                    input = fin;
            }

            BufferedReader buffy = new BufferedReader(new InputStreamReader(input));
            Gson           gson  = new Gson();

            buffy.lines().forEach(line -> {
                Comment comment = gson.fromJson(line, Comment.class);
                consumer.consume(comment);
            });


        } catch (IllegalArgumentException | CompressorException | IOException e) {
            throw new RuntimeException(String.format("An error occurred while trying to read the JSON data of %s: %s ", file.getAbsolutePath(), e.getMessage()), e);
        }
    }
}
