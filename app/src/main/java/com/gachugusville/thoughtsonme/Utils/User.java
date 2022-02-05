package com.gachugusville.thoughtsonme.Utils;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private static User user;

    public User() {

    }

    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    String user_doc_id, account_link, user_name, tag_line, user_profile_pic, private_profile_pic, private_name = "";
    List<String> phone_numbers;
    List<String> nicknames;
    List<ReviewModel> reviews;
    boolean premium_account;
    private int num_reviews, total_rating;
    private String user_online_status;
    private int user_age;
    private int positivity_rate;

    public User(String user_doc_id, String account_link, String private_name, String tag_line, String private_profile_pic, String user_name, String user_profile_pic, List<String> phone_numbers, List<String> nicknames, List<ReviewModel> reviews,
                boolean premium_account, int num_reviews, int total_rating, String user_online_status, int user_age, int positivity_rate) {
        this.user_doc_id = user_doc_id;
        this.account_link = account_link;
        this.user_name = user_name;
        this.tag_line = tag_line;
        this.user_profile_pic = user_profile_pic;
        this.private_name = private_name;
        this.private_profile_pic = private_profile_pic;
        this.phone_numbers = phone_numbers;
        this.nicknames = nicknames;
        this.reviews = reviews;
        this.premium_account = premium_account;
        this.num_reviews = num_reviews;
        this.total_rating = total_rating;
        this.user_online_status = user_online_status;
        this.user_age = user_age;
        this.positivity_rate = positivity_rate;
    }

    public String getUser_doc_id() {
        return user_doc_id;
    }

    public void setUser_doc_id(String user_doc_id) {
        this.user_doc_id = user_doc_id;
    }

    public String getAccount_link() {
        return account_link;
    }

    public void setAccount_link(String account_link) {
        this.account_link = account_link;
    }

    public String getPrivate_profile_pic() {
        return private_profile_pic;
    }

    public void setPrivate_profile_pic(String private_profile_pic) {
        this.private_profile_pic = private_profile_pic;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTag_line() {
        return tag_line;
    }

    public void setTag_line(String tag_line) {
        this.tag_line = tag_line;
    }

    public String getUser_profile_pic() {
        return user_profile_pic;
    }

    public void setUser_profile_pic(String user_profile_pic) {
        this.user_profile_pic = user_profile_pic;
    }

    public String getPrivate_name() {
        return private_name;
    }

    public void setPrivate_name(String private_name) {
        this.private_name = private_name;
    }

    public List<String> getPhone_numbers() {
        return phone_numbers;
    }

    public void setPhone_numbers(List<String> phone_numbers) {
        this.phone_numbers = phone_numbers;
    }

    public List<String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(List<String> nicknames) {
        this.nicknames = nicknames;
    }

    public List<ReviewModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewModel> reviews) {
        this.reviews = reviews;
    }

    public boolean isPremium_account() {
        return premium_account;
    }

    public void setPremium_account(boolean premium_account) {
        this.premium_account = premium_account;
    }

    public int getNum_reviews() {
        return num_reviews;
    }

    public void setNum_reviews(int num_reviews) {
        this.num_reviews = num_reviews;
    }

    public int getTotal_rating() {
        return total_rating;
    }

    public void setTotal_rating(int total_rating) {
        this.total_rating = total_rating;
    }

    public String getUser_online_status() {
        return user_online_status;
    }

    public void setUser_online_status(String user_online_status) {
        this.user_online_status = user_online_status;
    }

    public int getUser_age() {
        return user_age;
    }

    public void setUser_age(int user_age) {
        this.user_age = user_age;
    }

    public int getPositivity_rate() {
        return positivity_rate;
    }

    public void setPositivity_rate(int positivity_rate) {
        this.positivity_rate = positivity_rate;
    }
}
