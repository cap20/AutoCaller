package com.capofila.autodialer.importAndExport;


import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import java.io.File;


import android.app.Activity;
import android.content.Context;
import android.os.Environment;

public class MyCSVFileReader {

    public static void openDialogToReadCSV(final Activity activity,final Context context)
    {
        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
        FileDialog fileDialog = new FileDialog(activity, mPath);
        fileDialog.setFileEndsWith(".txt");
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {

            @Override
            public void fileSelected(File file) {
                new ImportCVSToSQLiteDataBase(context,activity,file).execute();
            }
        });
        fileDialog.showDialog();
    }

}