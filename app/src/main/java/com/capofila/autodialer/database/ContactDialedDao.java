package com.capofila.autodialer.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ContactDialedDao {
    @Insert
    public void insert(ContactDialed contactDialed);

    @Query("SELECT * from dialed_list")
    public LiveData<List<ContactDialed>> getAllDialedContacts();
}
