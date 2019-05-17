package com.appchat.model;

public class WelcomeModel {

    private String member1;
    private String member2;
    private String content;

    public WelcomeModel(String member1, String member2, String content) {
        this.member1 = member1;
        this.member2 = member2;
        this.content = content;
    }

    public String getMember1() {
        return member1;
    }

    public void setMember1(String member1) {
        this.member1 = member1;
    }

    public String getMember2() {
        return member2;
    }

    public void setMember2(String member2) {
        this.member2 = member2;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
