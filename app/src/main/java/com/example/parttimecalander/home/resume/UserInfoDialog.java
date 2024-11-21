package com.example.parttimecalander.home.resume;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Database.UserDatabase;
import com.example.parttimecalander.Database.User;
import com.example.parttimecalander.R;
import com.example.parttimecalander.databinding.DialogUserInfoBinding;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class UserInfoDialog extends Dialog {
    private Context context;
    DialogUserInfoBinding binding;
    private ArrayList<Integer> years;
    private ArrayList<Integer> months;
    private ArrayList<Integer> daysInMonth;
    private ArrayAdapter<Integer> dayAdapter;
    int year,month,day;

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
        Calendar calendar = Calendar.getInstance();
        Executors.newSingleThreadExecutor().execute(() -> {
            UserDatabase userDatabase=UserDatabase.getDatabase(context);
            UserDao userDao= userDatabase.userDao();

            if(!userDao.getDataAll().isEmpty()){
                binding.contentName.setText(userDao.getDataAll().get(0).name);
                binding.contentPhone.setText(userDao.getDataAll().get(0).phone);
                binding.contentEmail.setText(userDao.getDataAll().get(0).email);
                binding.contentAddress.setText(userDao.getDataAll().get(0).address);
                year=userDao.getDataAll().get(0).birthYear;
                month=userDao.getDataAll().get(0).birthMonth;
                day=userDao.getDataAll().get(0).birthDay;
            }
            else{
                year = calendar.get(Calendar.YEAR);           // 현재 년도
                month = calendar.get(Calendar.MONTH) + 1;    // 현재 월 (0부터 시작하므로 +1 필요)
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                updateUI(year,month,day);
            });
        });
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    }

    private void updateUI(int year,int month,int day){
        binding.titleBox.setOnClickListener(v->dismiss());

        setSpinner(year,month,day);

        binding.registerButton.setOnClickListener(v->changeData());
    }

    private void changeData(){
        Executors.newSingleThreadExecutor().execute(() -> {
            UserDatabase userDatabase=UserDatabase.getDatabase(context);
            UserDao userDao= userDatabase.userDao();
            if(!userDao.getDataAll().isEmpty()){
                User user=userDao.getDataAll().get(0);
                user.name=binding.contentName.getText().toString();
                user.phone=binding.contentPhone.getText().toString();
                user.email=binding.contentEmail.getText().toString();
                user.address=binding.contentAddress.getText().toString();
                user.birthYear=Integer.parseInt(binding.contentBirthyear.getSelectedItem().toString());
                user.birthMonth=Integer.parseInt(binding.contentBirthmonth.getSelectedItem().toString());
                user.birthDay=Integer.parseInt(binding.contentBirthday.getSelectedItem().toString());
                userDao.setUpdateData(user);
            }else{
                User user=new User();
                user.name=binding.contentName.getText().toString();
                user.phone=binding.contentPhone.getText().toString();
                user.email=binding.contentEmail.getText().toString();
                user.address=binding.contentAddress.getText().toString();
                user.birthYear=Integer.parseInt(binding.contentBirthyear.getSelectedItem().toString());
                user.birthMonth=Integer.parseInt(binding.contentBirthmonth.getSelectedItem().toString());
                user.birthDay=Integer.parseInt(binding.contentBirthday.getSelectedItem().toString());
                user.money=0;
                user.goal=0;
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                user.recentUpdate=now.format(formatter);
                userDao.setInsertData(user);
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                dismiss();
            });
        });

    }

    private void setSpinner(int year,int month,int day){
        // 연도 스피너
        years = new ArrayList<>();
        for (int i = Calendar.getInstance().get(Calendar.YEAR) - 50; i <= Calendar.getInstance().get(Calendar.YEAR) + 1; i++) {
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
        binding.contentBirthyear.setSelection(year-Calendar.getInstance().get(Calendar.YEAR)+50);
        binding.contentBirthmonth.setSelection(month-1);
        binding.contentBirthday.setSelection(day-1);
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
