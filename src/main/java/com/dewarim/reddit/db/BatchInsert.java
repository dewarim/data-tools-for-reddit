package com.dewarim.reddit.db;

import com.dewarim.reddit.Comment;
import com.dewarim.reddit.CommentConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 create database "reddit";
 \c reddit
 create table comments (id serial primary key, comment_id VARCHAR(32),
 name VARCHAR(64), body text, gilded int, score int, ups int, created_utc BIGINT,
 parent_id varchar(32), url VARCHAR(255), subreddit varchar(64)
 );

 To use scoreComments.py, add the columns for
 sentiment_score, max_pos_score and max_neg_score.
 */
public class BatchInsert implements CommentConsumer {

   int           batchSize  = 1000;
   List<Comment> batch      = new ArrayList<>();
   Connection    connection;
   int           batchCount = 0;

   public BatchInsert( Connection connection ) throws SQLException {
      this.connection = connection;
      connection.setAutoCommit(false);
   }

   @Override
   public void consume( Comment comment ) {
      batch.add(comment);
      if ( batch.size() == batchSize ) {
         batchCount++;
         writeBatch();
         System.out.print(".");
         if ( batchCount % 100 == 0 ) {
            System.out.println();
         }

      }

   }

   private void writeBatch() {
      if ( batch.isEmpty() ) {
         return;
      }
      try {
         PreparedStatement statement = connection.prepareStatement(
            "insert into comments(comment_id,body,gilded,score,ups,created_utc,parent_id,url,subreddit, author) values(?,?,?,?,?,?,?,?,?,?)");

         for ( Comment comment : batch ) {
            if ( comment.body.equals("[deleted]") || comment.body.equals("[removed]") ) {
               continue;
            }
            statement.setString(1, comment.id);
            statement.setString(2, comment.body.trim());
            statement.setInt(3, comment.gilded);
            statement.setInt(4, comment.score);
            statement.setInt(5, comment.ups);
            statement.setLong(6, comment.created_utc);
            statement.setString(7, comment.parent_id);
            statement.setString(8, comment.createLink());
            statement.setString(9, comment.subreddit);
            statement.setString(10, comment.author);
            statement.execute();
         }
         connection.commit();
         batch.clear();
      }
      catch ( SQLException e ) {
         throw new RuntimeException(e);
      }
   }

   public void finishLastBatch() {
      writeBatch();
   }
}
