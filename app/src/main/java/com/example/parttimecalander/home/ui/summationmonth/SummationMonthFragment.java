package com.example.parttimecalander.home.ui.summationmonth;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkDailyDatabase;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.MainActivity;
import com.example.parttimecalander.R;
import com.example.parttimecalander.home.workplace.WorkPlaceAdapter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SummationMonthFragment extends Fragment {

    private WorkDailyDatabase dailyDatabase;
    private WorkPlaceDatabase placeDatabase;
    private WorkDailyDao dailyDao;
    private WorkPlaceDao placeDao;
    private int year=2024;
    private int month=11;
    int dayOfWeekNumber;
    public int[][] time_calander = new int[6][7];
    List<RecyclerItem> items = new ArrayList<>();
    public RecyclerView recyclerView;
    private SummationMonthAdapter adapter;  // 어댑터 선언
    public TextView time_text;
    public static SummationMonthFragment newInstance() {
        return new SummationMonthFragment();
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summation_month, container, false);
        LocalDate firstDay = LocalDate.of(year, month, 1);
        dayOfWeekNumber = firstDay.getDayOfWeek().getValue() - 1;
        time_text=view.findViewById(R.id.time_text);
        dailyDatabase = WorkDailyDatabase.getDatabase(getContext());
        dailyDao = dailyDatabase.workDailyDao();
        placeDatabase = WorkPlaceDatabase.getDatabase(getContext());
        placeDao = placeDatabase.workPlaceDao();

        Executors.newSingleThreadExecutor().execute(() -> {
            double all_time=0;
            double all_money=0;
            List<WorkPlace> placeList = placeDao.getDataAll();
            List<WorkDaily> dailyList = dailyDao.getDataAll();
            for (int i = 0; i < placeList.size(); i++) {
                WorkPlace place = placeList.get(i);
                for (int ii = 0; ii < 6; ii++) {
                    for (int j = 0; j < 7; j++) {
                        time_calander[ii][j] = 0;
                    }
                }

                for (int j = 0; j < dailyList.size(); j++) {
                    WorkDaily dailyWork = dailyList.get(j);
                    if (dailyWork.placeId == place.ID) {
                        LocalDateTime startTime = LocalDateTime.parse(dailyWork.startTime, formatter);
                        LocalDateTime endTime = LocalDateTime.parse(dailyWork.endTime, formatter);
                        if (startTime.getYear() == year && startTime.getMonthValue() == month) {
                            set_time(startTime.getDayOfMonth(), (int) Duration.between(startTime, endTime).getSeconds());
                        }
                    }
                }

                int[][] new_calander = new int[6][7];
                for (int ii = 0; ii < 6; ii++) {
                    System.arraycopy(time_calander[ii], 0, new_calander[ii], 0, 7);
                }
                RecyclerItem new_item = new RecyclerItem(place.placeName, new_calander, place.isJuhyu, place.usualPay);
                items.add(new_item);
                double normal_hour=0;
                double over_hour=0;
                for(int ii=0;ii<6;ii++){
                    double second=0;
                    for(int j=0;j<7;j++){
                        second+=new_item.worked_time[ii][j];
                    }
                    second/=3600;
                    if(second>=15&&new_item.juhyu){
                        normal_hour+=15;
                        over_hour+=second-15;
                    }
                    else{
                        normal_hour+=second;
                    }
                }
                all_time+=normal_hour+over_hour;
                all_money+=normal_hour*new_item.pay+over_hour*new_item.pay*1.5;

            }

            // UI 스레드에서 RecyclerView 업데이트
            double finalAll_time = all_time;
            double finalAll_money = all_money;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    time_text.setText(finalAll_time +"시간 일하고\n"+ finalAll_money +"원 벌었어요");
                    recyclerView = view.findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    // 어댑터 설정
                    if (items != null && !items.isEmpty()) {
                        adapter = new SummationMonthAdapter(items, (AppCompatActivity) requireActivity());
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.e("SummationMonthFragment", "No data available for adapter");
                    }
                }
            });
        });


        return view;
    }


    public void set_time(int day, int worked_time) {
        int d = day + dayOfWeekNumber - 1;
        time_calander[d / 7][d % 7] += worked_time;
    }
}
