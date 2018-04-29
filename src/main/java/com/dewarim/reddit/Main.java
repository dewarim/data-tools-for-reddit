package com.dewarim.reddit;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private String baseDir;
    private String luceneDir;

    public Main(String baseDir, String luceneDir) {
        this.baseDir = baseDir;
        this.luceneDir = luceneDir;
    }

    public static void main(String[] args) throws IOException {
        Args     cliArguments = new Args();
        JCommander commander    = JCommander.newBuilder().addObject(cliArguments).build();
        commander.parse(args);

        if((cliArguments.help)){
            commander.setColumnSize(80);
            commander.usage();
            return;
        }
        
        Main main = new Main(cliArguments.baseDir, cliArguments.luceneDir);
        main.indexEverything();

    }

    private static class Args {
        @Parameter(names = {"--baseDir"}, description = "Base directory where the reddit archives are stored by year.\n" +
                                                        "Example: /home/ingo/reddit, containing: 2007/RC_2007-01.bz2.")
        String baseDir = "data";

        @Parameter(names = "--luceneDir", description = "Directory where to put the index-all folder containing the Lucene index")
        String luceneDir = "data";

        @Parameter(names = {"--help","-h"}, help = true, description = "Display help text.")
        boolean help;
    }

    public static List<File> gatherInputFiles(String baseDir) {
        List<File> inputFiles = new ArrayList<>();
        for (int year = 2007; year < 2018; year++) {
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
        if(inputFiles.isEmpty()){
            System.out.println("No index files were found in "+baseDir+"");
        }
        return inputFiles;
    }

    private void indexEverything() throws IOException {
        List<File> inputFiles = gatherInputFiles(baseDir);
        File indexDir = new File(luceneDir, "index-all");
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
