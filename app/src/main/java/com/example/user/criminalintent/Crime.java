package com.example.user.criminalintent;

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

    public Crime() {
        //generate unique identifier
        this.id = UUID.randomUUID();
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

}
