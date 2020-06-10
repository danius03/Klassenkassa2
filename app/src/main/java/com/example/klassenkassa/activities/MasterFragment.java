package com.example.klassenkassa.activities;

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
import android.widget.ListView;

import com.example.klassenkassa.R;
import com.example.klassenkassa.data.Student;
import com.example.klassenkassa.data.StudentAdapter;
import com.example.klassenkassa.other.OnSelectionChangedListener;

import java.util.ArrayList;
import java.util.List;


public class MasterFragment extends Fragment {
    private OnSelectionChangedListener listener;
    private ListView slistView;
    private StudentAdapter sAdapter;
    List<Student> students;

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
    }

    private void delete(int position) {
    }

    private void edit(int position) {
    }

    public void loadStudents()
    {

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
