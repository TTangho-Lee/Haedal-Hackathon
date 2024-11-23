package com.example.parttimecalander.home.ui.summationmonth;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class SummationWeekFragment extends Fragment {

    RecyclerItem item;
    public String placeName;
    public double[][] money=new double[6][7];
    public double[] over_worked_money=new double[6];
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public TextView[] textViews=new TextView[6];
    public LinearLayout[] linearLayouts=new LinearLayout[6];
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
        textViews[0]=view.findViewById(R.id.week_1_text);
        textViews[1]=view.findViewById(R.id.week_2_text);
        textViews[2]=view.findViewById(R.id.week_3_text);
        textViews[3]=view.findViewById(R.id.week_4_text);
        textViews[4]=view.findViewById(R.id.week_5_text);
        textViews[5]=view.findViewById(R.id.week_6_text);
        linearLayouts[0]=view.findViewById(R.id.week_1_layout);
        linearLayouts[1]=view.findViewById(R.id.week_2_layout);
        linearLayouts[2]=view.findViewById(R.id.week_3_layout);
        linearLayouts[3]=view.findViewById(R.id.week_4_layout);
        linearLayouts[4]=view.findViewById(R.id.week_5_layout);
        linearLayouts[5]=view.findViewById(R.id.week_6_layout);
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
        String[] array={"월","화","수","목","금","토","일"};
        List<String> array2=getWeekRanges(item.year,item.month);
        Log.d("ssss",array2.size()+"");
        for(int i=0;i<array2.size();i++){
            double money_sum=0;
            double time_sum=0;
            TextView textView=new TextView(getContext());
            textView.setText("기본급");
            for(int j=0;j<7;j++){
                if(item.worked_time[i][j]!=0){
                    LinearLayout linearLayout=new LinearLayout(getContext());
                    TextView textView1=new TextView(getContext());
                    textView1.setText(array[j]+"요일");
                    textView1.setGravity(Gravity.LEFT);

                    TextView textView2=new TextView(getContext());
                    textView2.setText(money[i][j]+"원("+item.worked_time[i][j]/3600+"시간)");
                    textView2.setGravity(Gravity.RIGHT);

                    linearLayout.addView(textView1);
                    linearLayout.addView(textView2);
                    linearLayouts[i].addView(linearLayout);

                    money_sum+=money[i][j];
                    time_sum+=item.worked_time[i][j];
                }
            }
            textViews[i].setText(item.month+"월 "+(i+1)+"주"+" ("+array2.get(i)+")     "+money_sum+"원("+time_sum/3600+"시간)");
            if(over_worked_money[i]!=0){
                LinearLayout linearLayout=new LinearLayout(getContext());
                TextView textView1=new TextView(getContext());
                textView1.setText("주휴수당");
                textView1.setGravity(Gravity.LEFT);

                TextView textView2=new TextView(getContext());
                textView2.setText(over_worked_money[i]+"원");
                textView2.setGravity(Gravity.RIGHT);

                linearLayout.addView(textView1);
                linearLayout.addView(textView2);
                linearLayouts[i].addView(linearLayout);
            }


        }
        return view;
    }
    public static List<String> getWeekRanges(int year, int month) {
        // 해당 월의 첫 번째 날을 구합니다.
        LocalDate startDate = LocalDate.of(year, month, 1);

        // 해당 월의 마지막 날을 구합니다.
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 주 시작과 끝 날짜를 저장할 리스트
        List<String> weekRanges = new ArrayList<>();

        // 첫 번째 주는 시작일이 월요일이 아닐 수 있으므로 처리
        int startDayOfWeek = startDate.getDayOfWeek().getValue()-1; // 월요일은 1, 일요일은 7
        LocalDate firstWeekEnd = startDate.plusDays(6 - startDayOfWeek); // 첫 번째 주의 끝 날짜 계산

        if (firstWeekEnd.isAfter(endDate)) {
            firstWeekEnd = endDate; // 첫 번째 주가 월 말일 전에 끝나지 않으면 월말로 조정
        }

        // 첫 번째 주 범위 추가
        weekRanges.add(formatRange(startDate, firstWeekEnd));

        // 두 번째 주부터는 월요일부터 시작
        LocalDate currentStart = firstWeekEnd.plusDays(1);

        while (!currentStart.isAfter(endDate)) {
            // 한 주의 끝은 현재 날짜에 6일을 더한 날짜
            LocalDate currentEnd = currentStart.plusDays(6);
            if (currentEnd.isAfter(endDate)) {
                currentEnd = endDate; // 마지막 주는 월말로 끝날 수 있도록 조정
            }
            weekRanges.add(formatRange(currentStart, currentEnd));
            currentStart = currentEnd.plusDays(1);
        }

        return weekRanges;
    }

    // 날짜 범위를 "MM.dd~MM.dd" 형식으로 반환하는 메소드
    private static String formatRange(LocalDate start, LocalDate end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
        return start.format(formatter) + "~" + end.format(formatter);
    }
}
