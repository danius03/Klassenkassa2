package com.example.klassenkassa.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.klassenkassa.R;
import com.example.klassenkassa.data.Student;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;


public class DetailFragment extends Fragment {

    private TextView txt_id;
    private TextView txt_name;
    private TextView txt_cost;
    private TextView txt_status;
    private TextView txt_add;

    private FrameLayout l;

    public boolean isDarkmode() {
        return darkmode;
    }

    public void setDarkmode(boolean darkmode) {
        this.darkmode = darkmode;
        changeTextColor();
    }

    private boolean darkmode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_detail, container, false);
        initializeViews(v);
        return v;
    }

    private void initializeViews(View view)
    {
        txt_id=view.findViewById(R.id.studentNumber_textView);
        txt_name=view.findViewById(R.id.studentName_textView);
        txt_cost=view.findViewById(R.id.studentValues_textView);
        txt_status=view.findViewById(R.id.studentStatus_textView);
        txt_add=view.findViewById(R.id.studentData_textView);
        l = view.findViewById(R.id.fragment_detail);
    }

    public void showInformation(int pos, Student item)
    {
        txt_id.setText(item.getStudentID()+"");
        txt_name.setText(item.getLastname()+" "+item.getFirstname());
        txt_cost.setText(item.getCost()+"€");
        txt_status.setText(item.getStatus().toString());
        txt_add.setText(item.getAdditionalData()+"");
    }

    public void changeTextColor()
    {
        if(darkmode)
        {
            txt_id.setTextColor(Color.WHITE);
            txt_name.setTextColor(Color.WHITE);
            txt_cost.setTextColor(Color.WHITE);
            txt_status.setTextColor(Color.WHITE);
            txt_add.setTextColor(Color.WHITE);
            l.setBackgroundColor(Color.DKGRAY);
        }else
        {
            txt_id.setTextColor(Color.BLACK);
            txt_name.setTextColor(Color.BLACK);
            txt_cost.setTextColor(Color.BLACK);
            txt_status.setTextColor(Color.BLACK);
            txt_add.setTextColor(Color.BLACK);
            l.setBackgroundColor(Color.WHITE);
        }
    }

    public void changeDarkmode(int brightness) {
        if (brightness > 100) {
            darkmode = true;
            changeTextColor();
        } else {
            darkmode = false;
            changeTextColor();
        }
    }
}
