package com.dewarim.reddit.tools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.CollectionId;

@Entity
public class Submission {

    @Id
    private String id;
    @Column(length = 1024)
    private String url;
    private String subreddit;
    private String title;
    @Column(length = 1024)
    private String permalink;
    @JsonProperty("over_18")
    private boolean over18;
    private boolean deleted;
    private int score;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public boolean isOver18() {
        return over18;
    }

    public void setOver18(boolean over18) {
        this.over18 = over18;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "url='" + url + '\'' +
                ", subreddit='" + subreddit + '\'' +
                ", title='" + title + '\'' +
                ", permalink='" + permalink + '\'' +
                ", over18=" + over18 +
                ", score=" + score +
                ", deleted=" + deleted +
                '}';
    }
}
