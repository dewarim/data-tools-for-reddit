package com.dewarim.reddit;

/**
 * A Reddit comment.
 */
public class Comment {

    public String  author;
    public String  name;
    public String  body;
    public String  authorFlairText;
    public Integer gilded;
    public Boolean scoreHidden;
    public Integer score;
    public String  linkId;
    public Long    retrievedOn;
    public String  authorFlairCssClass;
    public String  subreddit;
    public String  edited;
    public Integer ups;
    public Integer downs;
    public Integer controversiality;
    public Long    createdUtc;
    public String  parentId;
    public Boolean archived;
    public String  subredditId;
    public String  id;
    public String  distinguished;

    @Override
    public String toString() {
        return "Comment{" +
                "author='" + author + '\'' +
                ", name='" + name + '\'' +
                ", body='" + body + '\'' +
                ", authorFlairText='" + authorFlairText + '\'' +
                ", gilded=" + gilded +
                ", scoreHidden=" + scoreHidden +
                ", score=" + score +
                ", linkId='" + linkId + '\'' +
                ", retrievedOn=" + retrievedOn +
                ", authorFlairCssClass='" + authorFlairCssClass + '\'' +
                ", subreddit='" + subreddit + '\'' +
                ", edited=" + edited +
                ", ups=" + ups +
                ", downs=" + downs +
                ", controversiality=" + controversiality +
                ", createdUtc=" + createdUtc +
                ", parentId='" + parentId + '\'' +
                ", archived=" + archived +
                ", subredditId='" + subredditId + '\'' +
                ", id='" + id + '\'' +
                ", distinguished='" + distinguished + '\'' +
                '}';
    }
}
