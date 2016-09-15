package com.dewarim.reddit.db;

import com.dewarim.reddit.CommentConsumer;
import com.dewarim.reddit.DataReader;
import com.dewarim.reddit.Main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;


/**
 */
public class PgConverter {

   public static void main( String[] args ) throws IOException, SQLException {
      String inputPath = "data";
      if ( args.length > 0 ) {
         inputPath = args[0];
      }
      List<File> inputFiles = Main.gatherInputFiles(inputPath);

      String url = "jdbc:postgresql://localhost/reddit";
      Properties props = new Properties();
      props.setProperty("user", "ingo");
      props.setProperty("password", "dev");
      Connection conn = DriverManager.getConnection(url, props);
      BatchInsert consumer = new BatchInsert(conn);
      for ( File inputFile : inputFiles ) {
         new DataReader(inputFile, consumer).parseFile();
         consumer.finishLastBatch();
      }
   }
}
