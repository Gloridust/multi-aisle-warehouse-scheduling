package com.example.warehouse.dto;

public class AccountProfileRequest {
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatarBase64;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarBase64() {
        return avatarBase64;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }
}
