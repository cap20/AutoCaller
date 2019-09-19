package com.capofila.autodialer.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "dialed_list")
public class ContactDialed {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "person_name")
    private String personName;

    @ColumnInfo(name="person_contact")
    private String personContact;

    public ContactDialed(String personName, String personContact) {
        this.personName = personName;
        this.personContact = personContact;
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

    public String getPersonContact() {
        return personContact;
    }
}
