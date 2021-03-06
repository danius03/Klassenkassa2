package com.example.klassenkassa.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.klassenkassa.R;
import com.example.klassenkassa.Requests.DELETERequest;
import com.example.klassenkassa.Requests.GETRequest;
import com.example.klassenkassa.Requests.POSTRequest;
import com.example.klassenkassa.Requests.PUTRequest;
import com.example.klassenkassa.data.Status;
import com.example.klassenkassa.data.Student;
import com.example.klassenkassa.data.StudentAdapter;
import com.example.klassenkassa.other.OnSelectionChangedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MasterFragment extends Fragment {
    private OnSelectionChangedListener listener;
    private ListView slistView;
    private StudentAdapter sAdapter;
    List<Student> students;
    private String username = "app";
    private String password = "user2020";
    private String filename = "students.csv";
    private final String URL = "http://restapi.eu";

    private int currentCategoryID;
    private float currentCategoryCost;

    private boolean darkmode;
    private boolean darkmodeSensor;
    private FrameLayout l;
    private Activity2 activity2;

    public boolean isDarkmode() {
        return darkmode;
    }

    public void setDarkmode(boolean darkmode) {
        this.darkmode = darkmode;
        sAdapter.setDarkmode(darkmode);
        if(darkmode==true) {
            l.setBackgroundColor(Color.DKGRAY);
        }
    }

    public boolean isDarkmodeSensor() {
        return darkmodeSensor;
    }

    public void setDarkmodeSensor(boolean darkmodeSensor) {
        this.darkmodeSensor = darkmodeSensor;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_master, container, false);
        initializeViews(v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectionChangedListener) {
            listener = (OnSelectionChangedListener) context;
        }
    }

    public void setActivity(Activity2 activity)
    {
        activity2 = activity;
    }

    private void initializeViews(View view) {
        students = new ArrayList<>();
        slistView = view.findViewById(R.id.student_listView);
        registerForContextMenu(slistView);
        slistView.setOnItemClickListener((parent, view1, position, id) -> {
            Student item = students.get(position);
            listener.OnSelectionChanged(position, item);
        });
        l = view.findViewById(R.id.fragment_master);
        if(darkmode)
        {
            l.setBackgroundColor(Color.DKGRAY);
            sAdapter.setDarkmode(true);
            sAdapter.notifyDataSetChanged();
        }
    }

    public void changeDarkmode(int brightness)
    {
        if (brightness > 100) {
            darkmode = true;
            l.setBackgroundColor(Color.DKGRAY);
            sAdapter.setDarkmode(true);
        } else {
            darkmode = false;
            l.setBackgroundColor(Color.WHITE);
            sAdapter.setDarkmode(false);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int viewId = v.getId();
        if (viewId == R.id.student_listView) {
            getActivity().getMenuInflater().inflate(R.menu.context_2_menue, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.student_edit) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if (info != null) {
                edit(info.position);
            }
            return true;
        } else if (item.getItemId() == R.id.student_delete) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if (info != null) {
                delete(info.position);
            }
            return true;
        } else if (item.getItemId() == R.id.change_status) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if (info != null) {
                changeStatus(info.position);
            }
            return true;
        }
        sAdapter.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

    private void changeStatus(int position) {
        final View vDialog = getLayoutInflater().inflate(R.layout.change_status, null);
        Spinner changeStatusSpinner = vDialog.findViewById(R.id.changeStatusSpinner);
        changeStatusSpinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, Status.values()));

        new AlertDialog.Builder(this.getContext())
                .setMessage("Bezahlstatus Ändern")
                .setCancelable(true)
                .setView(vDialog)
                .setPositiveButton("Ok", ((dialog, which) -> handleChangeStatusDialog(vDialog, students.get(position))))
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    private void handleChangeStatusDialog(final View vDialog, Student student) {
        Spinner changeStatusSpinner = vDialog.findViewById(R.id.changeStatusSpinner);
        EditText changeStatusValue = vDialog.findViewById(R.id.changeStatusEditText);
        float newCost = student.getCost();
        Status newStatus = (Status) changeStatusSpinner.getSelectedItem();

        if (changeStatusSpinner.getSelectedItem().equals(Status.TEILWEISE_BEZAHLT)) {
            if (!changeStatusValue.getText().toString().equals("")) {
                newCost = newCost - Float.parseFloat(changeStatusValue.getText().toString());
            } else {
                newCost = -1.00f;
            }
        } else if (changeStatusSpinner.getSelectedItem().equals(Status.BEZAHLT)) {
            newCost = 0.00f;
        }

        if (newCost >= 0.00f) {
            PUTRequest request_post = new PUTRequest(URL + "/putstudent.php?username=" + username + "&password=" + password + "&categoryID=" + student.getCategoryID() + "&studentID=" + student.getStudentID());
            String jsonRequest = "{\"studentID\":\"" + student.getStudentID() +
                    "\",\"firstname\":\"" + student.getFirstname() +
                    "\",\"lastname\":\"" + student.getLastname() +
                    "\",\"debts\":\"" + newCost +
                    "\",\"status\":\"" + newStatus +
                    "\",\"additionalData\":\"" + student.getAdditionalData() + "\"}";
            String response = "";
            students.remove(student);
            if(newCost==0)
            {
                student.setStatus(Status.BEZAHLT);
            }else
            {
                student.setStatus(Status.TEILWEISE_BEZAHLT);
            }
            student.setCost(newCost);
            students.add(student);
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
                    /*loadStudents(currentCategoryID, currentCategoryCost);*/
                    Collections.sort(students);
                    sAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void delete(int position) {
        DELETERequest task = new DELETERequest(URL + "/deletestudent.php?username=" + username + "&password=" + password + "&categoryID=" + students.get(position).getCategoryID() + "&studentID=" + students.get(position).getStudentID());
        task.execute("");
        //maybeAddInterruption (sleep)
        String jsonResponse = task.getJsonResponse();
        students.remove(position);
        sAdapter.notifyDataSetChanged();
    }

    private void edit(int position) {
        final View vDialog = getLayoutInflater().inflate(R.layout.add_student, null);

        EditText et_number = vDialog.findViewById(R.id.studentNumber_numberText);
        et_number.setText(students.get(position).getStudentID() + "");
        EditText et_firstName = vDialog.findViewById(R.id.studentFirstName_plainText);
        et_firstName.setText(students.get(position).getFirstname());
        EditText et_lastName = vDialog.findViewById(R.id.studentSurname_plainText);
        et_lastName.setText(students.get(position).getLastname());
        EditText et_cost = vDialog.findViewById(R.id.studentCost_plainText);
        et_cost.setText(students.get(position).getCost() + "");
        EditText et_data = vDialog.findViewById(R.id.studentData_plainText);
        et_data.setText(students.get(position).getAdditionalData());

        setUpDialog(vDialog, position);
    }

    public void loadStudents(int currentCategoryID, float currentCategoryCost) {
        this.currentCategoryID = currentCategoryID;
        this.currentCategoryCost = currentCategoryCost;


        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        } else {
            loader();
        }
    }


    public void loader() {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) return;
        File outFile = getActivity().getExternalFilesDir(null);
        String path = outFile.getAbsolutePath();
        String fullPath = path + File.separator + filename;

        try {

            FileInputStream fos = new FileInputStream(fullPath);
            BufferedReader in = new BufferedReader(new InputStreamReader(fos));
            String line;
            String text = "";
            Gson gson = new GsonBuilder()
                    .setDateFormat("dd.MM.yyyy").create();

            TypeToken<List<Student>> token = new TypeToken<List<Student>>() {
            };
            while ((line = in.readLine()) != null) {
                text = text + line;
            }
            List<Student> students = gson.fromJson(text, token.getType());
            in.close();
            String response = null;
            for (int i = 0; i < students.size(); i++) {
                students.get(i).setAdditionalData("");
                students.get(i).setStatus(Status.AUSSTEHEND);
                students.get(i).setCost(currentCategoryCost);
                String jsonRequest = "{\"studentID\":" + "\""+students.get(i).getStudentID()+ "\"" + ",\"categoryID\":" + "\"" + currentCategoryID + "\"," + "\"firstname\":" + "\"" + students.get(i).getFirstname() + "\"," + "\"lastname\":" + "\"" + students.get(i).getLastname() + "\"," + "\"debts\":" + "\"" + currentCategoryCost + "\"," + "\"status\":" + "\"" + Status.AUSSTEHEND + "\"," + "\"additionalData\":" + "\" \"}";
                POSTRequest request_post = new POSTRequest(URL + "/createstudent.php?username=" + username + "&password=" + password);
                response = request_post.execute(jsonRequest).get();
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("message").equals("Student was created.")) {
                    activity2.fillItemsList();
                    System.out.println("Saved in Cloud and on SD");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(getActivity(), "loaded", Toast.LENGTH_SHORT).show();
    }

    public void saveStudents() {
        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            writer();
        }
    }

    public void writer() {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) return;
        File outFile = getActivity().getExternalFilesDir(null);
        String path = outFile.getAbsolutePath();
        String fullPath = path + File.separator + filename;

        try {

            FileOutputStream fos = new FileOutputStream(fullPath);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fos));
            Gson gson = new GsonBuilder()
                    .setDateFormat("dd.MM.yyyy").create();

            String sJson = gson.toJson(students);

            out.print(sJson);

            out.flush();
            out.close();




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();

    }

    public void createNewStudent(int currentCategoryID, float currentCategoryCost) {
        this.currentCategoryID = currentCategoryID;
        this.currentCategoryCost = currentCategoryCost;

        final View vDialog = getLayoutInflater().inflate(R.layout.add_student, null);
        EditText et_cost= vDialog.findViewById(R.id.studentCost_plainText);
        et_cost.setText(currentCategoryCost+"");
        setUpDialog(vDialog, -1);
    }

    private void setUpDialog(final View vDialog, int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(vDialog).setPositiveButton("SUBMIT", (dialog, which) -> handleDialog(vDialog, pos)).setNegativeButton("CANCEL", null).show();
    }

    private void handleDialog(View vDialog, int pos) {

        EditText et_number = vDialog.findViewById(R.id.studentNumber_numberText);
        EditText et_firstName = vDialog.findViewById(R.id.studentFirstName_plainText);
        EditText et_lastName = vDialog.findViewById(R.id.studentSurname_plainText);
        EditText et_cost = vDialog.findViewById(R.id.studentCost_plainText);
        EditText et_data = vDialog.findViewById(R.id.studentData_plainText);

        String response = null;

        String jsonRequest = "{\"studentID\":" + "\"" + et_number.getText().toString() + "\"" + ",\"categoryID\":" + "\"" + currentCategoryID + "\"," + "\"firstname\":" + "\"" + et_firstName.getText().toString() + "\"," + "\"lastname\":" + "\"" + et_lastName.getText().toString() + "\"," + "\"debts\":" + "\"" + et_cost.getText().toString() + "\"," + "\"status\":" + "\"" + Status.AUSSTEHEND + "\"," + "\"additionalData\":" + "\"" + et_data.getText().toString() + " \"}";
        if (pos == -1) {
            POSTRequest request_post = new POSTRequest(URL + "/createstudent.php?username=" + username + "&password=" + password);
            try {
                response = request_post.execute(jsonRequest).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("message").equals("Student was created.")) {
                    students.add(new Student(Integer.parseInt(et_number.getText().toString()), currentCategoryID, et_firstName.getText().toString(), et_lastName.getText().toString(), Double.parseDouble(et_cost.getText().toString()), Status.AUSSTEHEND, et_data.getText().toString()));
                    sAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            students.get(pos).setStudentID(Integer.valueOf(et_number.getText().toString()));
            students.get(pos).setFirstname(et_firstName.getText().toString());
            students.get(pos).setLastname(et_lastName.getText().toString());
            students.get(pos).setCost(Float.valueOf(et_cost.getText().toString()));
            students.get(pos).setAdditionalData(et_data.getText().toString());
            PUTRequest request_post = new PUTRequest(URL + "/putstudent.php?username=" + username + "&password=" + password + "&categoryID=" + students.get(pos).getCategoryID() + "&studentID=" + students.get(pos).getStudentID());
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
                    loadStudents(currentCategoryID, currentCategoryCost);
                    sAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void setStudents(List<Student> students) {
        this.students = students;
        sAdapter = new StudentAdapter(getActivity(), R.layout.student_list, students);
        sAdapter.setDarkmode(darkmode);
        slistView.setAdapter(sAdapter);
        sAdapter.notifyDataSetChanged();
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Schreiben fehlgeschlagen", Toast.LENGTH_SHORT).show();
            } else {
                writer();
            }
        } else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Lesen fehlgeschlagen", Toast.LENGTH_SHORT).show();
            } else {
                loader();
            }
        }
    }

    public void notifySetChanged() {
        sAdapter.notifyDataSetChanged();
    }
}

