package com.example.parttimecalander.home.scheduledialog;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Database.PartTimeDatabase;
import com.example.parttimecalander.Database.data.WorkDaily;
import com.example.parttimecalander.R;

import java.util.List;
import java.util.concurrent.Executors;

public class ScheduleDialogFragment extends DialogFragment {

    private static final String ARG_DATE = "selected_date";
    private Context mContext;
    private TimerDialogListener listener;
    RecyclerView scheduleRecycler;
    TextView dateTextView;


    // 인터페이스 정의
    public interface TimerDialogListener {
        void onTimeSet(String startTime, String endTime);
    }
    public static ScheduleDialogFragment newInstance(String selectedDate) {
        ScheduleDialogFragment fragment = new ScheduleDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, selectedDate);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context; // 액티비티의 Context 저장

        // 액티비티가 TimerDialogListener를 구현했는지 확인
        if (context instanceof TimerDialogListener) {
            listener = (TimerDialogListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement TimerDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_schedule, container, false);
        scheduleRecycler = view.findViewById(R.id.schedule_recycler);
        dateTextView = view.findViewById(R.id.date_text);

        loadDataFromDB();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null; // Context 참조 해제
    }
    private void loadDataFromDB(){
        String selectedDate = getArguments().getString(ARG_DATE, "");
        dateTextView.setText(selectedDate);

        // Fetch schedules for the selected date
        Executors.newSingleThreadExecutor().execute(() -> {

            PartTimeDatabase database = PartTimeDatabase.getDatabase(requireContext());
            WorkDailyDao dailyDao = database.workDailyDao();
            List<WorkDaily> schedules = dailyDao.getSchedulesForDate(selectedDate);

            requireActivity().runOnUiThread(() -> {
                updateUI(schedules);
            });
        });
    }
    private void updateUI(List<WorkDaily> schedules){
        HomeScheduleAdapter adapter = new HomeScheduleAdapter(mContext,schedules, new HomeScheduleAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(WorkDaily schedule) {
                // 아이템 클릭 시 액티비티로 데이터 전달
                if (listener != null) {
                    listener.onTimeSet(schedule.startTime, schedule.endTime);
                }
                dismiss();  // 다이얼로그 닫기
            }
        });

        scheduleRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        scheduleRecycler.setAdapter(adapter);
    }
}