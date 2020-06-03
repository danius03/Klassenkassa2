package com.example.klassenkassa.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.klassenkassa.R;
import com.example.klassenkassa.data.Student;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;


public class DetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_detail, container, false);
        initializeViews(v);
        return v;
    }

    private void initializeViews(View view)
    {

    }

    public void showInformation(int pos, Student item)
    {

    }

}
