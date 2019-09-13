package com.capofila.autodialer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.capofila.autodialer.contactList.Contacts;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        private RecyclerView mContactRecyclerView;
    private static final String TAG = "Mainactivity";
        private ArrayList<Contacts> mContactsList;
        private ContactAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "It will import the contacts...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
    }

    private void initRecyclerView(){

        mContactsList = new ArrayList<>();
        mContactsList.add(new Contacts(1,"abc","9899372603"));
        mContactsList.add(new Contacts(2,"bfc","888"));
        mContactsList.add(new Contacts(3,"xyz","222"));
        mContactsList.add(new Contacts(4,"ksi","444"));
        mContactsList.add(new Contacts(5,"wassd","55"));
        mContactsList.add(new Contacts(6,"qwerty","000"));
        mContactsList.add(new Contacts(7,"lss","9999"));
        mContactsList.add(new Contacts(8,"gaiiisd","8787"));
        mContactsList.add(new Contacts(9,"ish","9898"));
        mContactsList.add(new Contacts(10,"thisss","7656"));

        mContactRecyclerView = findViewById(R.id.contactList);
        mAdapter = new ContactAdapter(mContactsList);
        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mContactRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mContactsList.get(position);
            }

            @Override
            public void onCallClick(int position) {
                Contacts c = mContactsList.get(position);
                Toast.makeText(MainActivity.this,"Clicked on " + c.getId()  ,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + c.getPersonContactNumber()));
                startActivity(intent);
            }
        });
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
        if(id == R.id.start_call){
         Toast.makeText(this,"Call Started" + mContactsList.size(),Toast.LENGTH_LONG).show();
         startAutoCall();

        }
        if(id == R.id.pause_call){
            Toast.makeText(this,"Call paused", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startAutoCall() {

        for(int i = 0; i<mContactsList.size(); i++){
           final Contacts c = mContactsList.get(i);
            Log.d(TAG, "startAutoCall: " + c.getId());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Auto Dialer Start");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this,"hello",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + c.getPersonContactNumber()));
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
