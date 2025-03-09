package com.example.parttimecalander.calander;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.data.WorkDaily;
import com.example.parttimecalander.Database.data.WorkPlace;
import com.example.parttimecalander.R;

import java.util.List;

public class ScheduleDayAdapter extends RecyclerView.Adapter<ScheduleDayAdapter.ViewHolder>{
    private final List<Pair<WorkPlace, WorkDaily>> workPlaces;
    private OnItemLongClickListener longClickListener;
    public interface OnItemLongClickListener {
        void onItemLongClick(Pair<WorkPlace, WorkDaily> item, int position);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }
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
        Pair<WorkPlace, WorkDaily> currentItem = workPlaces.get(position);
        String placeName = currentItem.first.placeName;

        String startTime = currentItem.second.startTime;
        String startTimeString = startTime.substring(11, 16);

        String endTime = currentItem.second.endTime;
        String endTimeString = endTime.substring(11, 16);

        holder.titleTextView.setText(placeName);
        holder.titleTextView.setTextSize(20.0f);

        holder.startDateTextView.setText(startTimeString+" ~ "+endTimeString);
        holder.startDateTextView.setTextSize(16.f);

        // 롱 클릭 이벤트 연결
        // 롱 클릭 이벤트 연결
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(currentItem, position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return workPlaces.size();
    }
}
