package com.example.zoneout.model;

import android.location.Location;

import java.util.ArrayList;

public class Post implements Comparable{
    private static int id = 0;
    private ArrayList<Integer> images;
    private ArrayList<Integer> videos;
    private Location location;
    private int lat;
    private int lgt;
    private String description;
    private String ownerStr;
    private String title;
    private int rating;



    /*public Post(ArrayList<Integer> images, String description, float rating, User owner) {
        this.id += 1;
        this.images = images;
        this.description = description;
        this.rating = rating;
        this.owner = owner;
    }*/

    public Post(String description, String owner, String title){
        this.id += 1;
        this.description = description;
        this.ownerStr = owner;
        this.title = title;
        this.lat = 5;
        this.lgt = 45;
    }

    public Post(String description, String owner, int rating, String title) {
        this.id += 1;
        this.description = description;
        this.rating = rating;
        this.ownerStr = owner;
        this.title = title;
    }

    public ArrayList<Integer> getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public void addImage(int id){
        images.add(id);
    }

    public int getRating(){
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getOwner() {
        return ownerStr;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLat(){
        return lat;
    }

    public long getLgt(){
        return lgt;
    }

    @Override
    public int compareTo(Object o) {
        int compRating = ((Post) o).getRating();

        return compRating - this.rating;
    }
}
