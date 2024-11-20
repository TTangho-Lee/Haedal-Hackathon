package com.example.parttimecalander.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Database.WorkDailyDatabase;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.R;
import com.example.parttimecalander.calander.ScheduleAdapter;
import com.example.parttimecalander.home.workplace.HomeScheduleAdapter;

import java.util.List;
import java.util.concurrent.Executors;

public class ScheduleDialogFragment extends DialogFragment {

    private static final String ARG_DATE = "selected_date";

    public static ScheduleDialogFragment newInstance(String selectedDate) {
        ScheduleDialogFragment fragment = new ScheduleDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, selectedDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_schedule, container, false);
        RecyclerView scheduleRecycler = view.findViewById(R.id.schedule_recycler);
        TextView dateTextView = view.findViewById(R.id.date_text);

        String selectedDate = getArguments().getString(ARG_DATE, "");
        dateTextView.setText(selectedDate);

        // Fetch schedules for the selected date
        Executors.newSingleThreadExecutor().execute(() -> {
            WorkDailyDatabase database = WorkDailyDatabase.getDatabase(requireContext());
            WorkDailyDao dailyDao = database.workDailyDao();
            List<WorkDaily> schedules = dailyDao.getSchedulesForDate(selectedDate);

            requireActivity().runOnUiThread(() -> {
                HomeScheduleAdapter adapter = new HomeScheduleAdapter(getContext(),schedules);
                scheduleRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
                scheduleRecycler.setAdapter(adapter);
            });
        });

        return view;
    }
}