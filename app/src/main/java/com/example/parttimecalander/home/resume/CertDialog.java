package com.example.parttimecalander.home.resume;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.parttimecalander.databinding.DialogCertBinding;

import java.util.ArrayList;
import java.util.Calendar;


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
        //TODO: 입력받은 데이터 DB애 저장하기

        dismiss();
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
