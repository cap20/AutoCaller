package com.capofila.autodialer;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.capofila.autodialer.contactHistory.CallHistory;
import com.capofila.autodialer.contactList.ContactAdapter;
import com.capofila.autodialer.database.ContactDialed;
import com.capofila.autodialer.database.ContactEntity;
import com.capofila.autodialer.database.ContactViewModel;
import com.capofila.autodialer.importAndExport.MyCSVFileReader;
import com.capofila.autodialer.setting.Settings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private List<ContactEntity> mContactsList = new ArrayList<>();
    private ContactAdapter mAdapter;
    private ContactEntity c;
    private ContactViewModel mContactViewModel;
    private List<ContactEntity> contacts = new ArrayList<>();
    private int j = 0;
    private String callTime;
    TextView mCountDownTimer;
    private Button posButton;
    private boolean showCommentDialog;

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importCSV();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initRecyclerView();

        mContactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        mContactViewModel.getAllContacts().observe(this, new Observer<List<ContactEntity>>() {
            @Override
            public void onChanged(@Nullable List<ContactEntity> contactEntities) {
                mContactsList = contactEntities;
                mAdapter.setContacts(contactEntities);
            }
        });

        mAdapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mContactsList.get(position);
            }

            @Override
            public void onCallClick(int position) {
                ContactEntity c = mContactsList.get(position);
                Log.d(TAG, "onCallClick: call btn in each card " + c.getId() + "\n" + c.getPersonContactNumber());
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + c.getPersonContactNumber()));
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        callTime = sharedPreferences.getString("button_timeout", "5000");
        showCommentDialog = sharedPreferences.getBoolean("comment_dialog", true);


        SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                callTime = sharedPreferences.getString("button_timeout", "5000");
                showCommentDialog = sharedPreferences.getBoolean("comment_dialog", true);
                Log.d(TAG, "onSharedPreferenceChanged: call Time" + callTime);
                Log.d(TAG, "onSharedPreferenceChanged: showCommentDialog" + showCommentDialog);
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }



    private void initRecyclerView() {
        RecyclerView mContactRecyclerView = findViewById(R.id.contactList);
        mAdapter = new ContactAdapter();
        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mContactRecyclerView.setHasFixedSize(true);
        mContactRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(MainActivity.this, Settings.class);
            startActivity(settingIntent);
        }
        if (id == R.id.start_call) {
            startAutoCall();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAutoCall() {

        final CountDownTimer countDownTimer;
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View countDownLayout = layoutInflater.inflate(R.layout.timer_dialog, null);

        final AlertDialog.Builder countDownAlertBuilder = new AlertDialog.Builder(MainActivity.this);
        countDownAlertBuilder.setTitle("Call Will Start In");
        countDownAlertBuilder.setView(countDownLayout);
        mCountDownTimer = countDownLayout.findViewById(R.id.timerText);

        posButton = countDownLayout.findViewById(R.id.pos_btn);
        Button negButton = countDownLayout.findViewById(R.id.neg_btn);

        //creating alert Dialog
        final AlertDialog alertDialog = countDownAlertBuilder.create();

        if (mContactsList.isEmpty()) {
            importContactToast();
        } else {
            alertDialog.show();
            long millis = Long.parseLong(callTime);
            Log.d(TAG, "startAutoCall: COUNT DOWN TIMER : TIME IN LONG " + millis);
            countDownTimer = new CountDownTimer(millis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long timer = millisUntilFinished / 1000;
                    mCountDownTimer.setText("Call Will Start in" + timer);
                }

                @Override
                public void onFinish() {
                    /*
                     * If the @CountDownTimer finished this method execute automatically..
                     * */
                    posButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            executePositiveButton(alertDialog);
                        }
                    });
                    posButton.performClick();
                }
            }.start();

            negButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countDownTimer.cancel();
                    alertDialog.dismiss();
                }
            });
            /**
             *If the call now button is manually tapped this code will execute
             */
            posButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countDownTimer.cancel();
                    executePositiveButton(alertDialog);
                }
            });
        }
    }

    // positive button execution code is here..
    private void executePositiveButton(AlertDialog alertDialog) {
        autoCallDialog();
        alertDialog.dismiss();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_setting) {
            Intent settingIntent = new Intent(MainActivity.this, Settings.class);
            startActivity(settingIntent);
        }
        if (id == R.id.call_history) {
            Toast.makeText(MainActivity.this, "call his", Toast.LENGTH_LONG).show();
            Intent callHistoryIntent = new Intent(MainActivity.this, CallHistory.class);
            startActivity(callHistoryIntent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                break;
        }
    }

    private void importCSV() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                Log.d("Import", "onNavigationItemSelected: import clicked");
                MyCSVFileReader.openDialogToReadCSV(MainActivity.this, MainActivity.this);
            } else {

                Log.d("Import", "onNavigationItemSelected: import clicked");
                MyCSVFileReader.openDialogToReadCSV(MainActivity.this, MainActivity.this);
            }
        }
    }

    private void autoCallDialog() {
        if (mContactsList.isEmpty()) {
            importContactToast();
        } else {
            ContactEntity contactEntity1 = mContactsList.get(j);
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + contactEntity1.getPersonContactNumber()));
            startActivity(intent);
            mContactViewModel.insertDialedContact(new ContactDialed(contactEntity1.getPersonName(), contactEntity1.getPersonContactNumber()));
            mContactViewModel.deleteById(contactEntity1);
            mContactsList.remove(j);

            if(showCommentDialog){
                showCommentDialog();
            }else{
                afterFirstCall();
            }
        }
    }

    private void showCommentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.comment_dialog_title);
        builder.setPositiveButton(R.string.comment_post_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: comment posted");
                afterFirstCall();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void afterFirstCall(){
        if (mContactsList.isEmpty()) {
            importContactToast();
        } else {
            c = mContactsList.get(j);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.next_call_dialog_title);
        builder.setMessage("To call next number press next call \n To Pause Click cancel");
        builder.setPositiveButton(R.string.next_call_dialog_title, null);
        builder.setNegativeButton(R.string.alert_dialog_cancel_btn_text, null);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button negBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                final Button posBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                posBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + c.getPersonContactNumber()));
                        startActivity(intent);

                        if(showCommentDialog){
                            showCommentDialog();
                        }

                        mContactViewModel.insertDialedContact(new ContactDialed(c.getPersonName(), c.getPersonContactNumber()));
                        mContactViewModel.deleteById(c);
                        mContactsList.remove(j);
                        Log.i(TAG, "calling on id " + c.getId());

                        if(mContactsList.isEmpty()) {
                            importContactToast();
                            dialog.dismiss();
                        } else {
                            c = mContactsList.get(j);
                        }
                    }
                });

                negBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void importContactToast() {
        Toast.makeText(MainActivity.this, "No Contact Found!! Import Contacts", Toast.LENGTH_LONG).show();
    }
}



