# Reddit-Data-Tools

Reddit user Stuck_In_The_Matrix has created a very large archive of public Reddit comments
 and put them up for downloading, see: [Thread on Reddit](https://www.reddit.com/r/datasets/comments/3bxlg7/i_have_every_publicly_available_reddit_comment/)
  
I want to create some tools to handle this over 900 GByte of JSON data, therefore I have started with
   some Java classes to parse the comments. My plan is to create
   
* a Lucene index
* a public search interface based on Lucene.
   
Not planned: making any of the raw data available via GitHub, since the legal status of distributing the content is 
   somewhat unclear (the comments are public and are made freely available via the Reddit API, but there is no CC-license or such).
   So to work with the data, you must first obtain it via the link above 
  (or download it directly yourself from Reddit via their public API).
   
## License

My code is free to use under the [Apache License](http://www.apache.org/licenses/LICENSE-2.0), version 2.
Contributions will be accepted under the same terms and are welcome.

## Code style

The simplest thing that will work.
 
## Author
 
Ingo Wiarda / ingo_wiarda@dewarim.de 