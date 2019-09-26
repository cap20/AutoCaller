package com.capofila.autodialer.contactHistory;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.capofila.autodialer.R;
import com.capofila.autodialer.contactHistory.historyAdapter.CallHistoryAdapter;
import com.capofila.autodialer.database.ContactDialed;
import com.capofila.autodialer.database.ContactViewModel;
import java.util.List;

public class CallHistory extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CallHistoryAdapter mAdapter;
    private ContactViewModel mContactViewModel;
    public static final String CONTACT_NAME = "name";
    public static final String CONTACT_NUMBER = "number";
    public static final String CALL_COMMENT = "comment";

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

        mAdapter.setOnItemClickListener(new CallHistoryAdapter.OnItemClickListener() {
            @Override
            public void onClick(ContactDialed contactDialed) {
                Toast.makeText(CallHistory.this,contactDialed.getComment(),Toast.LENGTH_LONG).show();

                Intent intent = new Intent(CallHistory.this, CallDetails.class);
                intent.putExtra(CONTACT_NAME,contactDialed.getPersonName());
                intent.putExtra(CONTACT_NUMBER,contactDialed.getPersonContact());
                intent.putExtra(CALL_COMMENT,contactDialed.getComment());
                startActivity(intent);


            }
        });

    }
}
