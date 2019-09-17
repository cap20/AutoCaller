package com.capofila.autodialer.importAndExport;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.capofila.autodialer.database.ContactEntity;
import com.capofila.autodialer.database.ContactViewModel;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class ImportCVSToSQLiteDataBase extends AsyncTask<String, String, String> {
    private static final String TAG = "Import" ;
    private ContactViewModel contactViewModel;
    private ContactEntity contactEntity;
    Activity activity;
    Context context;
    File file=null;
    private ProgressDialog dialog;
    private String id;
    private String name;
    private String phone;
    private List<ContactEntity> mContact = new ArrayList<>();
    private Application application = new Application();

    public ImportCVSToSQLiteDataBase(Context context, Activity activity,File file) {
        this.context=context;
        this.activity=activity;
        this.file=file;
    }

    @Override
    protected void onPreExecute()
    {
        dialog=new ProgressDialog(context);
        dialog.setTitle("Importing Data into SecureIt DataBase");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        String data="";
        Log.d(getClass().getName(), file.toString());

        try{
            CSVReader reader = new CSVReader(new FileReader(file));
            String [] nextLine;

            //here I am just displaying the CSV file contents, and you can store your file content into db from while loop...

            while ((nextLine = reader.readNext()) != null) {

                // nextLine[] is an array of values from the line

                 id =nextLine[0];
                 name=nextLine[1];
                 phone=nextLine[2];

                data=data+"AccId:"+ id +"  Account_name:"+name+"\n";

                mContact.add(new ContactEntity(name,phone));

                Log.d(TAG, "doInBackground: " + data.length());
            }
            return data;

        } catch (Exception e) {
            Log.e("Error", "Error for importing file");
        }
        return data="";

    }

    protected void onPostExecute(String data)
    {

        if (dialog.isShowing())
        {
            dialog.dismiss();
        }

        if (data.length()!=0)
        {
            Toast.makeText(context, "File is built Successfully!"+"\n"+data, Toast.LENGTH_LONG).show();
            Log.d(TAG,"list Size" + mContact.size());

            contactViewModel = new ContactViewModel(application);
            for(int i = 0; i < mContact.size(); i++ ) {
                ContactEntity c = mContact.get(i);
                contactViewModel.insert(new ContactEntity(c.getPersonName(), c.getPersonContactNumber()));
            }

        }else{
            Toast.makeText(context, "File fail to build", Toast.LENGTH_SHORT).show();
        }
    }


}