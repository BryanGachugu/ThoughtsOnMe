package com.gachugusville.thoughtsonme.Utils;

import java.io.Serializable;

public class ReviewReply implements Serializable {

    public String parentCommentID = "", commentReplyID, replying_to_nickname, replying_to_id , publisher_profile_url, publisher_nick_name, comment, media_url;

    public ReviewReply() {
    }

    public ReviewReply( String parentCommentID, String commentReplyID, String replying_to_nickname, String replying_to_id, String publisher_profile_url, String publisher_nick_name, String comment, String media_url) {
        this.parentCommentID = parentCommentID;
        this.replying_to_nickname = replying_to_nickname;
        this.replying_to_id = replying_to_id;
        this.commentReplyID = commentReplyID;
        this.publisher_profile_url = publisher_profile_url;
        this.publisher_nick_name = publisher_nick_name;
        this.comment = comment;
        this.media_url = media_url;
    }

    public String getParentCommentID() {
        return parentCommentID;
    }

    public void setParentCommentID(String parentCommentID) {
        this.parentCommentID = parentCommentID;
    }

    public String getCommentReplyID() {
        return commentReplyID;
    }

    public void setCommentReplyID(String commentReplyID) {
        this.commentReplyID = commentReplyID;
    }

    public String getReplying_to_nickname() {
        return replying_to_nickname;
    }

    public void setReplying_to_nickname(String replying_to_nickname) {
        this.replying_to_nickname = replying_to_nickname;
    }

    public String getReplying_to_id() {
        return replying_to_id;
    }

    public void setReplying_to_id(String replying_to_id) {
        this.replying_to_id = replying_to_id;
    }

    public String getPublisher_profile_url() {
        return publisher_profile_url;
    }

    public void setPublisher_profile_url(String publisher_profile_url) {
        this.publisher_profile_url = publisher_profile_url;
    }

    public String getPublisher_nick_name() {
        return publisher_nick_name;
    }

    public void setPublisher_nick_name(String publisher_nick_name) {
        this.publisher_nick_name = publisher_nick_name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

}
