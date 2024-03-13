package com.com6103.email.entity;


import java.util.Date;

public class AccountSchedule {
    private String schedule_Id;
    private String userId;
    private String schedule_time;
    private String voice_type;

    public String getSchedule_Id() {
        return schedule_Id;
    }

    public void setSchedule_Id(String schedule_Id) {
        this.schedule_Id = schedule_Id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public String getVoice_type() {
        return voice_type;
    }

    public void setVoice_type(String voice_type) {
        this.voice_type = voice_type;
    }
}
