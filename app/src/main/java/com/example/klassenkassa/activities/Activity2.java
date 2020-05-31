package com.example.klassenkassa.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.klassenkassa.R;
import com.example.klassenkassa.data.Student;
import com.example.klassenkassa.other.OnSelectionChangedListener;

import java.io.Serializable;

public class Activity2 extends AppCompatActivity implements OnSelectionChangedListener {

    private DetailFragment detailFragment;
    private boolean showDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detailFragment= (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
        showDetail = detailFragment !=null && detailFragment.isInLayout();
        configActionBar();
    }


    @Override
    public void OnSelectionChanged(int pos, Student item) {

        if(showDetail) {
            detailFragment.showInformation(pos, item);
        }else
        {
            Intent intent=new Intent(this, DetailActivity.class);
            intent.putExtra("POS", pos);
            intent.putExtra("STUDENT", (Serializable) item);
            startActivity(intent);
        }
    }

    private void configActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
            case R.id.new_student:
                detailFragment.createNewStudent();
                break;
            case R.id.save_students:
                detailFragment.saveStudents();
                break;
            case R.id.load_students:
                detailFragment.loadStudents();
                break;
            case android.R.id.home:
                Intent data=new Intent(this, MainActivity.class);
                setResult(RESULT_OK, data);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
