package com.dewarim.reddit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 */
public class Indexer {

    File        indexDir;
    List<File>  inputFiles;
    boolean     appendToIndex;
    IndexWriter indexWriter;

    public Indexer(File indexDir, List<File> inputFiles, boolean appendToIndex) {
        this.indexDir = indexDir;
        this.inputFiles = inputFiles;
        this.appendToIndex = appendToIndex;
    }

    public void startIndexing() throws IOException {
        Directory dir = FSDirectory.open(indexDir.toPath());
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        if (appendToIndex) {
            config.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
        }
        config.setCommitOnClose(true);
        indexWriter = new IndexWriter(dir, config);
        for (File inputFile : inputFiles) {
            System.out.println("\nIndexing: " + inputFile);
            indexAllTheThings(indexWriter, inputFile);
        }
        indexWriter.close();
    }

    private void indexAllTheThings(IndexWriter indexWriter, File data) {
        DataReader dataReader = new DataReader(data, new LuceneCommentConsumer(indexWriter));
        dataReader.parseFile();
    }

}
