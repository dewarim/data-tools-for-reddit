package com.dewarim.reddit;

/**
 */
public interface CommentConsumer {

    default void consume(Comment comment ){
        System.out.println(comment);
    }
}
