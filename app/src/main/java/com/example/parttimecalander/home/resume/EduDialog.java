package com.example.parttimecalander.home.resume;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Database.PartTimeDatabase;
import com.example.parttimecalander.Database.data.User;
import com.example.parttimecalander.databinding.DialogEduBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;

public class EduDialog  extends Dialog {
    Context context;
    DialogEduBinding binding;
    private ArrayList<Integer> years;
    private ArrayList<Integer> months;
    private int baseYear = 2024;
    public EduDialog(Context context){
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.setCancelable(true);
        binding = DialogEduBinding.inflate(getLayoutInflater());
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
        String data="";
        data=data.concat(binding.contentSchool.getText().toString());
        data=data.concat(" ");
        data=data.concat(binding.contentMajor.getText().toString());
        data=data.concat("///(");
        data=data.concat(binding.contentAdmissionYear.getSelectedItem().toString());
        data=data.concat(".");
        data=data.concat(binding.contentAdmissionMonth.getSelectedItem().toString());
        data=data.concat(" ~ ");

        data=data.concat(binding.contentGraduateYear.getSelectedItem().toString());
        data=data.concat(".");
        data=data.concat(binding.contentGraduateMonth.getSelectedItem().toString());
        data=data.concat(")\n");
        String finalData = data;
        Executors.newSingleThreadExecutor().execute(() -> {
            PartTimeDatabase partTimeDatabase=PartTimeDatabase.getDatabase(context);
            UserDao userDao= partTimeDatabase.userDao();
            if(userDao.getDataAll().isEmpty()){
                User user=new User();
                user.schoolList="";
                user.schoolList=user.schoolList.concat(finalData);
                userDao.setInsertData(user);
            }
            else{
                User user=userDao.getDataAll().get(0);
                if(user.schoolList==null){
                    user.schoolList="";
                }
                user.schoolList=user.schoolList.concat(finalData);
                userDao.setUpdateData(user);
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                dismiss();
            });
        });
    }

    private void setSpinner(){
        baseYear = Calendar.getInstance().get(Calendar.YEAR);
        // 연도 스피너
        years = new ArrayList<>();
        for (int i = baseYear - 50; i <= baseYear + 50; i++) {
            years.add(i);
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contentAdmissionYear.setAdapter(yearAdapter);
        binding.contentGraduateYear.setAdapter(yearAdapter);

        // 월 스피너 설정
        months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(i);
        }
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contentAdmissionMonth.setAdapter(monthAdapter);
        binding.contentGraduateMonth.setAdapter(monthAdapter);
    }

}
