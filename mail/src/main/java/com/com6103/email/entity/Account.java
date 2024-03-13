package com.com6103.email.entity;

public class Account{
    private String userId;
    private String username;
    private String address;
    private String password;
    private String smtp;
    private String imap;
    private String accountType;
    private String deviceId;

    public Account(){

    }

    public Account(String address, String password, String smtp, String imap) {
        this.address = address;
        this.password = password;
        this.smtp = smtp;
        this.imap = imap;
    }

    public Account(String username, String address, String password, String accountType, String smtp, String imap, String deviceId) {
        this.username = username;
        this.address = address;
        this.password = password;
        this.accountType = accountType;
        this.smtp = smtp;
        this.imap = imap;
        this.deviceId = deviceId;
    }

    public Account(String userId, String username, String address, String password, String accountType, String smtp, String imap, String deviceId) {
        this.username = username;
        this.address = address;
        this.password = password;
        this.accountType = accountType;
        this.smtp = smtp;
        this.imap = imap;
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getSmtp() {
        return smtp;
    }

    public void setSmtp(String smtp) {
        this.smtp = smtp;
    }

    public String getImap() {
        return imap;
    }

    public void setImap(String imap) {
        this.imap = imap;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
