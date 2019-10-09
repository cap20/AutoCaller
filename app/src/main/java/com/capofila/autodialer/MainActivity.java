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
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
        implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";
    private List<ContactEntity> mContactsList = new ArrayList<>();
    private ContactAdapter mAdapter;
    private ContactEntity c;
    private ContactViewModel mContactViewModel;
     // private List<ContactEntity> contacts = new ArrayList<>();
    private int j = 0;
    private String callTime;
    TextView mCountDownTimer;
    private Button posButton;
    private boolean showCommentDialog;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    SharedPreferences sharedPreferences;
    String commentText;
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
                showBottomSheetDialog();
                //importExportDialog();
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
            public void onCallClick(int position) {
                ContactEntity c = mContactsList.get(position);
                Log.d(TAG, "onCallClick: call btn in each card " + c.getId() + "\n" + c.getPersonContactNumber());

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + c.getPersonContactNumber()));
                startActivity(intent);
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        callTime = sharedPreferences.getString("button_timeout", "5000");
        showCommentDialog = sharedPreferences.getBoolean(Settings.KEY_PREF_CALL_COMMENT_DIALOG_SWITCH, false);
        Toast.makeText(this, "" + showCommentDialog,
                Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onCreate: showCommntDialog" + showCommentDialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        //sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

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
        /**
         *Variables and @View Declaration..
         */

        final CountDownTimer countDownTimer;
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View countDownLayout = layoutInflater.inflate(R.layout.timer_dialog, null);

        // AlertDialog creation
        final AlertDialog.Builder countDownAlertBuilder = new AlertDialog.Builder(MainActivity.this);
        countDownAlertBuilder.setTitle("Call Will Start In");
        countDownAlertBuilder.setView(countDownLayout);
        //TextView Reference
        mCountDownTimer = countDownLayout.findViewById(R.id.timerText);
        //button reference
        posButton = countDownLayout.findViewById(R.id.pos_btn);
        Button negButton = countDownLayout.findViewById(R.id.neg_btn);

        //creating alert Dialog
        final AlertDialog alertDialog = countDownAlertBuilder.create();

        /*
         * if @List is empty, then it will show @Toast
         * else it will execute the countdown timer
         * and positiveButton code
         * */
        if (mContactsList.isEmpty()) {
            showImportContactMsg();
        } else {
            alertDialog.show();
            long millis = Long.parseLong(callTime);
            countDownTimer = new CountDownTimer(millis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long timer = millisUntilFinished / 1000;
                    String s = String.valueOf(timer);
                    mCountDownTimer.setText(s);

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
                   // alertDialog.dismiss();
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


    private void autoCallDialog() {
        if (mContactsList.isEmpty()) {
            showImportContactMsg();
        } else {
            ContactEntity contactEntity1 = mContactsList.get(j);
            //send intent with the @contact_number to auto dial
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + contactEntity1.getPersonContactNumber()));
            startActivity(intent);

            /*
             * if @showCommentDialog is true, which is the value of preference,
             * @showCommentDialog method will execute,
             * else @afterFirstCall() will execute
             * */
            if (showCommentDialog) {
                showCommentDialog(contactEntity1.getId(), contactEntity1.getPersonName(), contactEntity1.getPersonContactNumber());
                //mContactViewModel.insertDialedContact(new ContactDialed(contactEntity1.getPersonName(), contactEntity1.getPersonContactNumber(),""));
                mContactViewModel.deleteById(contactEntity1);
                mContactsList.remove(j);
            } else {
                mContactViewModel.insertDialedContact(new ContactDialed(contactEntity1.getPersonName(), contactEntity1.getPersonContactNumber(), ""));
                mContactViewModel.deleteById(contactEntity1);
                mContactsList.remove(j);
                afterFirstCall();
            }
        }
    }

    private void showCommentDialog(final int id, final String name, final String contactNumber) {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View commentView = layoutInflater.inflate(R.layout.comment_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.comment_dialog_title);
        builder.setView(commentView);
        TextInputLayout commentInputLayout = commentView.findViewById(R.id.comment_text_layout);
        TextInputEditText commentEditText = commentView.findViewById(R.id.comment_edit_text);
        if (commentEditText.getText() != null) {
            commentText = commentEditText.getText().toString();
        } else {

            Log.d(TAG, "showCommentDialog: Text Field is Empty");
        }


        builder.setPositiveButton(R.string.comment_post_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: comment posted");

                ContactDialed contactDialed = new ContactDialed(name, contactNumber, commentText);
                //contactDialed.setId(id);
                mContactViewModel.insertDialedContact(contactDialed);
                Log.d(TAG, "onClick: " + commentText);
                afterFirstCall();

            }
        });

        AlertDialog dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void afterFirstCall() {
        if (mContactsList.isEmpty()) {
            showImportContactMsg();
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

                        if (showCommentDialog) {
                            showCommentDialog(c.getId(), c.getPersonName(), c.getPersonContactNumber());
                            mContactViewModel.deleteById(c);
                            mContactsList.remove(j);
                        } else {
                            mContactViewModel.insertDialedContact(new ContactDialed(c.getPersonName(), c.getPersonContactNumber(), ""));
                            mContactViewModel.deleteById(c);
                            mContactsList.remove(j);
                            Log.i(TAG, "calling on id " + c.getId());
                        }

                        if (mContactsList.isEmpty()) {
                            showImportContactMsg();
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

    private void showImportContactMsg() {
        Toast.makeText(MainActivity.this, "No Contact Found!! Import Contacts", Toast.LENGTH_LONG).show();
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
            Intent callHistoryIntent = new Intent(MainActivity.this, CallHistory.class);
            startActivity(callHistoryIntent);
        }

        if (id == R.id.add_contact) {
            addContactDialog();
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


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        showCommentDialog = sharedPreferences.getBoolean(Settings.KEY_PREF_CALL_COMMENT_DIALOG_SWITCH, true);
        callTime = sharedPreferences.getString(Settings.KEY_PREF_CALL_START_TIME, "5000");
        Log.d(TAG, "onSharedPreferenceChanged: call Time" + sharedPreferences.getBoolean(Settings.KEY_PREF_CALL_COMMENT_DIALOG_SWITCH, true));
        Log.d(TAG, "onSharedPreferenceChanged: showCommentDialog" + sharedPreferences.getString(Settings.KEY_PREF_CALL_START_TIME, "5000"));

    }

    private void addContactDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.add_contact, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_contact_dialog_title);
        builder.setPositiveButton("Save and Add More", null);
        builder.setNegativeButton("Save and Exit", null);

        final TextInputEditText contactPersonName = view.findViewById(R.id.name_edit_text);
        final TextInputEditText contactNumberEditText = view.findViewById(R.id.contact_number_edit_text);

        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button saveAndExit = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button saveAndAddMore = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                saveAndAddMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String personName = contactPersonName.getText().toString();
                        String contactNumber = contactNumberEditText.getText().toString();

                        if (contactNumber.isEmpty() && personName.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Enter Details", Toast.LENGTH_LONG).show();
                        } else {
                            ContactEntity c = new ContactEntity(personName, contactNumber);
                            mContactViewModel.insert(c);
                            contactPersonName.getText().clear();
                            contactNumberEditText.getText().clear();
                        }
                    }
                });

                saveAndExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: ");
                        String personName = contactPersonName.getText().toString();
                        String contactNumber = contactNumberEditText.getText().toString();

                        if (contactNumber.isEmpty() && personName.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Enter Details", Toast.LENGTH_LONG).show();
                        } else {
                            ContactEntity c = new ContactEntity(personName, contactNumber);
                            mContactViewModel.insert(c);
                            contactPersonName.getText().clear();
                            contactNumberEditText.getText().clear();
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        alertDialog.show();

    }

//    private void importExportDialog() {
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View view = layoutInflater.inflate(R.layout.import_export_dialog, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Import/Export")
//                .setIcon(R.drawable.ic_import_export);
//        builder.setView(view);
//        Button importButton = view.findViewById(R.id.import_btn);
//        Button exportButton = view.findViewById(R.id.export_btn);
//        final AlertDialog alertDialog = builder.create();
//
//        importButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                importCSV();
//                alertDialog.dismiss();
//            }
//        });
//
//        exportButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Export Clicked", Toast.LENGTH_LONG).show();
//                alertDialog.dismiss();
//            }
//        });
//        alertDialog.show();
//    }


    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();

        Button importBtn = view.findViewById(R.id.bottom_dialog_import);
        Button exportBtn = view.findViewById(R.id.bottom_dialog_export);

        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importCSV();
                dialog.dismiss();
            }
        });

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Export Clicked", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });


    }
}


