package com.dewarim.reddit;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.File;

/**
 */
public class Search {

    public static void main(String[] args) throws Exception {
        String indexDir = System.getProperty("reddit.index", "F:/reddit_data/index-all");
        System.out.println("Opening search index at " + indexDir + ". This may take a moment.");
        Searcher searcher = new Searcher(new File(indexDir), "body");
        System.out.println("Going to search over " + searcher.countDocuments() + " documents.");

        long start = System.currentTimeMillis();
        if (args.length > 0) {
            for (String query : args) {
                searcher.search(query);
            }
        } else {
            BooleanQuery query = new BooleanQuery();
            query.add(new TermQuery(new Term("body", "love")), BooleanClause.Occur.MUST);
            query.add(new TermQuery(new Term("body", "story")), BooleanClause.Occur.MUST);
            query.add(new TermQuery(new Term("body", "twilight")), BooleanClause.Occur.MUST);
            query.add(NumericRangeQuery.newIntRange("ups", 4, 1000, 10000, true, true), BooleanClause.Occur.MUST);
            searcher.search(query);
        }
        System.out.println("Search took " + (System.currentTimeMillis() - start) + " ms");
    }

}
