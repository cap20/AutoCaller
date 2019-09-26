package com.capofila.autodialer.contactList;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.capofila.autodialer.R;
import com.capofila.autodialer.database.ContactEntity;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private List<ContactEntity> contacts = new ArrayList<>();
    private ArrayList<Contacts> mContactList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onCallClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener mListener){
        this.mListener = mListener;

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_list, viewGroup,false);
        return new ContactViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int i) {
        ContactEntity currentContacts = contacts.get(i);
        contactViewHolder.mContactPersonNameText.setText(currentContacts.getPersonName());
        contactViewHolder.mPersonContactText.setText(currentContacts.getPersonContactNumber());

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<ContactEntity> contacts){
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    public ContactEntity getContactAtPosition(int position){
        return contacts.get(position);
    }
}
