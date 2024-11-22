package com.example.parttimecalander.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                .inflate(R.layout.home_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // 데이터 바인딩
        String data = dataList.get(position);
        String[] parts = data.split("///");
        holder.textView.setText(parts[0]);
        holder.textView2.setText((parts[1].substring(2,10)+"~"+parts[1].substring(22,30)).replace("-","."));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder 클래스 정의
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView2;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.place_name);
            textView2=itemView.findViewById(R.id.work_time);
        }
    }
}
