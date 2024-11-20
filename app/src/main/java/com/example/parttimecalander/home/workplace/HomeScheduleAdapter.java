package com.example.parttimecalander.home.workplace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;
import com.example.parttimecalander.calander.ScheduleAdapter;

import java.util.List;

public class HomeScheduleAdapter extends RecyclerView.Adapter<HomeScheduleAdapter.ViewHolder> {

    private List<WorkDaily> schedules;
    private WorkPlaceDatabase placeDatabase;
    private WorkPlaceDao placeDao;
    private Context context;

    public HomeScheduleAdapter(Context context, List<WorkDaily> schedules) {
        this.context = context;
        this.schedules = schedules;

        // Initialize the database and DAO
        placeDatabase = WorkPlaceDatabase.getDatabase(context);
        placeDao = placeDatabase.workPlaceDao();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkDaily schedule = schedules.get(position);
        new Thread(() -> {
            WorkPlace place = placeDao.getByID(schedule.placeId);
            if (place != null) {
                String placeName = place.placeName; // Assume WorkPlace has a getName() method

                // Update the UI on the main thread
                holder.itemView.post(() -> holder.scheduleTextView.setText(
                        placeName+ "   " + schedule.startTime.substring(11, 16) + " - " + schedule.endTime.substring(11, 16)
                ));
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView scheduleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            scheduleTextView = itemView.findViewById(R.id.schedule_text);
        }
    }
}