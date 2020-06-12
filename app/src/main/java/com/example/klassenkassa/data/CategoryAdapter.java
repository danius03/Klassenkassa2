package com.example.klassenkassa.data;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.klassenkassa.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private List<Category> categories;
    private int layoutId;
    private LayoutInflater inflater;
    private boolean darkmode;

    public CategoryAdapter(Context ctx, int layoutId, List<Category> categories) {
        this.categories = categories;
        this.layoutId = layoutId;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category c= categories.get(position);
        View listItem = (convertView == null) ?inflater.inflate(this.layoutId, null) : convertView;
        ((TextView) (listItem.findViewById(R.id.categoryName_textView))).setText(c.getName());
        ((TextView) (listItem.findViewById(R.id.dueDate_textView))).setText(c.getDueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        ((TextView) (listItem.findViewById(R.id.cost_textView))).setText(c.getCost()+"€");
        if(darkmode)
        {
            ((TextView) (listItem.findViewById(R.id.categoryName_textView))).setTextColor(Color.WHITE);
            ((TextView) (listItem.findViewById(R.id.dueDate_textView))).setTextColor(Color.WHITE);
            ((TextView) (listItem.findViewById(R.id.cost_textView))).setTextColor(Color.WHITE);
        }else
        {
            ((TextView) (listItem.findViewById(R.id.categoryName_textView))).setTextColor(Color.BLACK);
            ((TextView) (listItem.findViewById(R.id.dueDate_textView))).setTextColor(Color.BLACK);
            ((TextView) (listItem.findViewById(R.id.cost_textView))).setTextColor(Color.BLACK);
        }
        return listItem;
    }

    public boolean isDarkmode() {
        return darkmode;
    }

    public void setDarkmode(boolean darkmode) {
        this.darkmode = darkmode;
    }
}
