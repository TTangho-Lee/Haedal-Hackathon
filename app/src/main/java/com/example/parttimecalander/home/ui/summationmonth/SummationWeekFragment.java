package com.example.parttimecalander.home.ui.summationmonth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private WorkDailyDatabase dailyDatabase;
    private WorkPlaceDatabase placeDatabase;
    private WorkDailyDao dailyDao;
    private WorkPlaceDao placeDao;
    private int year;
    private int month;
    private SummationViewModel viewModel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public SummationMonthFragment.information[] array=new SummationMonthFragment.information[1000];

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
        dailyDatabase= Room.databaseBuilder(
                        requireContext(), WorkDailyDatabase.class,"dailyDatabase")
                .allowMainThreadQueries()
                .build();
        dailyDao=dailyDatabase.workDailyDao();
        placeDatabase= Room.databaseBuilder(
                        requireContext(), WorkPlaceDatabase.class,"placeDatabase")
                .allowMainThreadQueries()
                .build();
        placeDao=placeDatabase.workPlaceDao();

        int year=2024;
        int month=10;
        int placeID=1;

        List<WorkPlace> placeList=placeDao.getDataAll();
        List<WorkDaily> workList=dailyDao.getDataAll();

        for(int i=0;i<placeList.size();i++){
            array[i].place_id=placeList.get(i).ID;
            array[i].num_of_day=getDaysPerWeek(year,month);
            array[i].time_per_day=new int[31];
            for(int j=0;j<31;j++){
                array[i].time_per_day[j]=0;
            }
            for(int j=0;j<workList.size();j++){
                if(workList.get(j).placeId==array[i].place_id){
                    array[i].time_per_day[LocalDateTime.parse(workList.get(j).startTime,formatter).getDayOfMonth()]
                            +=(int) Duration.between(LocalDateTime.parse(workList.get(j).startTime,formatter),LocalDateTime.parse(workList.get(j).startTime,formatter)).getSeconds();
                }
            }
        }
        //array배열에 각각 근무지 아이디와 각 주마다 며칠씩 있는지, 해당 근무지에서 1일~30(31)일까지 각각 몇초 일했는지 저장해놓음


        for(int i=0;i<placeList.size();i++){

        }












    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summation_week, container, false);
    }
    public static class information{
        int place_id;
        int[] num_of_day;
        int[] time_per_day;
    }
    public static int[] getDaysPerWeek(int year, int month) {
        // YearMonth 객체 생성
        YearMonth yearMonth = YearMonth.of(year, month);

        // 월의 첫날과 마지막 날
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        // 주 계산을 위한 설정 (Locale에 따라 주 시작 요일이 다를 수 있음)
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // 첫 주의 시작 날짜 (월요일 기준)
        LocalDate startOfWeek = firstDayOfMonth.with(weekFields.dayOfWeek(), DayOfWeek.MONDAY.getValue());

        // 주별 날짜 수를 저장할 리스트
        List<Integer> daysInWeeks = new ArrayList<>();

        // 주별 날짜 수 계산
        LocalDate currentStartDate = startOfWeek;
        while (!currentStartDate.isAfter(lastDayOfMonth)) {
            // 해당 주의 종료 날짜 (6일 후 또는 월의 마지막 날 중 작은 값)
            LocalDate endOfWeek = currentStartDate.plusDays(6).isAfter(lastDayOfMonth)
                    ? lastDayOfMonth
                    : currentStartDate.plusDays(6);

            // 주에 포함된 날짜 수 계산
            int daysInWeek = (int) java.time.temporal.ChronoUnit.DAYS.between(currentStartDate, endOfWeek) + 1;
            daysInWeeks.add(daysInWeek);

            // 다음 주로 이동
            currentStartDate = currentStartDate.plusWeeks(1);
        }

        // 리스트를 배열로 변환하여 반환
        return daysInWeeks.stream().mapToInt(i -> i).toArray();
    }
}
