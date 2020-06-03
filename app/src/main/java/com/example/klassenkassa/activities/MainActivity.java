package com.example.klassenkassa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.klassenkassa.R;
import com.example.klassenkassa.Requests.GETRequest;
import com.example.klassenkassa.data.Category;
import com.example.klassenkassa.data.CategoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private List<Category> categorys = new ArrayList<>();
    private ListView clistView;
    private CategoryAdapter cAdapter;
    private String username = "app";
    private String password = "user2020";
    private final String URL = "http://restapi.eu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clistView = findViewById(R.id.category_listView);
        cAdapter = new CategoryAdapter(this, R.layout.category_list, categorys);
        clistView.setAdapter(cAdapter);

        checkNetwork();
        fillItemsList();

        clistView.setOnItemClickListener((parent, view, position, id) -> {
            int selection = categorys.get(position).getCategoryID();
            Intent intent = new Intent(this, Activity2.class);
            intent.putExtra("selection", selection);
            startActivity(intent);
        });
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

    private void fillItemsList(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd");
        dtf = dtf.withLocale(Locale.getDefault());


        GETRequest requestGET = new GETRequest(URL+"/getcategory.php?username="+username+"&password="+password);

        String response = null;
        try {
            response = requestGET.execute("").get();
            JSONArray jsonArray = new JSONArray(response);
            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                int categoryID = jsonObject.getInt("categoryID");
                String name = jsonObject.getString("name");
                String date = jsonObject.getString("dueDate");
                float cost = (float)jsonObject.getDouble("cost");
                LocalDate dt = LocalDate.parse(date, dtf);

                categorys.add(new Category(categoryID, name, dt, cost));
            }
        }catch (ExecutionException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(this, "Herunterladen der Daten fehlgeschlagen", Toast.LENGTH_LONG).show();
        }

    }

    private void checkNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo == null && !activeNetworkInfo.isConnected()){
            Toast.makeText(this, "Bitte überprüfen Sie Ihre Internetverbindung!", Toast.LENGTH_LONG).show();
        }
    }
}
