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
