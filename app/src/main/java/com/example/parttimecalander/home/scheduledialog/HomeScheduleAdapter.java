package com.example.parttimecalander.home.scheduledialog;

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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeScheduleAdapter extends RecyclerView.Adapter<HomeScheduleAdapter.ViewHolder> {

    private List<WorkDaily> schedules;
    private WorkPlaceDatabase placeDatabase;
    private WorkPlaceDao placeDao;
    private Context context;
    private OnItemClickListener listener;

    public HomeScheduleAdapter(Context context, List<WorkDaily> schedules, OnItemClickListener listener) {
        this.context = context;
        this.schedules = schedules;
        this.listener = listener;

        // Initialize the database and DAO
        placeDatabase = WorkPlaceDatabase.getDatabase(context);
        placeDao = placeDatabase.workPlaceDao();
    }


    // 아이템 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(WorkDaily schedule);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView scheduleTextView, startTimer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            scheduleTextView = itemView.findViewById(R.id.schedule_text);
            startTimer = itemView.findViewById(R.id.timer_button);
        }
        public void bind(WorkDaily schedule, WorkPlaceDao placeDao){
            // UI 초기화
            scheduleTextView.setText("Loading...");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                WorkPlace place = placeDao.getByID(schedule.placeId);
                if (place != null) {
                    String placeName = place.placeName;

                    scheduleTextView.post(() -> scheduleTextView.setText(
                            placeName + "   " + schedule.startTime.substring(11, 16) + " - " + schedule.endTime.substring(11, 16)
                    ));
                }
            });
        }
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
        holder.bind(schedules.get(position),placeDao);

        // 아이템 클릭 시 리스너 호출
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(schedule);  // 클릭된 schedule을 리스너에 전달
            }
        });
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }


}