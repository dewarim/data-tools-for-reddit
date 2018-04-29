package com.dewarim.reddit;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class Search {

    public static void main(String[] args) throws Exception {
        Args       cliArguments = new Args();
        JCommander commander    = JCommander.newBuilder().addObject(cliArguments).build();
        commander.parse(args);

        if ((cliArguments.help)) {
            commander.setColumnSize(80);
            commander.usage();
            return;
        }

        if (cliArguments.queryStrings.isEmpty()) {
            System.out.println("You have supplied no query strings like '-q reddit -q gold'.");
            return;
        }

        String indexDir = cliArguments.luceneDir;
        System.out.println("Opening search index at " + indexDir + ". This may take a moment.");
        Searcher searcher = new Searcher(new File(indexDir), "body");
        System.out.println("Going to search over " + searcher.countDocuments() + " documents.");

        long start = System.currentTimeMillis();
        BooleanQuery query = new BooleanQuery();
        cliArguments.queryStrings.forEach(
                q -> query.add(new TermQuery(new Term("body", q)), BooleanClause.Occur.MUST)
        );
        query.add(NumericRangeQuery.newIntRange("ups", 4, cliArguments.minUps, cliArguments.maxUps, true, true), BooleanClause.Occur.MUST);
        searcher.search(query);
        System.out.println("Search took " + (System.currentTimeMillis() - start) + " ms");
    }

    private static class Args {
        @Parameter(names = "--luceneDir", description = "Directory where to put the index-all folder containing the Lucene index. Default: data/index-all")
        String luceneDir = "data/index-all";

        @Parameter(names = "--min-ups", description = "Minimum number of upvotes, default 1000")
        int minUps = 1000;

        @Parameter(names = "--max-ups", description = "Maximum number of upvotes, default 100000")
        int maxUps = 100_000;

        @Parameter(names = {"--query", "-q"}, description = "Search strings that must occur in the comment body")
        List<String> queryStrings = new ArrayList<>();

        @Parameter(names = {"--help", "-h"}, help = true, description = "Display help text.")
        boolean help;
    }

}
