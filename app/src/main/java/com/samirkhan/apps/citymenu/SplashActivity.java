package com.samirkhan.apps.citymenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import custom.PreferencesFile;
import datalayer.Updates;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Activity activity = this;

       /* String[] dataM = {

        };
        new LocalDataManager(this).addViewer(dataM);
        String query = "SELECT * FROM viewer";
        Cursor cursor = new DBHelper(this).execRAW(query);
        try {
            if (cursor.moveToFirst())
                do {
                } while (cursor.moveToNext());
        }
        catch (Exception e){

        }*/

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                PreferencesFile preferencesFile = new PreferencesFile(getBaseContext());
                boolean firstTime = preferencesFile.firstTime();
                if (firstTime) {
                    ReferenceDialog referenceDialog = new ReferenceDialog(SplashActivity.this);
                    referenceDialog.show(SplashActivity.this.getFragmentManager(), "dialog_ref");
                    return;
                }

                int cityId = preferencesFile.defaultCityId();
                int restId = preferencesFile.restId();
                Intent intent;
                if (cityId < 1) {
                    intent = new Intent(activity, CityListActivity.class);
                    intent.putExtra("setDefaultCity", true);

                } else if (restId > 0) {
                    intent = new Intent(activity, RestaurantProfileEditableActivity.class);
                    intent.putExtra("restId", restId);
                } else {
                    intent = new Intent(activity, RestaurantListActivity.class);
                    intent.putExtra("cityId", cityId);
                }
                Updates updates = new Updates(activity, false);
                updates.execute();

                // add new viewer
               /* Viewer viewer = new Viewer(SplashActivity.this);
                viewer.add();
                UploadViewerService service= new UploadViewerService(SplashActivity.this);
                service.execute();*/

                startActivity(intent);
                activity.finish();


            }
        });
        thread1.start();

    }

}
