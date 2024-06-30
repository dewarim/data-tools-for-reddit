package com.dewarim.reddit.tools.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "comments")
public class Comment {

    @Id
    private String id;
    private int score;
    @Column(name = "author_flair_css_class")
    private String authorFlairCssClass;
    @Column(name="created_utc")
    private long createdUtc;
    private int ups;
    private int downs;
    private boolean archived;
    @Column(name="link_id")
    private String linkId;
    @Column(name="score_hidden")
    private boolean scoreHidden;
    private boolean gilded;
    private String author;
    private String subreddit;
    @Column(name="subreddit_id")
    private String subredditId;
    @Column(name="author_flair_text")
    private String authorFlairText;
    private String distinguished;
    private String name;
    @Column(name="retrieved_on")
    private long retrievedOn;
    @Column(length = 65535)
    private String body;
    @Column(name="is_submitter")
    private boolean isSubmitter;
    private boolean locked;
    @Column(length = 1024)
    private String permalink;
    @Column(name="parent_id")
    private String parentId;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAuthorFlairCssClass() {
        return authorFlairCssClass;
    }

    public void setAuthorFlairCssClass(String authorFlairCssClass) {
        this.authorFlairCssClass = authorFlairCssClass;
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(long createdUtc) {
        this.createdUtc = createdUtc;
    }

    public int getUps() {
        return ups;
    }

    public void setUps(int ups) {
        this.ups = ups;
    }

    public int getDowns() {
        return downs;
    }

    public void setDowns(int downs) {
        this.downs = downs;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public boolean isScoreHidden() {
        return scoreHidden;
    }

    public void setScoreHidden(boolean scoreHidden) {
        this.scoreHidden = scoreHidden;
    }

    public boolean isGilded() {
        return gilded;
    }

    public void setGilded(boolean gilded) {
        this.gilded = gilded;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getSubredditId() {
        return subredditId;
    }

    public void setSubredditId(String subredditId) {
        this.subredditId = subredditId;
    }

    public String getAuthorFlairText() {
        return authorFlairText;
    }

    public void setAuthorFlairText(String authorFlairText) {
        this.authorFlairText = authorFlairText;
    }

    public String getDistinguished() {
        return distinguished;
    }

    public void setDistinguished(String distinguished) {
        this.distinguished = distinguished;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRetrievedOn() {
        return retrievedOn;
    }

    public void setRetrievedOn(long retrievedOn) {
        this.retrievedOn = retrievedOn;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSubmitter() {
        return isSubmitter;
    }

    public void setSubmitter(boolean submitter) {
        isSubmitter = submitter;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", score=" + score +
                ", authorFlairCssClass='" + authorFlairCssClass + '\'' +
                ", createdUtc=" + createdUtc +
                ", ups=" + ups +
                ", downs=" + downs +
                ", archived=" + archived +
                ", linkId='" + linkId + '\'' +
                ", scoreHidden=" + scoreHidden +
                ", gilded=" + gilded +
                ", author='" + author + '\'' +
                ", subreddit='" + subreddit + '\'' +
                ", subredditId='" + subredditId + '\'' +
                ", authorFlairText='" + authorFlairText + '\'' +
                ", distinguished='" + distinguished + '\'' +
                ", name='" + name + '\'' +
                ", retrievedOn=" + retrievedOn +
                ", body='" + body + '\'' +
                ", isSubmitter=" + isSubmitter +
                ", locked=" + locked +
                ", permalink='" + permalink + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}
