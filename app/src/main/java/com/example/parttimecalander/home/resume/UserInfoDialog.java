package com.example.parttimecalander.home.resume;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import com.example.parttimecalander.databinding.DialogUserInfoBinding;

public class UserInfoDialog extends Dialog {
    private Context context;
    DialogUserInfoBinding binding;

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
        binding.back.setOnClickListener(v->dismiss());

        binding.registerButton.setOnClickListener(v->changeData());
    }

    private void changeData(){
        //TODO: 입력받은 데이터 DB애 저장하기
        dismiss();
    }

}
