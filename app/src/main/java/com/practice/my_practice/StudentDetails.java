package com.practice.my_practice;

public class StudentDetails {
    String Name, Email, Degree, Level, Url;

    public StudentDetails() {
    }

    public StudentDetails(String name, String email, String degree, String level, String url) {
        Name = name;
        Email = email;
        Degree = degree;
        Level = level;
        Url = url;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDegree() {
        return Degree;
    }

    public void setDegree(String degree) {
        Degree = degree;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
