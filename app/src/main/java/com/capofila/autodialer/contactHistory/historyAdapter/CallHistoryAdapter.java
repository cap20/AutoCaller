package com.capofila.autodialer.contactHistory.historyAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.capofila.autodialer.R;
import com.capofila.autodialer.database.ContactDialed;
import java.util.ArrayList;
import java.util.List;

public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryViewHolder> {

    List<ContactDialed> mContactDialed = new ArrayList<>();

    @NonNull
    @Override
    public CallHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.call_history_list,viewGroup,false);
        return new CallHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallHistoryViewHolder callHistoryViewHolder, int i) {
        ContactDialed currentDialed = mContactDialed.get(i);

        callHistoryViewHolder.mPersonName.setText(currentDialed.getPersonName());
        callHistoryViewHolder.mPersonContact.setText(currentDialed.getPersonContact());

    }

    @Override
    public int getItemCount() {
        return mContactDialed.size();
    }

    public void setContact(List<ContactDialed> contactDialed){
        this.mContactDialed = contactDialed;
        notifyDataSetChanged();
    }
}
