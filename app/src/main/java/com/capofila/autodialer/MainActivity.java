package com.capofila.autodialer;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;
import com.capofila.autodialer.contactList.ContactAdapter;
import com.capofila.autodialer.database.ContactEntity;
import com.capofila.autodialer.database.ContactViewModel;
import com.capofila.autodialer.importAndExport.MyCSVFileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mContactRecyclerView;
    private static final String TAG = "Mainactivity";
    private List<ContactEntity> mContactsList = new ArrayList<>();
    private ContactAdapter mAdapter;
    private ContactEntity c;
    private ContactViewModel mContactViewModel;
    private List<ContactEntity> contacts = new ArrayList<>();
    private int i = 0;

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
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
            public void onChanged(@Nullable final List<ContactEntity> contactEntities) {
//                Toast.makeText(MainActivity.this,"Hello",Toast.LENGTH_LONG).show();
                mContactsList = contactEntities;
                mAdapter.setContacts(contactEntities);

                mAdapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        contacts.get(position);
                    }

                    @Override
                    public void onCallClick(int position) {
                        ContactEntity c = contactEntities.get(position);
                        Toast.makeText(MainActivity.this, "Clicked on " + c.getId(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + c.getPersonContactNumber()));
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void initRecyclerView() {

//        mContactsList = new ArrayList<>();
//        mContactsList.add(new Contacts(1,"abc","9899372603"));
//        mContactsList.add(new Contacts(2,"bfc","888"));
//        mContactsList.add(new Contacts(3,"xyz","222"));
//        mContactsList.add(new Contacts(4,"ksi","444"));
//        mContactsList.add(new Contacts(5,"wassd","55"));
//        mContactsList.add(new Contacts(6,"qwerty","000"));
//        mContactsList.add(new Contacts(7,"lss","9999"));
//        mContactsList.add(new Contacts(8,"gaiiisd","8787"));
//        mContactsList.add(new Contacts(9,"ish","9898"));
//        mContactsList.add(new Contacts(10,"thisss","7656"));

        mContactRecyclerView = findViewById(R.id.contactList);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.start_call) {
            Toast.makeText(this, "Call Started" + mContactsList.size(), Toast.LENGTH_LONG).show();
            startAutoCall();

        }
        if (id == R.id.pause_call) {
            Toast.makeText(this, "Call paused", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startAutoCall() {
        if (mContactsList.isEmpty()) {
            Toast.makeText(this, "Import Contacts ", Toast.LENGTH_LONG).show();
        } else {
            c = mContactsList.get(i);
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + c.getPersonContactNumber()));
            startActivity(intent);

            for (i = 1; i < mContactsList.size();i++) {
                c = mContactsList.get(i);

                Log.d(TAG, "startAutoCall: " + c.getId());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Auto Dialer Start");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + c.getPersonContactNumber()));
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

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


}



