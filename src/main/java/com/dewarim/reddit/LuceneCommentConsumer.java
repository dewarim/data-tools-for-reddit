package com.dewarim.reddit;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;

/**
 */
public class LuceneCommentConsumer implements CommentConsumer{

    IndexWriter indexWriter;
    Long counter = 0L;
    Long deleted = 0L;

    public LuceneCommentConsumer(IndexWriter indexWriter) {
        this.indexWriter = indexWriter;
    }

    @Override
    public void consume(Comment comment) {
        if(comment.body.equals("[deleted]")){
            // do not index deleted comments: they contain little of value.
            deleted++;
            return;
        }
        Document doc = new Document();

        doc.add(new StringField("author", comment.author, Field.Store.YES));
        doc.add(new StringField("name", comment.name, Field.Store.YES));
        doc.add(new TextField("body", comment.body, Field.Store.YES));
        doc.add(new IntField("gilded", comment.gilded, Field.Store.YES));
        doc.add(new IntField("score", comment.score, Field.Store.YES));
        doc.add(new IntField("ups", comment.ups, Field.Store.YES));
        doc.add(new IntField("downs", comment.downs, Field.Store.YES));
        doc.add(new LongField("created_utc", comment.created_utc, Field.Store.YES));
        doc.add(new StringField("parent_id", comment.parent_id, Field.Store.YES));
        doc.add(new StringField("subreddit", comment.subreddit, Field.Store.YES));
        doc.add(new StringField("id", comment.id, Field.Store.YES));
        doc.add(new StringField("url", Comment.createLink(comment.subreddit, comment.link_id, comment.id), Field.Store.YES));

        // List of fields is currently incomplete, indexing "edited" or "distinguished" or "retrieved_on"
        // are probably not main search fields. Indexing everything takes longer and requires more space.
        //        doc.add(new IntField("controversiality", comment.controversiality, Field.Store.YES));

        try {
            indexWriter.addDocument(doc);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        counter++;
        if(counter % 100_000 == 0){
            System.out.print(".");
        }
    }
}
