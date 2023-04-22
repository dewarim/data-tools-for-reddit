package com.dewarim.reddit.tools.model;

import java.util.HashMap;
import java.util.Map;

public class ImportStatistics {

    private long              submissions;
    private long              nsfw;
    private long              duration;
    private long              deleted;
    private Map<String, Long> subreddits = new HashMap<>();

    public void incSubmissionCount() {
        submissions = submissions + 1;
    }

    public void incNsfwCount() {
        nsfw = nsfw + 1;
    }

    public void incDeleted() {
        deleted = deleted + 1;
    }

    public void incSubredditCount(String subreddit) {
        Long current = subreddits.getOrDefault(subreddit, 0L);
        subreddits.put(subreddit, current + 1);
    }

    public Long getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Long submissions) {
        this.submissions = submissions;
    }

    public Long getNsfw() {
        return nsfw;
    }

    public void setNsfw(Long nsfw) {
        this.nsfw = nsfw;
    }

    public Map<String, Long> getSubreddits() {
        return subreddits;
    }

    public void setSubreddits(Map<String, Long> subreddits) {
        this.subreddits = subreddits;
    }

    public void setSubmissions(long submissions) {
        this.submissions = submissions;
    }

    public void setNsfw(long nsfw) {
        this.nsfw = nsfw;
    }

    public long getDuration() {
        return duration;
    }

    public long getDeleted() {
        return deleted;
    }

    public void setDeleted(long deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "ImportStatistics{" +
                "submissions=" + submissions +
                ", nsfw=" + nsfw +
                ", duration=" + duration +
                ", deleted=" + deleted +
                ", subreddits=" + subreddits +
                '}';
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
