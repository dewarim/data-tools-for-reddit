package com.dewarim.reddit.csv;

import com.dewarim.reddit.Comment;
import com.dewarim.reddit.CommentConsumer;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class BatchConsumer implements CommentConsumer {

    int           batchSize = 10000;
    List<Comment> batch     = new ArrayList<>();
    CsvSchema          schema;
    ObjectWriter       objectWriter;
    OutputStreamWriter outputStream;

    public BatchConsumer(CsvSchema schema, ObjectWriter objectWriter, OutputStreamWriter outputStream) {
        this.schema = schema;
        this.objectWriter = objectWriter;
        this.outputStream = outputStream;
    }

    @Override
    public void consume(Comment comment) {
        batch.add(comment);
        if (batch.size() == batchSize) {
            writeBatch();
            System.out.print(".");
        }

    }

    private void writeBatch() {
        if (batch.isEmpty()) {
            return;
        }
        try {
            objectWriter.writeValue(outputStream, batch);
            batch.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finishLastBatch() {
        writeBatch();
    }
}
