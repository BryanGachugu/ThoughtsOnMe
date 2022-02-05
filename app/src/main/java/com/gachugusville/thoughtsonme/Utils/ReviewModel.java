package com.gachugusville.thoughtsonme.Utils;

import java.io.Serializable;
import java.util.List;

public class ReviewModel implements Serializable {

    public String review_id, publisherUid, publisher_name, publisher_profile_url, target_name, targetUID, target_pic_url, review_topic, review_content;
    public List<String> target_phone_numbers;
    public List<String> target_nicknames;
    public List<ReviewReply> reviewReplies;
    boolean showDetails, ageRestricted;
    private double rating;
    private int num_seconders, num_disapproval;

    public ReviewModel() {
        //Needed for firebase
    }

    public ReviewModel(String review_id, String publisherUid, String publisher_name, String publisher_profile_url, String target_name, String targetUID, String target_pic_url, String review_topic, String review_content, List<String> target_phone_numbers,
                       List<String> target_nicknames, List<ReviewReply> reviewReplies, boolean showDetails, boolean ageRestricted, double rating, int num_seconders, int num_disapproval) {
        this.review_id = review_id;
        this.publisherUid = publisherUid;
        this.publisher_name = publisher_name;
        this.publisher_profile_url = publisher_profile_url;
        this.target_name = target_name;
        this.targetUID = targetUID;
        this.target_pic_url = target_pic_url;
        this.review_topic = review_topic;
        this.review_content = review_content;
        this.target_phone_numbers = target_phone_numbers;
        this.target_nicknames = target_nicknames;
        this.reviewReplies = reviewReplies;
        this.showDetails = showDetails;
        this.ageRestricted = ageRestricted;
        this.rating = rating;
        this.num_seconders = num_seconders;
        this.num_disapproval = num_disapproval;
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public String getPublisherUid() {
        return publisherUid;
    }

    public void setPublisherUid(String publisherUid) {
        this.publisherUid = publisherUid;
    }

    public String getPublisher_name() {
        return publisher_name;
    }

    public void setPublisher_name(String publisher_name) {
        this.publisher_name = publisher_name;
    }

    public String getPublisher_profile_url() {
        return publisher_profile_url;
    }

    public void setPublisher_profile_url(String publisher_profile_url) {
        this.publisher_profile_url = publisher_profile_url;
    }

    public String getTarget_name() {
        return target_name;
    }

    public void setTarget_name(String target_name) {
        this.target_name = target_name;
    }

    public String getTargetUID() {
        return targetUID;
    }

    public void setTargetUID(String targetUID) {
        this.targetUID = targetUID;
    }

    public String getTarget_pic_url() {
        return target_pic_url;
    }

    public void setTarget_pic_url(String target_pic_url) {
        this.target_pic_url = target_pic_url;
    }

    public String getReview_topic() {
        return review_topic;
    }

    public void setReview_topic(String review_topic) {
        this.review_topic = review_topic;
    }

    public String getReview_content() {
        return review_content;
    }

    public void setReview_content(String review_content) {
        this.review_content = review_content;
    }

    public List<String> getTarget_phone_numbers() {
        return target_phone_numbers;
    }

    public void setTarget_phone_numbers(List<String> target_phone_numbers) {
        this.target_phone_numbers = target_phone_numbers;
    }

    public List<String> getTarget_nicknames() {
        return target_nicknames;
    }

    public void setTarget_nicknames(List<String> target_nicknames) {
        this.target_nicknames = target_nicknames;
    }

    public List<ReviewReply> getReviewReplies() {
        return reviewReplies;
    }

    public void setReviewReplies(List<ReviewReply> reviewReplies) {
        this.reviewReplies = reviewReplies;
    }

    public boolean isShowDetails() {
        return showDetails;
    }

    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }

    public boolean isAgeRestricted() {
        return ageRestricted;
    }

    public void setAgeRestricted(boolean ageRestricted) {
        this.ageRestricted = ageRestricted;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    public int getNum_seconders() {
        return num_seconders;
    }

    public void setNum_seconders(int num_seconders) {
        this.num_seconders = num_seconders;
    }

    public int getNum_disapproval() {
        return num_disapproval;
    }

    public void setNum_disapproval(int num_disapproval) {
        this.num_disapproval = num_disapproval;
    }
}
