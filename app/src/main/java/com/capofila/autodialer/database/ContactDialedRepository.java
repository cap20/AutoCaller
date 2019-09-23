package com.capofila.autodialer.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ContactDialedRepository {
    private ContactDialedDao mDao;
    private LiveData<List<ContactDialed>> mAllDialedContacts;

    ContactDialedRepository(Application application){
        ContactRoomDatabase db = ContactRoomDatabase.getDatabase(application);
        mDao = db.contactDialedDao();
        mAllDialedContacts = mDao.getAllDialedContacts();
    }

    public LiveData<List<ContactDialed>> getAllContacts(){
        return mAllDialedContacts;
    }

    public void insertDialedContact(ContactDialed contactDialed){
        new InsertDialedAsyncTask(mDao).execute(contactDialed);
    }

    public void update (ContactDialed contactDialed){
        new UpdateDialedAsyncTask(mDao).execute(contactDialed);
    }

    private static class UpdateDialedAsyncTask extends AsyncTask<ContactDialed, Void, Void>{

        ContactDialedDao mDialedDao;

        public UpdateDialedAsyncTask(ContactDialedDao mDialedDao) {
            this.mDialedDao = mDialedDao;
        }

        @Override
        protected Void doInBackground(ContactDialed... contactDialeds) {
            mDialedDao.update(contactDialeds[0]);
            return null;
        }
    }

    private static class InsertDialedAsyncTask extends AsyncTask<ContactDialed, Void, Void>{
        ContactDialedDao mDao;

        public InsertDialedAsyncTask(ContactDialedDao mDao) {
            this.mDao = mDao;
        }

        @Override
        protected Void doInBackground(ContactDialed... contactDialeds) {
            mDao.insert(contactDialeds[0]);
            return null;
        }
    }


}

