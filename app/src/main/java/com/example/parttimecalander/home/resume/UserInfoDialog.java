package com.example.parttimecalander.home.resume;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.parttimecalander.R;
import com.example.parttimecalander.databinding.DialogUserInfoBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserInfoDialog extends Dialog {
    private Context context;
    DialogUserInfoBinding binding;
    private ArrayList<Integer> years;
    private ArrayList<Integer> months;
    private ArrayList<Integer> daysInMonth;
    private ArrayAdapter<Integer> dayAdapter;
    int baseYear = 2024;

    public UserInfoDialog(Context context){
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.setCancelable(true);
        binding = DialogUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        updateUI();
    }

    private void updateUI(){
        binding.titleBox.setOnClickListener(v->dismiss());

        setSpinner();

        binding.registerButton.setOnClickListener(v->changeData());
    }

    private void changeData(){
        //TODO: 입력받은 데이터 DB애 저장하기
        String name = String.valueOf(binding.contentName.getText());
        //int year = binding.contentBirthyear.getSelectedItem();

    }

    private void setSpinner(){
        // 연도 스피너
        years = new ArrayList<>();
        for (int i = baseYear - 50; i <= baseYear + 1; i++) {
            years.add(i);
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contentBirthyear.setAdapter(yearAdapter);

        // 월 스피너 설정
        months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(i);
        }
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contentBirthmonth.setAdapter(monthAdapter);

        //일 스피너 설정
        daysInMonth = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            daysInMonth.add(i);
        }
        dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, daysInMonth);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contentBirthday.setAdapter(dayAdapter);

        binding.contentBirthmonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int month = (int) binding.contentBirthmonth.getSelectedItem();
                int year = (int) binding.contentBirthyear.getSelectedItem();
                updateDaysInMonth(year, month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        binding.contentBirthyear.setSelection(years.indexOf(Calendar.getInstance().get(Calendar.YEAR)));
        binding.contentBirthmonth.setSelection(Calendar.getInstance().get(Calendar.MONTH));  // 0-11 범위, 1월은 0
        updateDaysInMonth(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1);
    }
    private void updateDaysInMonth(int year, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, 1);
        int daysInMonthCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        daysInMonth.clear();
        for (int i = 1; i <= daysInMonthCount; i++) {
            daysInMonth.add(i);
        }
        dayAdapter.notifyDataSetChanged();
    }


}
