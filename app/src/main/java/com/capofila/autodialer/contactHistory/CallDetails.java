package com.capofila.autodialer.contactHistory;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.capofila.autodialer.R;
import static com.capofila.autodialer.contactHistory.CallHistory.CALL_COMMENT;
import static com.capofila.autodialer.contactHistory.CallHistory.CONTACT_NAME;
import static com.capofila.autodialer.contactHistory.CallHistory.CONTACT_NUMBER;

public class CallDetails extends AppCompatActivity {

    private TextView mNameTextView, mContactTextView, mCommentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_call_history);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0f);
        }

        mNameTextView = findViewById(R.id.name_text);
        mCommentTextView = findViewById(R.id.comment_text);
        mContactTextView = findViewById(R.id.contact_number_text);

        Intent intent = getIntent();

        String name = intent.getStringExtra(CONTACT_NAME);
        String contactNumber = intent.getStringExtra(CONTACT_NUMBER);
        String comment = intent.getStringExtra(CALL_COMMENT);

        mNameTextView.setText(name);
        mContactTextView.setText(contactNumber);
        mCommentTextView.setText(comment);

    }
}
