package com.capofila.autodialer.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import java.util.List;

public class ContactViewModel extends AndroidViewModel {

    private ContactRepository mRepository;
    private LiveData<List<ContactEntity>> mAllContacts;
    private ContactDialedRepository mContactDialedRepository;
    private LiveData<List<ContactDialed>> mAllContactDialed;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ContactRepository(application);
        mAllContacts = mRepository.getAllContacts();
        mContactDialedRepository = new ContactDialedRepository(application);
        mAllContactDialed = mContactDialedRepository.getAllContacts();
    }

    public LiveData<List<ContactEntity>> getAllContacts()
    {
        return mAllContacts;
    }

    public void insert(ContactEntity contactEntity){
        mRepository.insert(contactEntity);
    }

    public void delete(ContactEntity contactEntity){
        mRepository.delete(contactEntity);
    }

    public void insertDialedContact(ContactDialed contactDialed){
        mContactDialedRepository.insertDialedContact(contactDialed);
    }

    public void deleteAll(){
        mRepository.deleteAll();
    }

    public void deleteById(ContactEntity contactEntity){
        mRepository.deleteById(contactEntity);
    }

    public LiveData<List<ContactDialed>> getAllDialedContact(){
        return mAllContactDialed;
    }
}
