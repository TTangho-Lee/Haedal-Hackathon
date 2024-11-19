package com.example.parttimecalander.home.workplace;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.parttimecalander.R;

import java.util.List;

public class SpinnerColorAdapter extends BaseAdapter {
    private Context context;
    private List<String> items;

    public SpinnerColorAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_color_item, parent, false);
        }

        View view = convertView.findViewById(R.id.circleView);
        view.setBackgroundColor(Color.parseColor(items.get(position)));

        return convertView;
    }
}
