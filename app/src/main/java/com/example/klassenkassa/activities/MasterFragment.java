package com.example.klassenkassa.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.klassenkassa.R;
import com.example.klassenkassa.other.OnSelectionChangedListener;


public class MasterFragment extends Fragment {
    private OnSelectionChangedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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


    }
}
