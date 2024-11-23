package com.example.parttimecalander.home;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.R;

import java.util.List;

public class homeRecyclerviewAdapter extends RecyclerView.Adapter<homeRecyclerviewAdapter.MyViewHolder>{
    private List<String> dataList;

    public homeRecyclerviewAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_layout.xml 파일을 inflate
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_parttime, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // 데이터 바인딩
        String data = dataList.get(position);
        String[] parts = data.split("///");
        holder.wp_title.setText(parts[0]);
        holder.wp_start_date.setText(parts[1].substring(2,10).replace("-","."));
        holder.wp_end_date.setText(parts[1].substring(22,30).replace("-","."));
        holder.colorIndicator.setBackgroundColor(Color.parseColor(parts[2]));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder 클래스 정의
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView wp_title;
        TextView wp_start_date;
        TextView wp_end_date;
        View colorIndicator;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            wp_title = itemView.findViewById(R.id.workplace_title);
            wp_start_date = itemView.findViewById(R.id.workplace_start);
            wp_end_date = itemView.findViewById(R.id.workplace_end);
            colorIndicator = itemView.findViewById(R.id.workplace_color);
        }
    }
}
