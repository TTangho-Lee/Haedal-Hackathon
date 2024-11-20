package com.example.parttimecalander.home.workplace;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.parttimecalander.R;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<String> items;

    GradientDrawable border = new GradientDrawable();

    public SpinnerAdapter(Context context, List<String> items) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        }

        border.setColor(Color.WHITE); // 배경색
        //border.setStroke(2, Color.BLACK); // 테두리 두께와 색상
        border.setCornerRadius(20);
        TextView textView = convertView.findViewById(R.id.text);
        textView.setBackground(border);
        textView.setText(items.get(position));

        return convertView;
    }
}
