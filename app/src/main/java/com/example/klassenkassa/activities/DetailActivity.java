package com.example.klassenkassa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.klassenkassa.R;
import com.example.klassenkassa.data.Student;

public class DetailActivity extends AppCompatActivity {

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
        DetailFragment detailFragment= (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
        int pos=intent.getIntExtra("POS", -1);
        Student s= (Student) intent.getSerializableExtra("STUDENT");
        detailFragment.showInformation(pos, s);
    }
}
