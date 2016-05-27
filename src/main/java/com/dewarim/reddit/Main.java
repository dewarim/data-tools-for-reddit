package com.dewarim.reddit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
//        DataReader dataReader = new DataReader(new File("data", "firstHundred.json"), new PrintingCommentConsumer());
//        dataReader.parseFile();

//        doIndex();
        indexEverything();

    }

    public static void doIndex() throws IOException {
        List<File> inputFiles = new ArrayList<>();
        inputFiles.add(new File("data", "RC_2007-10"));
        File indexDir = new File("data", "index");
        if (indexDir.exists()) {
            File[] oldIndexFiles = indexDir.listFiles();
            if (oldIndexFiles != null) {
                for (File file : oldIndexFiles) {
                    boolean wasDeleted = file.delete();
                    if (!wasDeleted) {
                        throw new RuntimeException("Could not delete old index file " + file.getAbsolutePath());
                    }
                }
            }
        } else {
            boolean result = indexDir.mkdirs();
            if (!result) {
                throw new RuntimeException("Could not create index directory.");
            }
        }
        Indexer indexer = new Indexer(indexDir, inputFiles, false);
        indexer.startIndexing();
        Searcher searcher = new Searcher(indexDir, "body");
        searcher.search("reddit gold");
    }

    public static List<File> gatherInputFiles(String baseDir) {
        List<File> inputFiles = new ArrayList<>();
        for (int year = 2007; year < 2017; year++) {
            File singleYearDir = new File(baseDir + File.separator + year);
            File[] files = singleYearDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("RC_")) {
                        inputFiles.add(file);
                    }
                }
            }
        }
        return inputFiles;
    }

    public static void indexEverything() throws IOException {
        String baseDir = "F:/reddit_data";
        List<File> inputFiles = gatherInputFiles(baseDir);
        File indexDir = new File("data", "index-all");
        if (indexDir.exists()) {
            File[] oldIndexFiles = indexDir.listFiles();
            if (oldIndexFiles != null) {
                for (File file : oldIndexFiles) {
                    boolean wasDeleted = file.delete();
                    if (!wasDeleted) {
                        throw new RuntimeException("Could not delete old index file " + file.getAbsolutePath());
                    }
                }
            }
        } else {
            boolean result = indexDir.mkdirs();
            if (!result) {
                throw new RuntimeException("Could not create index directory.");
            }
        }
        Indexer indexer = new Indexer(indexDir, inputFiles, false);
        indexer.startIndexing();
    }

    public static void countComments() {
        CountingCommentConsumer consumer = new CountingCommentConsumer();
        DataReader dataReader = new DataReader(new File("data", "RC_2007-10"), consumer);
//        DataReader dataReader = new DataReader(new File("F:/reddit_data/2015", "RC_2015-01"),consumer);
        dataReader.parseFile();
        System.out.println("\nTotal # of comments: " + consumer.getCounter());
    }
}
