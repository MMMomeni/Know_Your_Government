package com.mmomeni.know_your_government;

import androidx.annotation.NonNull;

import java.io.Serializable;



public class Official implements Serializable{ //if we are going to pass this object over threads, we need to implement serializable
            //in order to be able to use Collection.sort() we have to implement comparable and the function to explain in case of sorting what need to
            //be compared to what
    private String location;
    private String officeName;
    private String name;
    private String address;
    private String party;
    private String phone;
    private String url;
    private String email;
    private String photoURL;
    private Channel channel;




    Official(String location, String officeName, String name, String address, String party,
             String phone, String url, String email, String photoURL, Channel channel) {
       this.location = location;
       this.officeName = officeName;
       this.name = name;
       this.address = address;
       this.party = party;
       this.phone = phone;
       this.url = url;
       this.email = email;
       this.photoURL = photoURL;
       this.channel = channel;

    }

    public String getLocation() {
        return location;
    }
    public String getOfficeName() {
        return officeName;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getParty() {
        return party;
    }
    public String getPhone() {
        return phone;
    }
    public String getURL() {
        return url;
    }
    public String getEmail() {
        return email;
    }
    public String getPhotoURL() {
        return photoURL;
    }
    public Channel getChannel() {
        return channel;
    }





}
