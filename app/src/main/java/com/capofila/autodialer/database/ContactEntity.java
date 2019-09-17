package com.capofila.autodialer.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "contact")
public class ContactEntity {

    @PrimaryKey (autoGenerate = true)
    private int id;

    @ColumnInfo(name = "person_name")
    private String personName;

    @ColumnInfo(name="contact_number")
    private String personContactNumber;


    public ContactEntity(String personName, String personContactNumber) {
        this.personName = personName;
        this.personContactNumber = personContactNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getPersonName() {
        return personName;
    }

    public String getPersonContactNumber() {
        return personContactNumber;
    }
}
