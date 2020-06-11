package com.example.klassenkassa.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.klassenkassa.R;
import com.example.klassenkassa.Requests.DELETERequest;
import com.example.klassenkassa.Requests.GETRequest;
import com.example.klassenkassa.Requests.POSTRequest;
import com.example.klassenkassa.Requests.PUTRequest;
import com.example.klassenkassa.data.Category;
import com.example.klassenkassa.data.CategoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private List<Category> categories = new ArrayList<>();
    private ListView clistView;
    private CategoryAdapter cAdapter;
    private String username = "app";
    private String password = "user2020";
    private final String URL = "http://restapi.eu";
    private String jsonRequest="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clistView = findViewById(R.id.category_listView);
        cAdapter = new CategoryAdapter(this, R.layout.category_list, categories);
        clistView.setAdapter(cAdapter);
        registerForContextMenu(clistView);

        checkNetwork();
        fillItemsList();

        clistView.setOnItemClickListener((parent, view, position, id) -> {
            int selection = categories.get(position).getCategoryID();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {

        int viewId = v.getId();
        if (viewId == R.id.category_listView)
        {
            getMenuInflater().inflate(R.menu.context_menue,menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if(item.getItemId()== R.id.category_edit)
        {
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if   ( info  !=  null ){
                edit(info.position);
            }
            return true;
        }else if(item.getItemId()== R.id.category_delete)
        {
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if   ( info  !=  null ){
                delete(info.position);
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void delete(int position) {
        String response=null;
        int cat_id=categories.get(position).getCategoryID();
        DELETERequest request_delete = new DELETERequest(URL + "/deletecategory.php?username=" + username + "&password=" + password+"&categoryID="+cat_id);
        try {
            response=request_delete.execute(jsonRequest).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.getString("message").equals("Successful"))
            {
                categories.remove(position);
                fillItemsList();
                cAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void edit(int position) {
        final View vDialog = getLayoutInflater().inflate(R.layout.add_category, null);
        EditText et_cat=vDialog.findViewById(R.id.categoryName_plainText);
        et_cat.setText(categories.get(position).getName());
        EditText et_due=vDialog.findViewById(R.id.categoryDate_calendarView);
        et_due.setText(categories.get(position).getDueDate().format( DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        EditText et_cost=vDialog.findViewById(R.id.categoryCost_plainText);
        et_cost.setText(categories.get(position).getCost()+"");
        setUpDialog(vDialog, categories.get(position).getCategoryID());
    }

    private void createNewCategory()
    {
        final View vDialog = getLayoutInflater().inflate(R.layout.add_category, null);
        EditText et_cost=vDialog.findViewById(R.id.categoryCost_plainText);
        et_cost.setText("");
        setUpDialog(vDialog, -1);
    }

    private void showSettings()
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    private void setUpDialog(final View vDialog, int pos)
    {
        AlertDialog.Builder ad=new AlertDialog.Builder(this);
        ad.setView(vDialog).setPositiveButton("SUBMIT", (dialog,which)-> handleDialog(vDialog, pos)).setNegativeButton("CANCEL", null).show();
    }

    private void handleDialog(View vDialog, int pos) {
        EditText et_name = vDialog.findViewById(R.id.categoryName_plainText);
        EditText et_cost=vDialog.findViewById(R.id.categoryCost_plainText);
        CalendarView et_due=vDialog.findViewById(R.id.categoryDate_calendarView);
        LocalDate date = Instant.ofEpochMilli(et_due.getDate()).atZone(ZoneId.systemDefault()).toLocalDate();
        String response = null;
        double cost=0;
        if(!et_cost.getText().toString().equals("")) {
            cost = Double.parseDouble(et_cost.getText().toString());
        }
        jsonRequest= "{\"name\":"+"\""+et_name.getText().toString()+"\""+",\"dueDate\":"+"\""+date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"\","+"\"cost\":"+"\""+cost+"\"}";
            if(pos==-1) {
            POSTRequest request_post = new POSTRequest(URL + "/createcategory.php?username=" + username + "&password=" + password);
                try {
                    response=request_post.execute(jsonRequest).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("message").equals("Category was created."))
                    {
                        fillItemsList();
                        cAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else
            {
                PUTRequest request_post = new PUTRequest(URL + "/putcategory.php?username=" + username + "&password=" + password+"&categoryID="+pos);
                try {
                    response=request_post.execute(jsonRequest).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("message").equals("Category was put."))
                    {
                        fillItemsList();
                        cAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

    }

    private void fillItemsList(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dtf = dtf.withLocale(Locale.getDefault());

        categories.clear();
        GETRequest requestGET = new GETRequest(URL+"/getcategory.php?username="+username+"&password="+password);

        String response = null;
        try {
            response = requestGET.execute("").get();
            JSONArray jsonArray = new JSONArray(response);
            jsonArray = jsonArray.optJSONArray(0);
            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                int categoryID = jsonObject.getInt("categoryID");
                String name = jsonObject.getString("name");
                String date = jsonObject.getString("dueDate");
                float cost = (float)jsonObject.getDouble("cost");
                LocalDate dt = LocalDate.parse(date, dtf);

                categories.add(new Category(categoryID, name, dt, cost));
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
