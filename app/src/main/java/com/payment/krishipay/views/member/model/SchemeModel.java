package com.payment.krishipay.views.member.model;

public class SchemeModel {
    private String id;
    private String name;
    private String type;
    private String status;
    private String user_id;

    public SchemeModel(String id, String name, String type, String status, String user_id) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
