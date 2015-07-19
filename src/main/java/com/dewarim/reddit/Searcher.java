package com.dewarim.reddit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

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
            System.out.println("Searching in " + reader.getDocCount(defaultField) + " documents for '" + queryValue + "'");
            Query query = queryParser.parse(queryValue);
            TopDocs topDocs = searcher.search(query, 10);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : hits) {
                Document doc = searcher.doc(scoreDoc.doc);
                say("Score: " + scoreDoc.score + " url: " + doc.get("url")+"\n"+doc.get("body"));
            }
        } catch (ParseException | IOException p) {
            throw new RuntimeException(p);
        }
    }

    private void say(String s) {
        System.out.println(s);
    }
}
