package com.capofila.autodialer.contactHistory.historyAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capofila.autodialer.R;
import com.capofila.autodialer.database.ContactDialed;

import java.util.ArrayList;
import java.util.List;

public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.CallHistoryViewHolder> {

    List<ContactDialed> mContactDialed = new ArrayList<>();
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onClick(ContactDialed contactDialed);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    public class CallHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView mPersonName, mPersonContact;

        public CallHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mPersonContact = itemView.findViewById(R.id.contacted_contact_no);
            mPersonName = itemView.findViewById(R.id.contacted_person_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mListener != null && position != RecyclerView.NO_POSITION) {
                        mListener.onClick(mContactDialed.get(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public CallHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.call_history_list, viewGroup, false);
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

    public void setContact(List<ContactDialed> contactDialed) {
        this.mContactDialed = contactDialed;
        notifyDataSetChanged();
    }
}
