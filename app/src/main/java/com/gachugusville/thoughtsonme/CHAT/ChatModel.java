package com.gachugusville.thoughtsonme.CHAT;

import com.gachugusville.thoughtsonme.Utils.User;

import java.io.Serializable;
import java.util.List;

public class ChatModel implements Serializable {

    private User his_details;
    int VISIBILITY_STATUS;
    String chat_id, his_uid, first_sender, sender_id, last_message;

    public ChatModel() {
        //Needed
    }

    public ChatModel(User his_details, int VISIBILITY_STATUS, String chat_id, String last_message, String first_sender, String his_uid) {
        this.his_details = his_details;
        this.VISIBILITY_STATUS = VISIBILITY_STATUS;
        this.last_message = last_message;
        this.first_sender = first_sender;
        this.chat_id = chat_id;
        this.his_uid = his_uid;
    }

    public User getHis_details() {
        return his_details;
    }

    public void setHis_details(User his_details) {
        this.his_details = his_details;
    }

    public int getVISIBILITY_STATUS() {
        return VISIBILITY_STATUS;
    }

    public void setVISIBILITY_STATUS(int VISIBILITY_STATUS) {
        this.VISIBILITY_STATUS = VISIBILITY_STATUS;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getFirst_sender() {
        return first_sender;
    }

    public void setFirst_sender(String first_sender) {
        this.first_sender = first_sender;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }
}
