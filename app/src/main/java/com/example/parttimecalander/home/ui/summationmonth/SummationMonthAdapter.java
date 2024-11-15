package com.example.parttimecalander.home.ui.summationmonth;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.R;

import java.util.List;

public class SummationMonthAdapter extends RecyclerView.Adapter<SummationMonthAdapter.ViewHolder>{


    private List<RecyclerItem> items;

    // 생성자
    public SummationMonthAdapter(List<RecyclerItem> items) {
        this.items = items;
    }

    // ViewHolder 정의
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,money1,money2,money3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            money1 = itemView.findViewById(R.id.money1);
            money2 = itemView.findViewById(R.id.money2);
            money3 = itemView.findViewById(R.id.money3);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.summation_month_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerItem item = items.get(position);

        // 데이터 바인딩
        holder.name.setText(item.name);
        holder.money1.setText(item.money1+"("+item.time1/3600+")");
        holder.money2.setText(item.money2+"("+item.time2/3600+")");
        holder.money3.setText(item.money1+item.money2+"("+(item.time1+item.time2)/3600+")");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
