package com.dewarim.reddit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

/**
 */
public class Searcher {

    File          indexDir;
    IndexReader   reader;
    IndexSearcher searcher;
    QueryParser   queryParser;
    String        defaultField;

    public Searcher(File indexDir, String defaultField) throws IOException {
        this.indexDir = indexDir;
        this.reader = DirectoryReader.open(FSDirectory.open(indexDir.toPath()));
        this.searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        this.queryParser = new QueryParser(defaultField, analyzer);
        this.defaultField = defaultField;
    }

    public void search(String queryValue) {
        try {
            Query query = queryParser.parse(queryValue);
            countMatches(query);
            TopDocs topDocs = searcher.search(query, 10);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : hits) {
                Document doc = searcher.doc(scoreDoc.doc);
                say("Score: " + scoreDoc.score + " author: " + doc.get("author") + ", url: " + doc.get("url") + "\n" + doc.get("body"));
            }
        } catch (ParseException | IOException p) {
            throw new RuntimeException(p);
        }
    }
    public void search(Query query) {
        try {
            countMatches(query);
            TopDocs topDocs = searcher.search(query, 10);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : hits) {
                Document doc = searcher.doc(scoreDoc.doc);
                say("DocScore: " + scoreDoc.score + " author: " + doc.get("author") +
                        ", ups:"+doc.get("ups")+", url: " + doc.get("url") + "\n" + doc.get("body"));
            }
        } catch (IOException p) {
            throw new RuntimeException(p);
        }
    }

    public long countDocuments() throws IOException {
        return reader.getDocCount(defaultField);
    }

    private long countMatches(Query query) throws IOException{
        TotalHitCountCollector collector = new TotalHitCountCollector();
        searcher.search(query, collector);
        long totalHits = collector.getTotalHits();
        System.out.println("Found: " + totalHits + " matching documents.");

        if (collector.getTotalHits() > 10) {
            say("Going to display top 10:");
        } else if ( totalHits == 0) {
            say("Found nothing.");
        } else {
            say("Here are the results:");
        }
        return totalHits;
    }

    private void say(String s) {
        System.out.println(s);
    }
}
