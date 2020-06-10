package com.example.klassenkassa.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.klassenkassa.R;
import com.example.klassenkassa.Requests.GETRequest;
import com.example.klassenkassa.Requests.PUTRequest;
import com.example.klassenkassa.data.Status;
import com.example.klassenkassa.data.Student;
import com.example.klassenkassa.data.StudentAdapter;
import com.example.klassenkassa.other.OnSelectionChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MasterFragment extends Fragment {
    private OnSelectionChangedListener listener;
    private ListView slistView;
    private StudentAdapter sAdapter;
    List<Student> students;
    private String username = "app";
    private String password = "user2020";
    private final String URL = "http://restapi.eu";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_master, container, false);
        initializeViews(v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSelectionChangedListener)
        {
            listener = (OnSelectionChangedListener) context;
        }
    }

    private void initializeViews(View view)
    {
        students=new ArrayList<>();
        slistView = view.findViewById(R.id.student_listView);
        registerForContextMenu(slistView);
        slistView.setOnItemClickListener((parent, view1, position, id) -> {
            Student item=students.get(position);
            listener.OnSelectionChanged(position, item);
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {int viewId = v.getId();
        if (viewId == R.id.student_listView)
        {
            getActivity().getMenuInflater().inflate(R.menu.context_2_menue,menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if(item.getItemId()== R.id.student_edit)
        {
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if   ( info  !=  null ){
                edit(info.position);
            }
            return true;
        }else if(item.getItemId()== R.id.student_delete)
        {
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if   ( info  !=  null ){
                delete(info.position);
                students.remove(info.position);
            }
            return true;
        }else if(item.getItemId()== R.id.change_status)
        {
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if   ( info  !=  null ){
                changeStatus(info.position);
            }
            return true;
        }
        sAdapter.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

    private void changeStatus(int position) {
        final View vDialog = getLayoutInflater().inflate(R.layout.change_status,null);
        Spinner changeStatusSpinner = vDialog.findViewById(R.id.changeStatusSpinner);
        changeStatusSpinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Status.values()));

        new AlertDialog.Builder(this.getContext())
                .setMessage("Bezahlstatus Ändern")
                .setCancelable(true)
                .setView(vDialog)
                .setPositiveButton("Ok",((dialog, which) -> handleChangeStatusDialog(vDialog, students.get(position))))
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    private void handleChangeStatusDialog(final View vDialog, Student student){
        Spinner changeStatusSpinner = vDialog.findViewById(R.id.changeStatusSpinner);
        EditText changeStatusValue = vDialog.findViewById(R.id.changeStatusEditText);
        float newCost = student.getCost();
        Status newStatus = (Status) changeStatusSpinner.getSelectedItem();

        if(changeStatusSpinner.getSelectedItem().equals(Status.TEILWEISE_BEZAHLT)){
            newCost = newCost - Float.parseFloat(changeStatusValue.getText().toString());
        }else if(changeStatusSpinner.getSelectedItem().equals(Status.BEZAHLT)){
            newCost = 0.00f;
        }

        if(newCost >= 0.00f) {
            PUTRequest request_post = new PUTRequest(URL + "/putstudent.php?username=" + username + "&password=" + password + "&categoryID=" + student.getCategoryID() + "&studentID=" + student.getStudentID());
            String jsonRequest = "{\"studentID\":\"" + student.getStudentID() +
                    "\",\"firstname\":\"" + student.getFirstname() +
                    "\",\"lastname\":\"" + student.getLastname() +
                    "\",\"debts\":\"" + newCost +
                    "\",\"status\":\"" + newStatus +
                    "\",\"additionalData\":\"" + student.getAdditionalData() + "\"}";
            String response = "";
            try {
                response = request_post.execute(jsonRequest).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("message").equals("Student was put.")) {
                    loadStudents();
                    sAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void delete(int position) {
    }

    private void edit(int position) {
    }

    public void loadStudents()
    {
        students.clear();
        GETRequest requestGET = new GETRequest(URL+"/getstudent.php?username="+username+"&password="+password);

        String response = null;
        try {
            response = requestGET.execute("").get();
            JSONArray jsonArray = new JSONArray(response);
            jsonArray = jsonArray.optJSONArray(0);
            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                int studentID = jsonObject.getInt("studentID");
                int categoryID = jsonObject.getInt("categoryID");
                String firstname = jsonObject.getString("firstname");
                String lastname = jsonObject.getString("lastname");
                float debts = (float)jsonObject.getDouble("debts");
                Status status = Status.valueOf(jsonObject.getString("status"));
                String additionalData = jsonObject.getString("additionalData");

                students.add(new Student(studentID, categoryID, firstname, lastname, debts, status, additionalData));
            }
        }catch (ExecutionException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Herunterladen der Daten fehlgeschlagen", Toast.LENGTH_LONG).show();
        }
    }

    public void saveStudents()
    {

    }

    public void createNewStudent()
    {

    }

    public void setStudents(List<Student> students) {
        this.students=students;
        sAdapter = new StudentAdapter(getActivity(), R.layout.student_list, students);
        slistView.setAdapter(sAdapter);
        sAdapter.notifyDataSetChanged();
    }
}
