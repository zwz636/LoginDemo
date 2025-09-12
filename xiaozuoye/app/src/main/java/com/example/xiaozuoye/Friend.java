package com.example.xiaozuoye;

public class Friend {
    private String name;
    private String status;
    private int avatarId;

    public Friend(String name, String status, int avatarId) {
        this.name = name;
        this.status = status;
        this.avatarId = avatarId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public int getAvatarId() {
        return avatarId;
    }
}