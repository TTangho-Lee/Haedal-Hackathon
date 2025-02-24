package com.example.parttimecalander.calander;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.data.WorkPlace;
import com.example.parttimecalander.R;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private final List<WorkPlace> workPlaces;
    public ScheduleAdapter(List<WorkPlace> workPlaces) {
        this.workPlaces = workPlaces;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View colorView;
        public TextView titleTextView,startDateTextView,endDateTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.workplace_color);
            titleTextView = itemView.findViewById(R.id.workplace_title);
            startDateTextView = itemView.findViewById(R.id.workplace_start);
            endDateTextView = itemView.findViewById(R.id.workplace_end);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_parttime, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position) {
        holder.titleTextView.setText(workPlaces.get(position).placeName);
        if(workPlaces.get(position).startDate==null || workPlaces.get(position).startDate.trim().isEmpty()){
            holder.startDateTextView.setText("0000-00-00");
        }else{
            holder.startDateTextView.setText(workPlaces.get(position).startDate);
        }
        if(workPlaces.get(position).endDate==null || workPlaces.get(position).endDate.trim().isEmpty()){
            holder.endDateTextView.setText("0000-00-00");
        }else{
            holder.endDateTextView.setText(workPlaces.get(position).endDate);
        }
    }

    @Override
    public int getItemCount() {
        return workPlaces.size();
    }
}
