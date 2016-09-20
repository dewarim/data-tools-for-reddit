# Reddit-Data-Tools

Note: this project is in no way an official or endorsed Reddit tool.

Reddit user Stuck_In_The_Matrix has created a very large archive of public Reddit comments
 and put them up for downloading, see: [Thread on Reddit](https://www.reddit.com/r/datasets/comments/3bxlg7/i_have_every_publicly_available_reddit_comment/)
  
I want to create some tools to handle this over 900 GByte of JSON data, therefore I have started with
   some Java classes to parse the comments. This repository contains code to
   
* create a Lucene 5 index (skipping deleted comments, needs 24 hours single-threaded)
* convert the JSON data into CSV (cuts required size in half and is easier to digest for many programs)

Future plans are to create a simple web interface for complex queries 
(for example: >2000 upvotes, must not be in /r/funny or /r/pics, must contain all of (op, surely, deliver), 
must not contain more than one mention of (safe, picture, money) and can be from 2012 or 2013). 
Currently you will have to write such queries in Java (see: Search class to get an idea of how to start).

## Field order of CSV:

    @JsonPropertyOrder(value = {"author", "name", "body", "author_flair_text", "gilded", "score_hidden", "score", "link_id",
            "retrieved_on", "author_flair_css_class", "subreddit", "edited", "ups", "downs", "controversiality",
            "created_utc", "parent_id", "archived", "subreddit_id", "id", "distinguished"})
    
## Fields indexed with Lucene
    
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
   
   STORE.YES means the field is contained in the index and can be shown on a search result page.       

## Input/Output Format

CsvConverter should be called with two parameters, input directory and output directory.
It reads .bz2 and emits .csv.gz files at the moment.
    
    mvn clean package
    # copy target/reddit-1.0-SNAPSHOT-distribution.zip to target machine
    # unzip  unzip reddit-1.0-SNAPSHOT-distribution.zip 
    # cd  reddit-1.0-SNAPSHOT-distribution 
    java -cp reddit-1.0-SNAPSHOT.jar:lib/. com.dewarim.reddit.csv.CsvConverter /home/ingo/reddit_data /home/ingo/reddit_csv    

## License

My code is free to use under the [Apache License](http://www.apache.org/licenses/LICENSE-2.0), version 2.
Contributions will be accepted under the same terms and are welcome.

## Code style

The simplest thing that will work.
 
## Author
 
Ingo Wiarda / ingo_wiarda@dewarim.de /u/Dewarim