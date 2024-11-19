package com.example.parttimecalander;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkDailyDatabase;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.databinding.FragmentSecondBinding;
import com.example.parttimecalander.home.HomeActivity;
import com.example.parttimecalander.home.ui.summationmonth.RecyclerItem;
import com.example.parttimecalander.home.ui.summationmonth.SummationMonthAdapter;
import com.example.parttimecalander.home.ui.summationmonth.SummationMonthFragment;
import com.example.parttimecalander.home.workplace.WorkPlaceActivity;
import com.example.parttimecalander.home.workplace.WorkPlaceAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SecondFragment extends Fragment {
    private FragmentSecondBinding binding;
    private WorkDailyDatabase dailyDatabase;
    private WorkPlaceDatabase placeDatabase;
    private WorkDailyDao dailyDao;
    private WorkPlaceDao placeDao;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Executors.newSingleThreadExecutor().execute(() -> {

            dailyDatabase=WorkDailyDatabase.getDatabase(getContext());
            dailyDao=dailyDatabase.workDailyDao();
            placeDatabase=WorkPlaceDatabase.getDatabase(getContext());
            placeDao=placeDatabase.workPlaceDao();

            WorkPlace workPlace1=new WorkPlace();
            workPlace1.placeName="GS25";
            workPlace1.isJuhyu=true;
            workPlace1.type="편의점";
            workPlace1.isExpanded=false;
            workPlace1.usualPay=10000;
            workPlace1.ColorHex="#888888";
            placeDao.setInsertData(workPlace1);

            WorkPlace workPlace2=new WorkPlace();
            workPlace2.placeName="CU";
            workPlace2.isJuhyu=true;
            workPlace2.type="편의점";
            workPlace2.isExpanded=false;
            workPlace2.usualPay=20000;
            workPlace2.ColorHex="#444444";
            placeDao.setInsertData(workPlace2);

            WorkDaily work1=new WorkDaily();
            work1.placeId=placeDao.getDataAll().get(0).ID;
            work1.startTime="2024-11-16 20:00:00";
            work1.endTime="2024-11-16 21:00:00";
            dailyDao.setInsertData(work1);

            WorkDaily work2=new WorkDaily();
            work2.placeId=placeDao.getDataAll().get(0).ID;
            work2.startTime="2024-11-17 20:00:00";
            work2.endTime="2024-11-17 22:00:00";
            dailyDao.setInsertData(work2);

            WorkDaily work3=new WorkDaily();
            work3.placeId=placeDao.getDataAll().get(1).ID;
            work3.startTime="2024-11-18 20:00:00";
            work3.endTime="2024-11-18 23:00:00";
            dailyDao.setInsertData(work3);

            binding.buttonSecond.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            });
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
