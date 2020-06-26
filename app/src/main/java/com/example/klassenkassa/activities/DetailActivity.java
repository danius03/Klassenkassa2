package com.example.klassenkassa.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import com.example.klassenkassa.R;
import com.example.klassenkassa.data.Student;

public class DetailActivity extends AppCompatActivity {
    DetailFragment detailFragment;
    private boolean darkmode;
    private boolean darkmodeSensor;
    BrightnessSensor bs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        int   orientation = getResources().getConfiguration ().orientation ;
        if   ( orientation !=  Configuration.ORIENTATION_PORTRAIT){
            finish();
            return;
        }
        handleIntent();
    }

    public void handleIntent()
    {
        Intent intent=getIntent();
        detailFragment= (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
        int pos=intent.getIntExtra("POS", -1);
        Student s= (Student) intent.getSerializableExtra("STUDENT");
        configActionBar();
        bs = new BrightnessSensor();
        darkmode = getIntent().getBooleanExtra("darkmode", false);
        detailFragment.setDarkmode(darkmode);
        darkmodeSensor = getIntent().getBooleanExtra("sensor", false);
        if (darkmodeSensor) {
            bs.start();
        }
        detailFragment.showInformation(pos, s);
    }

    private class BrightnessSensor extends Thread {
        boolean run = true;

        @Override
        public void run() {
            int brightness = 0;
            while (run) {
                ContentResolver cr = getApplicationContext().getContentResolver();

                int oldBrightness = brightness;
                try {
                    brightness = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                if (oldBrightness != brightness) {

                    if (brightness > 100) {
                        detailFragment.setDarkmode(true);

                    } else {
                        detailFragment.setDarkmode(false);
                    }
                    DetailActivity.this.runOnUiThread(() -> detailFragment.changeTextColor());
                }

            }
        }
    }
    @Override
    public void finish() {
        if(bs!=null) {
            if (bs.isAlive()) {
                try {
                    bs.run = false;
                    bs.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        super.finish();
    }


    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
