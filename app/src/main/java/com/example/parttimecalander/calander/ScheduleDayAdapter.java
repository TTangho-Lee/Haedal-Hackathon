package com.example.parttimecalander.calander;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;

import java.util.List;

public class ScheduleDayAdapter extends RecyclerView.Adapter<ScheduleDayAdapter.ViewHolder>{
    private final List<Pair<WorkPlace, WorkDaily>> workPlaces;
    public ScheduleDayAdapter(List<Pair<WorkPlace, WorkDaily>> workPlaces)  {
        this.workPlaces = workPlaces;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View colorView;
        public TextView titleTextView,startDateTextView,endDateTextView,middleTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.workplace_color);
            titleTextView = itemView.findViewById(R.id.workplace_title);
            startDateTextView = itemView.findViewById(R.id.workplace_start);
            endDateTextView = itemView.findViewById(R.id.workplace_end);
            middleTextView = itemView.findViewById(R.id.workplace_middle);
        }
    }

    @NonNull
    @Override
    public ScheduleDayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_parttime, parent, false);
        return new ScheduleDayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String placeName = workPlaces.get(position).first.placeName;
        String startTime = workPlaces.get(position).second.startTime.split(" ")[1];
        String endTime = workPlaces.get(position).second.endTime.split(" ")[1];
        if(startTime == null || startTime.trim().isEmpty()) startTime = "00:00:00";
        if(endTime == null || endTime.trim().isEmpty()) endTime = "23:59:59";

        holder.titleTextView.setText(placeName);
        holder.titleTextView.setTextSize(20.0f);

        holder.startDateTextView.setText(startTime);
        holder.startDateTextView.setTextSize(16.f);

        holder.middleTextView.setTextSize(16.f);

        holder.endDateTextView.setText(endTime);
        holder.endDateTextView.setTextSize(16.f);
    }

    @Override
    public int getItemCount() {
        return workPlaces.size();
    }
}
