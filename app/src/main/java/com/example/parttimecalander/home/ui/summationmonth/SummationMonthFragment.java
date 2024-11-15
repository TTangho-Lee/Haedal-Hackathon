package com.example.parttimecalander.home.ui.summationmonth;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

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
    List<RecyclerItem> items=new ArrayList<>();
    public RecyclerView recyclerView;


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
            array[i] = new information();
            array[i].place_id=placeList.get(i).ID;
            array[i].num_of_day=getDaysPerWeek(year,month);
            array[i].time_per_day=new int[32];
            for(int j=0;j<32;j++){
                array[i].time_per_day[j]=0;
            }
            for(int j=0;j<workList.size();j++){
                if(workList.get(j).placeId==array[i].place_id){
                    array[i].time_per_day[LocalDateTime.parse(workList.get(j).startTime,formatter).getDayOfMonth()]
                            +=(int) Duration.between(LocalDateTime.parse(workList.get(j).startTime,formatter),LocalDateTime.parse(workList.get(j).endTime,formatter)).getSeconds();
                }
            }
        }
        //array배열에 각각 근무지 아이디와 각 주마다 며칠씩 있는지, 해당 근무지에서 1일~30(31)일까지 각각 몇초 일했는지 저장해놓음


        int all_time=0;                                                                //전체 시간
        double all_money=0;                                                            //전체 돈
        for(int i=0;i<placeList.size();i++){
            String placeName=placeDao.getByID(array[i].place_id).placeName;            //근무지명
            int normal_money=0;                                                     //기본수당
            int over_money=0;                                                       //주휴수당
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
                over_money= (int) (over_second*pay*1.5/60/60);
            }
            else{
                normal_money=(normal_second+over_second)*pay/60/60;
            }
            items.add(new RecyclerItem(placeName,normal_second,over_second,normal_money,over_money));
        }
        viewModel = new ViewModelProvider(this).get(SummationViewModel.class);

        // TODO: Use the ViewModel
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_summation_month, container, false);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // 어댑터 설정을 여기에 넣습니다.
        SummationMonthAdapter adapter = new SummationMonthAdapter(items);
        recyclerView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PieChart pieChart = view.findViewById(R.id.pie_chart_money);



    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PieChart pieChart = view.findViewById(R.id.pie_chart_money);



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

        // 시작 날짜를 월의 첫날로 설정
        LocalDate startOfWeek = firstDayOfMonth;

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

    public void setPieChart(PieChart pieChart){
        //TODO : 파이차트 데이터 채우기
        //sample data, 주별 월급 항목으로 파이차트 만들 것
        // 예시 데이터: 주별 월급 항목 (각 항목의 값은 예시값)
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(5000f, "Week 1"));
        entries.add(new PieEntry(4000f, "Week 2"));
        entries.add(new PieEntry(6000f, "Week 3"));
        entries.add(new PieEntry(3000f, "Week 4"));

        // 데이터 세트 설정
        PieDataSet dataSet = new PieDataSet(entries, "Weekly Salary");
        // 색상 설정 (각 항목에 맞는 색상)
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);  // 또는 직접 색상 배열 설정 가능

        // PieData 설정
        PieData pieData = new PieData(dataSet);

        // PieChart에 데이터 설정
        pieChart.setData(pieData);

        // 그래프 애니메이션
        pieChart.animateY(1000);

        // 불필요한 항목 숨기기 (원형 그래프에서 텍스트나 레이블을 숨기고 싶을 경우)
        pieChart.setUsePercentValues(true);  // 퍼센트 표시
        pieChart.setEntryLabelColor(Color.WHITE);  // 레이블 색상
        pieChart.setEntryLabelTextSize(12f);  // 레이블 글씨 크기

        // 파이차트 설정 (예: 범례, 홀 색상 등)
        pieChart.getLegend().setEnabled(true);  // 범례 표시
        pieChart.setDrawHoleEnabled(true);  // 파이차트 가운데 구멍 그리기 (옵션)
        pieChart.setHoleColor(Color.TRANSPARENT);  // 가운데 구멍 색상 투명하게 설정
    }
}
