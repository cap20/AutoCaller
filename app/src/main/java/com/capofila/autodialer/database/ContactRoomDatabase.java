package com.capofila.autodialer.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {ContactEntity.class, ContactDialed.class}, version = 2)
public abstract class ContactRoomDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();
    public abstract ContactDialedDao contactDialedDao();

    private static ContactRoomDatabase INSTANCE;

    static ContactRoomDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (ContactRoomDatabase.class) {
                if (INSTANCE == null) {
                     INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ContactRoomDatabase.class, "contact_database")
//                            .addCallback(callback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();

        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>{

        private ContactDao contactDao;
        private ContactDialedDao contactDialedDao;

        public PopulateDbAsync(ContactRoomDatabase db) {
            contactDao = db.contactDao();
            contactDialedDao = db.contactDialedDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            contactDialedDao.insert(new ContactDialed("shubham","9899372603"));
            return null;
        }
    }
}
