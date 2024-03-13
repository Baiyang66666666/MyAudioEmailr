package com.com6103.email.entity;

public class TTSRequest {
    String emailId;
    String content;

    String voiceType;

    public TTSRequest(String emailId, String content, String voiceType) {
        this.emailId = emailId;
        this.content = content;
        this.voiceType = voiceType;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }
}


