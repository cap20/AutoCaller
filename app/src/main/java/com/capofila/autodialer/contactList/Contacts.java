package com.capofila.autodialer.contactList;

public class Contacts {
    private int id;
    private String personName;
    private String personContactNumber;

    public Contacts(int id, String personName, String personContactNumber) {
        this.id = id;
        this.personName = personName;
        this.personContactNumber = personContactNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonContactNumber() {
        return personContactNumber;
    }

    public void setPersonContactNumber(String personContactNumber) {
        this.personContactNumber = personContactNumber;
    }
}
