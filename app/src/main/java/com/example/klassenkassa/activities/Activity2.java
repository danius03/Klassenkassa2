package com.example.klassenkassa.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.klassenkassa.R;
import com.example.klassenkassa.Requests.GETRequest;
import com.example.klassenkassa.data.Category;
import com.example.klassenkassa.data.CategoryAdapter;
import com.example.klassenkassa.data.Status;
import com.example.klassenkassa.data.Student;
import com.example.klassenkassa.data.StudentAdapter;
import com.example.klassenkassa.other.OnSelectionChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Activity2 extends AppCompatActivity implements OnSelectionChangedListener {

    private DetailFragment detailFragment;
    private MasterFragment masterFragment;
    private boolean showDetail;
    private boolean darkmode;
    private boolean darkmodeSensor;

    public int currentCategoryID;
    public float currentCategoryCost;

    private List<Student> students = new ArrayList<>();
    private String username = "app";
    private String password = "user2020";
    private final String URL = "http://restapi.eu";
    BrightnessSensor bs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
        masterFragment = (MasterFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_master);

        showDetail = detailFragment != null && detailFragment.isInLayout();
        configActionBar();
        if (getIntent().getExtras() != null) {
            currentCategoryID = getIntent().getExtras().getInt("selection");
            currentCategoryCost = getIntent().getExtras().getFloat("cost");
        }
        fillItemsList();
        bs = new BrightnessSensor();
        masterFragment.setStudents(students);
        darkmode = getIntent().getBooleanExtra("darkmode", false);
        darkmodeSensor = getIntent().getBooleanExtra("sensor", false);
        masterFragment.setDarkmode(darkmode);

        if (darkmodeSensor) {

            bs.start();
        }

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
                    masterFragment.changeDarkmode(brightness);
                    if(showDetail)
                    {
                        detailFragment.changeDarkmode(brightness);
                    }
                    Activity2.this.runOnUiThread(() -> masterFragment.notifySetChanged());
                }

            }
        }
    }

    @Override
    public void finish() {
        if(bs.isAlive()) {
            try {
                bs.run=false;
                bs.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.finish();
    }

    @Override
    public void OnSelectionChanged(int pos, Student item) {

        if (showDetail) {
            detailFragment.showInformation(pos, item);
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("POS", pos);
            intent.putExtra("STUDENT", (Serializable) item);
            if(!darkmodeSensor) {
                intent.putExtra("darkmode", darkmode);
            }else {
                intent.putExtra("sensor", darkmodeSensor);
            }
            startActivity(intent);
        }
    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_2_menue, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.new_student:
                masterFragment.createNewStudent(currentCategoryID, currentCategoryCost);

                break;
            case R.id.save_students:
                masterFragment.saveStudents();
                break;
            case R.id.load_students:
                masterFragment.loadStudents(currentCategoryID, currentCategoryCost);
                break;
            case android.R.id.home:
                Intent data = new Intent(this, MainActivity.class);
                setResult(RESULT_OK, data);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillItemsList() {
        GETRequest requestGET = new GETRequest(URL + "/getstudent.php?username=" + username + "&password=" + password);

        String response = null;
        try {
            response = requestGET.execute("").get();
            JSONArray jsonArray = new JSONArray(response);
            if(jsonArray.length()!=0)
            {
            jsonArray = jsonArray.optJSONArray(0);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                if (jsonObject.getInt("categoryID") == currentCategoryID) {
                    int studentID = jsonObject.getInt("studentID");
                    String firstname = jsonObject.getString("firstname");
                    String lastname = jsonObject.getString("lastname");
                    double debts = jsonObject.getDouble("debts");
                    String statusString = jsonObject.getString("status");
                    String additionalData = jsonObject.getString("additionalData");
                    Status status = Status.valueOf(statusString);
                    students.add(new Student(studentID, currentCategoryID, firstname, lastname, debts, status, additionalData));
                }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(this, "Herunterladen der Schüler-Daten fehlgeschlagen", Toast.LENGTH_LONG).show();
        }

    }


}
