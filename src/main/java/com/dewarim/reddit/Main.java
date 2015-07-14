package com.dewarim.reddit;

import java.io.File;

public class Main {

    public static void main(String[] args) {
//        DataReader dataReader = new DataReader(new File("data", "firstHundred.json"), new PrintingCommentConsumer());
//        dataReader.parseFile();

        CountingCommentConsumer consumer =  new CountingCommentConsumer();
        DataReader dataReader = new DataReader(new File("data", "RC_2007-10"),consumer);
        dataReader.parseFile();
        System.out.println("\nTotal # of comments: "+consumer.getCounter());

    }
}
