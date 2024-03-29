package com.capofila.autodialer.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import java.util.List;


@Dao
public interface ContactDao {

    @Insert
    void insert(ContactEntity contactEntity);

    @Delete
    void delete(ContactEntity contactEntity);

    @Query("SELECT * FROM contact")
    LiveData<List<ContactEntity>> getAllContacts();

    @Query("DELETE FROM contact")
    void deleteAll();

    @Query("DELETE FROM contact WHERE id = :id")
    void deleteById(int id);
}
