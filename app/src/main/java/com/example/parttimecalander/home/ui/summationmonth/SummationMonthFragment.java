package com.example.parttimecalander.home.ui.summationmonth;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.parttimecalander.Database.Dao.UserDao;
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
import java.util.Map;

public class SummationMonthFragment extends Fragment {

    private WorkDailyDatabase dailyDatabase;
    private WorkPlaceDatabase placeDatabase;
    private WorkDailyDao dailyDao;
    private WorkPlaceDao placeDao;
    private int year;
    private int month;
    private SummationViewModel viewModel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public information[] array=new information[1000];

    public static SummationMonthFragment newInstance() {
        return new SummationMonthFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dailyDatabase= Room.databaseBuilder(
                        requireContext(),WorkDailyDatabase.class,"dailyDatabase")
                .allowMainThreadQueries()
                .build();
        dailyDao=dailyDatabase.workDailyDao();
        placeDatabase= Room.databaseBuilder(
                        requireContext(),WorkPlaceDatabase.class,"placeDatabase")
                .allowMainThreadQueries()
                .build();
        placeDao=placeDatabase.workPlaceDao();

        int year=2024;
        int month=10;

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

        int all_time=0;                                                                //전체 시간
        double all_money=0;                                                            //전체 돈
        for(int i=0;i<placeList.size();i++){
            String placeName=placeDao.getByID(array[i].place_id).placeName;            //근무지명
            double normal_money=0;                                                     //기본수당
            double over_money=0;                                                       //주휴수당
            int pay=placeDao.getByID(array[i].place_id).usualPay;
            int normal_second=0;
            int over_second=0;
            int date=1;
            for(int j=0;j<array[i].num_of_day.length;j++){
                int second_per_week=0;
                for(int k=0;k<array[i].num_of_day[j];k++){
                    second_per_week+=array[i].time_per_day[date++];
                }
                if(second_per_week>=54000){
                    normal_second+=54000;
                    over_second+=(second_per_week-54000);
                }
                else{
                    normal_second+=second_per_week;
                }
            }
            if(placeDao.getByID(array[i].place_id).isJuhyu){
                normal_money=normal_second*pay/60/60;
                over_money=over_second*pay*1.5/60/60;
            }
            else{
                normal_money=(normal_second+over_second)*pay/60/60;
                over_money=0;
            }
            all_time+=(normal_second+over_second)/60/60;
            all_money+=normal_money+over_money;
        }


        viewModel = new ViewModelProvider(this).get(SummationViewModel.class);

        // TODO: Use the ViewModel
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summation_month, container, false);
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
