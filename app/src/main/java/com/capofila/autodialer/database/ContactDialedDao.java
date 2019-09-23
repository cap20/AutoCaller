package com.capofila.autodialer.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ContactDialedDao {
    @Insert
     void insert(ContactDialed contactDialed);

    @Query("SELECT * from dialed_list")
     LiveData<List<ContactDialed>> getAllDialedContacts();

    @Update
     void update(ContactDialed contactDialed);
}
