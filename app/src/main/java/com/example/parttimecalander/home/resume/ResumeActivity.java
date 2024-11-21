package com.example.parttimecalander.home.resume;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Database.UserDatabase;
import com.example.parttimecalander.Database.User;
import com.example.parttimecalander.R;
import com.example.parttimecalander.databinding.ActivityResumeBinding;

import java.util.concurrent.Executors;

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
        UserDatabase userDatabase=UserDatabase.getDatabase(this);
        UserDao userDao=userDatabase.userDao();
        // LiveData 관찰
        userDao.getDataChange().observe(this, users -> {
            if (users != null && !users.isEmpty()) {

                user = users.get(0);
                updateUI();
            }
        });

    }
    private void updateUI(){
        if(user != null){
            binding.name.setText(user.name);
            binding.birth.setText(user.birthYear+"."+user.birthMonth+"."+user.birthDay);
            binding.phone.setText(user.phone);
            binding.email.setText(user.email);
            binding.address.setText(user.address);
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