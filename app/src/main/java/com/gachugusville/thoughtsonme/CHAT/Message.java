package com.gachugusville.thoughtsonme.CHAT;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {

    private int DELIVERED_READ_STATUS;
    private int REACTION_CODE;
    private String sender_name;
    private Message text_replied_to;
    private String message_id, sender_id, message_content, deleted;
    private List<String> media_urls;

    public Message() {
        //Needed
    }

    public Message(int DELIVERED_READ_STATUS, int REACTION_CODE, Message text_replied_to, String sender_name, String message_id, String sender_id, String message_content, String deleted, List<String> media_urls) {
        this.DELIVERED_READ_STATUS = DELIVERED_READ_STATUS;
        this.REACTION_CODE = REACTION_CODE;
        this.text_replied_to = text_replied_to;
        this.sender_name = sender_name;
        this.deleted = deleted;
        this.message_id = message_id;
        this.sender_id = sender_id;
        this.message_content = message_content;
        this.media_urls = media_urls;
    }


    public int getDELIVERED_READ_STATUS() {
        return DELIVERED_READ_STATUS;
    }

    public void setDELIVERED_READ_STATUS(int DELIVERED_READ_STATUS) {
        this.DELIVERED_READ_STATUS = DELIVERED_READ_STATUS;
    }

    public int getREACTION_CODE() {
        return REACTION_CODE;
    }

    public void setREACTION_CODE(int REACTION_CODE) {
        this.REACTION_CODE = REACTION_CODE;
    }

    public Message getText_replied_to() {
        return text_replied_to;
    }

    public void setText_replied_to(Message text_replied_to) {
        this.text_replied_to = text_replied_to;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public List<String> getMedia_urls() {
        return media_urls;
    }

    public void setMedia_urls(List<String> media_urls) {
        this.media_urls = media_urls;
    }

}
