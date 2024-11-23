package com.example.parttimecalander.home.ui.summationmonth;

import android.graphics.Color;
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
import com.example.parttimecalander.databinding.FragmentSummationWeekBinding;

import org.w3c.dom.Text;

import java.text.NumberFormat;
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

    private List<WorkDaily> workDailyList = new ArrayList<>();

    WorkDailyDatabase workDailyDatabase;
    WorkDailyDao workDailyDao;

    private int sumMoney;
    private int sumTime;

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

        workDailyDatabase = WorkDailyDatabase.getDatabase(getContext());
        workDailyDao = workDailyDatabase.workDailyDao();

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
        sumMoney = 0;
        sumTime  = 0;
        String[] array={"월","화","수","목","금","토","일"};
        List<String> array2=getWeekRanges(item.year,item.month);
        Log.d("ssss",array2.size()+"");
        for(int i=0;i<array2.size();i++) {
            double money_sum = 0;
            double time_sum = 0;
            TextView textView = new TextView(getContext());
            textView.setText("기본급");
            for(int j=0; j<7; j++) {
                if(item.worked_time[i][j] != 0) {
                    LinearLayout linearLayout = new LinearLayout(getContext());

                    TextView textView1 = new TextView(getContext());
                    textView1.setText(array[j] + "요일");
                    textView1.setGravity(Gravity.LEFT);
                    textView1.setTextColor(Color.BLACK);
                    TextView textView2 = new TextView(getContext());
                    String formattedMoney = String.format(Locale.getDefault(), "%,d", (int) money[i][j]); // 정수형 변환 및 천 단위 콤마 추가
                    String formattedTime = String.format(Locale.getDefault(), "%d", item.worked_time[i][j] / 3600); // 정수형 변환
                    textView2.setText(formattedMoney + "원(" + formattedTime + "시간)");
                    textView2.setGravity(Gravity.RIGHT);
                    textView2.setTextColor(Color.BLACK);
                    linearLayout.addView(textView1);
                    linearLayout.addView(textView2);
                    linearLayouts[i].addView(linearLayout);

                    money_sum += money[i][j];
                    time_sum += item.worked_time[i][j];
                }
            }
            sumMoney += (int)money_sum;
            sumTime  += (int)(time_sum / 3600);

            String formattedMoneySum = String.format(Locale.getDefault(), "%,d", (int) money_sum);
            String formattedTimeSum = String.format(Locale.getDefault(), "%d", (int) (time_sum / 3600));
            textViews[i].setText(item.month + "월 " + (i + 1) + "주" + " (" + array2.get(i) + ")     " + formattedMoneySum + "원(" + formattedTimeSum + "시간)");
            textViews[i].setTextColor(Color.BLACK);
            if(over_worked_money[i] != 0) {
                LinearLayout linearLayout = new LinearLayout(getContext());

                TextView textView1 = new TextView(getContext());
                textView1.setText("주휴수당");
                textView1.setGravity(Gravity.LEFT);
                textView1.setTextColor(Color.BLACK);

                TextView textView2 = new TextView(getContext());
                String formattedOverworkedMoney = String.format(Locale.getDefault(), "%,d", (int) over_worked_money[i]);
                textView2.setText(formattedOverworkedMoney + "원");
                textView2.setGravity(Gravity.RIGHT);
                textView2.setTextColor(Color.BLACK);

                linearLayout.addView(textView1);
                linearLayout.addView(textView2);
                linearLayouts[i].addView(linearLayout);
            }
            final int index = i;
            textViews[i].setOnClickListener(v -> {
                if (linearLayouts[index].getVisibility() == View.VISIBLE) {
                    linearLayouts[index].setVisibility(View.GONE);
                } else {
                    linearLayouts[index].setVisibility(View.VISIBLE);
                }
            });

            // Initially hide all layouts
            linearLayouts[i].setVisibility(View.GONE);
        }
        String formattedSum = NumberFormat.getNumberInstance(Locale.US).format(sumMoney);
        String sumTimeString = String.valueOf(sumTime);
        TextView sumTextView = view.findViewById(R.id.sumText);
        sumTextView.setText(formattedSum + "원" + "("  + sumTimeString +"시간" + ")");
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
