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
    int baseYear;

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
        baseYear= calendar.get(Calendar.YEAR);
        Executors.newSingleThreadExecutor().execute(() -> {
            UserDatabase userDatabase=UserDatabase.getDatabase(context);
            UserDao userDao= userDatabase.userDao();
            setSpinner();
            if(!userDao.getDataAll().isEmpty()){
                binding.contentName.setText(userDao.getDataAll().get(0).name);
                binding.contentPhone.setText(userDao.getDataAll().get(0).phone);
                binding.contentEmail.setText(userDao.getDataAll().get(0).email);
                binding.contentAddress.setText(userDao.getDataAll().get(0).address);


                SpinnerAdapter adapter1 = binding.contentBirthyear.getAdapter();
                for (int i = 0; i < adapter1.getCount(); i++) {
                    if (Integer.parseInt(adapter1.getItem(i).toString())==userDao.getDataAll().get(0).birthYear) {
                        binding.contentBirthyear.setSelection(i);
                        break;
                    }
                }
                SpinnerAdapter adapter2 = binding.contentBirthmonth.getAdapter();
                for (int i = 0; i < adapter2.getCount(); i++) {
                    if (adapter2.getItem(i).equals(userDao.getDataAll().get(0).birthMonth)) {
                        binding.contentBirthmonth.setSelection(i);
                        break;
                    }
                }
                SpinnerAdapter adapter3 = binding.contentBirthday.getAdapter();
                for (int i = 0; i < adapter3.getCount(); i++) {
                    if (adapter3.getItem(i).equals(userDao.getDataAll().get(0).birthDay)) {
                        binding.contentBirthday.setSelection(i);
                        break;
                    }
                }
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                updateUI();
            });
        });
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    }

    private void updateUI(){
        binding.back.setOnClickListener(v->dismiss());

        setSpinner();

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
                userDao.setInsertData(user);
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                dismiss();
            });
        });

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
