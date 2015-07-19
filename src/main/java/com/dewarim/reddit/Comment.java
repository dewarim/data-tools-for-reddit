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
    public String  link_id;
    public Long    retrieved_on;
    public String  authorFlairCssClass;
    public String  subreddit;
    public String  edited;
    public Integer ups;
    public Integer downs;
    public Integer controversiality;
    public Long    created_utc;
    public String  parent_id;
    public Boolean archived;
    public String  subreddit_id;
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
                ", link_id='" + link_id + '\'' +
                ", retrieved_on=" + retrieved_on +
                ", authorFlairCssClass='" + authorFlairCssClass + '\'' +
                ", subreddit='" + subreddit + '\'' +
                ", edited=" + edited +
                ", ups=" + ups +
                ", downs=" + downs +
                ", controversiality=" + controversiality +
                ", created_utc=" + created_utc +
                ", parent_id='" + parent_id + '\'' +
                ", archived=" + archived +
                ", subreddit_id='" + subreddit_id + '\'' +
                ", id='" + id + '\'' +
                ", distinguished='" + distinguished + '\'' +
                '}';
    }

    public static String createLink(String subreddit, String linkId, String commentId) {
        if(subreddit == null || linkId == null || commentId == null){
            return "[error]: can only create link from non-null fields.";
        }
        return String.format("http://www.reddit.com/r/%s/comments/%s/%s", subreddit, linkId.replace("t3_", ""), commentId);
    }
}
