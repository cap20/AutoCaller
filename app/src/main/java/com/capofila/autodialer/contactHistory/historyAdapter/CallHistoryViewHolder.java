package com.capofila.autodialer.contactHistory.historyAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.capofila.autodialer.R;

public class CallHistoryViewHolder extends RecyclerView.ViewHolder {

    TextView mPersonName, mPersonContact;

    public CallHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        mPersonContact = itemView.findViewById(R.id.contacted_contact_no);
        mPersonName = itemView.findViewById(R.id.contacted_person_name);
    }
}
