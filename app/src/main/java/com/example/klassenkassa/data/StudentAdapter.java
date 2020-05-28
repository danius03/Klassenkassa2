package com.example.klassenkassa.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.klassenkassa.R;

import java.util.List;

public class StudentAdapter extends BaseAdapter {
    private List<Student> students;
    private int layoutId;
    private LayoutInflater inflater;

    public StudentAdapter(Context ctx, int layoutId, List<Student> students) {
        this.students = students;
        this.layoutId = layoutId;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Student s = students.get(position);
        View listItem = (convertView == null) ?inflater.inflate(this.layoutId, null) : convertView;
        ((TextView) (listItem.findViewById(R.id.studentName_textView))).setText(s.getLastname()+" "+s.getFirstname());
        ((TextView) (listItem.findViewById(R.id.studentNumber_textView))).setText(s.getStudentID());
        ((TextView) (listItem.findViewById(R.id.studentValues_textView))).setText(s.getCost()+"");
        ((TextView) (listItem.findViewById(R.id.studentStatus_textView))).setText(s.getStatus()+"");
        return listItem;
    }
}
