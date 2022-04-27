package com.example.zoneout.model;

import java.util.ArrayList;

public class User {
    private String name;
    private String email;
    private String photo;
    private ArrayList<Post> visitedPosts;
    private ArrayList<Post> trips;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name) {
        this.name = name;
        this.visitedPosts = visitedPosts;
    }

    public User(){
        this.name = "hysa";
        this.email = "hysa.email";
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public ArrayList<Post> getVisitedPosts() {
        return visitedPosts;
    }

    public ArrayList<Post> getTrips() {
        return trips;
    }
}
