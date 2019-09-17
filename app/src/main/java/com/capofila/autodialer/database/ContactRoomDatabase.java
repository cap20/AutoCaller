package com.capofila.autodialer.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.ViewOutlineProvider;

@Database(entities = {ContactEntity.class}, version = 1)
public abstract class ContactRoomDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();

    private static   ContactRoomDatabase INSTANCE;

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

        public PopulateDbAsync(ContactRoomDatabase db) {
            contactDao = db.contactDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            contactDao.insert(new ContactEntity("shubham","9899372603"));
            contactDao.insert(new ContactEntity("shubham1","98993726031"));
            contactDao.insert(new ContactEntity("shubham2","98993726032"));
            contactDao.insert(new ContactEntity("shubham3","98993726033"));


            return null;
        }
    }

}
