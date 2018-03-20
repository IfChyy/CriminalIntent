package com.example.user.criminalintent;

import android.content.ContentValues;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Ivo Georgiev(IfChyy)
 * Crime class holding infromatin for each crime
 * id , title, data , and if it was solved or not
 */

public class Crime {

    private UUID id;
    private String title;
    private Date date;
    private boolean solved;
    public String suspectName;


    public String suspectNumber;

    public Crime() {
        //generate unique identifier
        this.id = UUID.randomUUID();
        this.date = new Date();
    }


    public Crime(UUID id) {
        this.id = id;
        this.date = new Date();

    }
    //--------------------------GETTERS AND SETTERS


    public UUID getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getSuspectName() {
        return suspectName;
    }

    public void setSuspectName(String suspectName) {
        this.suspectName = suspectName;
    }

    public String getSuspectNumber() {
        return suspectNumber;
    }

    public void setSuspectNumber(String suspectNumber) {
        this.suspectNumber = suspectNumber;
    }
    //--------------------------GETTERS AND SETTERS

    // get photoFileName returns the photo image we made saved in the device by the name of
    // IMG and id of our crime uniqie identifier, ending with .jpg
    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }

}
