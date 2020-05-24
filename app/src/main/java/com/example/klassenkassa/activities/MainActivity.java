package com.example.klassenkassa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.klassenkassa.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.action_menue,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();

        switch(id) {
            case R.id.new_category:
                createNewCategory();
                break;
            case R.id.settings:
                showSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNewCategory()
    {

    }

    private void showSettings()
    {

    }
}
