package com.capofila.autodialer.contactHistory;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.capofila.autodialer.R;
import com.capofila.autodialer.contactHistory.historyAdapter.CallHistoryAdapter;
import com.capofila.autodialer.database.ContactDialed;
import com.capofila.autodialer.database.ContactViewModel;
import java.util.List;

public class CallHistory extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CallHistoryAdapter mAdapter;
    private ContactViewModel mContactViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);
        mAdapter = new CallHistoryAdapter();
        mRecyclerView = findViewById(R.id.callHistory_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        mContactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        mContactViewModel.getAllDialedContact().observe(this, new Observer<List<ContactDialed>>() {
            @Override
            public void onChanged(@Nullable List<ContactDialed> contactDialed) {
                mAdapter.setContact(contactDialed);
            }
        });
    }
}
