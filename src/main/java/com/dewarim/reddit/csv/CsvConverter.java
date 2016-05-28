package com.dewarim.reddit.csv;

import com.dewarim.reddit.Comment;
import com.dewarim.reddit.DataReader;
import com.dewarim.reddit.Main;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Convert the Reddit JSON files into tab separated CSV files.
 */
public class CsvConverter {

    File       csvDir;
    List<File> inputFiles;
    CsvSchema  schema;
    CsvMapper csvMapper = new CsvMapper();

    public CsvConverter(File csvDir, List<File> inputFiles) {
        this.csvDir = csvDir;
        this.inputFiles = inputFiles;

        csvMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        schema = csvMapper.schemaFor(Comment.class);
        schema.withColumnSeparator('\t');
        schema.withHeader();

    }

    public void startConversion() throws IOException {
        for (File inputFile : inputFiles) {
            System.out.println("\nConverting: " + inputFile);
            totalConversion(inputFile);
        }
    }

    private void totalConversion(File data) throws IOException {
        ObjectWriter objectWriter = csvMapper.writer(schema);
        File csvOutput = new File(csvDir, data.getName() + ".csv.gz");
        FileOutputStream tempFileOutputStream = new FileOutputStream(csvOutput);
        GZIPOutputStream bufferedOutputStream = new GZIPOutputStream(tempFileOutputStream, 1024*1024);
        OutputStreamWriter writerOutputStream = new OutputStreamWriter(bufferedOutputStream, "UTF-8");
        BatchConsumer batchConsumer = new BatchConsumer(schema, objectWriter, writerOutputStream);
        DataReader dataReader = new DataReader(data, batchConsumer);
        dataReader.parseFile();
        batchConsumer.finishLastBatch();
        writerOutputStream.flush();
        writerOutputStream.close();
    }

    public static void main(String[] args) throws IOException {
//        File csvDir = new File("data", "csv");
        String inputPath = "F:/reddit_data";
        if(args.length > 0){
            inputPath = args[0];
        }
        String outputPath = inputPath+"/csv";
        if(args.length == 2){
            outputPath = args[1];
        }
        File csvDir = new File(outputPath);
        if (!csvDir.exists()) {
            boolean mkResult = csvDir.mkdirs();
            if (!mkResult) {
                throw new RuntimeException("Could not create " + csvDir + " directory for output.");
            }
        }
        List<File> inputFiles = Main.gatherInputFiles(inputPath);
//        inputFiles.add(new File("data", "firstHundred.json"));
        CsvConverter csvConverter = new CsvConverter(csvDir, inputFiles);
        csvConverter.startConversion();
    }
}
