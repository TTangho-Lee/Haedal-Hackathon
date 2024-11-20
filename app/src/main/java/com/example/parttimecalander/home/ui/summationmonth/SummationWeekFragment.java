package com.example.parttimecalander.home.ui.summationmonth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkDailyDatabase;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;

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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Use the [SummationWeekFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
public class SummationWeekFragment extends Fragment {

    RecyclerItem item;
    public String placeName;
    public double[][] money=new double[6][7];
    public double[] over_worked_money=new double[6];
    // TODO: Rename and change types of parameters
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String param1;
    private String param2;

    public SummationWeekFragment() {
        // Required empty public constructor
    }

    public static SummationWeekFragment newInstance(String param1, String param2) {
        SummationWeekFragment fragment = new SummationWeekFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param1 = getArguments().getString(ARG_PARAM1);
            param2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 전달된 데이터 받기
        if (getArguments() != null) {
            item = (RecyclerItem) getArguments().getSerializable("item");
        }

        // 레이아웃 설정
        View view = inflater.inflate(R.layout.fragment_summation_week, container, false);
        placeName=item.name;
        for(int i=0;i<6;i++){
            int week_worked_time=0;
            for(int j=0;j<7;j++){
                money[i][j]=item.worked_time[i][j]*item.pay/3600;
                week_worked_time+=item.worked_time[i][j];

            }
            if(week_worked_time/3600>=15&&item.juhyu){
                over_worked_money[i]=(week_worked_time/3600-15)*item.pay*0.5;
            }
            else{
                over_worked_money[i]=0;
            }
        }
        double[] week1=money[0];
        double[] week2=money[1];
        double[] week3=money[2];
        double[] week4=money[3];
        double[] week5=money[4];
        double[] week6=money[5];

        return view;
    }

}
