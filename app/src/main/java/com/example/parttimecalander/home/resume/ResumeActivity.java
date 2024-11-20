package com.example.parttimecalander.home.resume;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parttimecalander.Database.User;
import com.example.parttimecalander.R;
import com.example.parttimecalander.databinding.ActivityResumeBinding;

public class ResumeActivity extends AppCompatActivity {
    private ActivityResumeBinding binding;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        binding = ActivityResumeBinding.inflate((getLayoutInflater()));
        setContentView(binding.getRoot());
        loadDataFromDatabase();
        updateUI();
    }

    private void loadDataFromDatabase(){
        //TODO: 데이터베이스에서 유저 데이터 받아오기
    }
    private void updateUI(){
        if(user != null){
            /*TODO: 채우기
            binding.name.setText(user.name);
            binding.birth.setText();
            binding.phone.setText();
            binding.email.setText();
            binding.address.setText();
            */
        }

        //개인정보 수정
        binding.editPerson.setOnClickListener(v -> showInfoDialog());
        //학력 추가
        binding.registerEdu.setOnClickListener(v->{
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        });
        //자격증 추가
        binding.registerCert.setOnClickListener(v->{

        });
    }

    private void showInfoDialog(){
        UserInfoDialog userInfoDialog = new UserInfoDialog(this);
        userInfoDialog.show();
    }

}