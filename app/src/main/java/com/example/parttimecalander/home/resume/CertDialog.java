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
import com.example.parttimecalander.databinding.DialogCertBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;


public class CertDialog  extends Dialog {
    Context context;
    DialogCertBinding binding;
    private ArrayList<Integer> years;
    private ArrayList<Integer> months;
    private int baseYear = 2024;
    public CertDialog(Context context){
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.setCancelable(true);
        binding = DialogCertBinding.inflate(getLayoutInflater());
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
        data=data.concat(binding.contentCertName.getText().toString());
        data=data.concat(" ");
        data=data.concat(binding.contentCertGrade.getText().toString());
        data=data.concat("///(");
        data=data.concat(binding.contentCertYear.getSelectedItem().toString());
        data=data.concat(".");
        data=data.concat(binding.contentCertMonth.getSelectedItem().toString());
        data=data.concat(")\n");
        String finalData = data;
        Executors.newSingleThreadExecutor().execute(() -> {
            PartTimeDatabase partTimeDatabase=PartTimeDatabase.getDatabase(context);
            UserDao userDao= partTimeDatabase.userDao();
            if(userDao.getDataAll().isEmpty()){
                User user=new User();
                user.certList="";
                user.certList=user.certList.concat(finalData);
                userDao.setInsertData(user);
            }
            else{
                User user=userDao.getDataAll().get(0);
                if(user.certList==null){
                    user.certList="";
                }
                user.certList=user.certList.concat(finalData);
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
        for (int i = baseYear - 50; i <= baseYear + 1; i++) {
            years.add(i);
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contentCertYear.setAdapter(yearAdapter);

        // 월 스피너 설정
        months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(i);
        }
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contentCertMonth.setAdapter(monthAdapter);
    }
}
