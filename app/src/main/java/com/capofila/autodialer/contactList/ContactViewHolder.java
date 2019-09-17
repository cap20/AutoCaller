package com.capofila.autodialer.contactList;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.capofila.autodialer.R;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    public TextView mContactPersonNameText, mPersonContactText, mStatusText, mCallText;

    public ContactViewHolder(@NonNull View itemView, final ContactAdapter.OnItemClickListener mListener) {
        super(itemView);
        mContactPersonNameText = itemView.findViewById(R.id.contact_person_name);
        mPersonContactText = itemView.findViewById(R.id.contact_number);
        mStatusText = itemView.findViewById(R.id.call_status);
        mCallText = itemView.findViewById(R.id.call_btn);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(position);
                    }
                }
            }
        });
        mCallText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onCallClick(position);
                    }
                }
            }
        });
    }
}
