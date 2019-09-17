package com.capofila.autodialer.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ContactRepository {
    private ContactDao mContactDao;
    private LiveData<List<ContactEntity>> mAllContacts;

    ContactRepository(Application application){
        ContactRoomDatabase db = ContactRoomDatabase.getDatabase(application);
        mContactDao = db.contactDao();
        mAllContacts = mContactDao.getAllContacts();
    }

    public LiveData<List<ContactEntity>> getAllContacts(){
        return mAllContacts;
    }

    public void insert(ContactEntity contactEntity){
        new InsertAsyncTask(mContactDao).execute(contactEntity);
    }

    public void delete(ContactEntity contactEntity){

    }

    public static class InsertAsyncTask extends AsyncTask<ContactEntity, Void, Void>{

        private ContactDao mAsyncTaskDao;

        public InsertAsyncTask(ContactDao mAsyncTaskDao) {
            this.mAsyncTaskDao = mAsyncTaskDao;
        }

        @Override
        protected Void doInBackground(ContactEntity... contactEntities) {
            mAsyncTaskDao.insert(contactEntities[0]);
            return null;
        }
    }
}
