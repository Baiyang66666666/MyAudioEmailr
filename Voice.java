package com.com6103.email.entity;

import javax.xml.crypto.Data;

public class Voice {
    String tts_id;
    String email_id;
    String content;
    String createby;

    public Voice(){

    }

    public Voice(String tts_id, String email_id, String content, String createby) {
        this.tts_id = tts_id;
        this.email_id = email_id;
        this.content = content;
        this.createby = createby;
    }

    public String getTts_id() {
        return tts_id;
    }

    public void setTts_id(String tts_id) {
        this.tts_id = tts_id;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateby() {
        return createby;
    }

    public void setCreateby(String createby) {
        this.createby = createby;
    }
}
