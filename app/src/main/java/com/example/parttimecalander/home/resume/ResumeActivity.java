package com.example.parttimecalander.home.resume;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

public class ResumeActivity extends AppCompatActivity {
    private ActivityResumeBinding binding;
    User user;
    private static final int PICK_IMAGE_REQUEST = 1;
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
            if(user.image!=null){
                Log.d("qqqq","qqqq");
                binding.profileImg.setBackground(byteArrayToDrawable(this,user.image));
            }
        }
        binding.profileImg.setOnClickListener(v->openGallery());
        //개인정보 수정
        binding.containerPerson.setOnClickListener(v -> showInfoDialog());
        //학력 추가
        binding.registerEdu.setOnClickListener(v -> showEduDialog());
        //자격증 추가
        binding.registerCert.setOnClickListener(v->showCertDialog());
    }

    private void showInfoDialog(){
        UserInfoDialog userInfoDialog = new UserInfoDialog(this);
        userInfoDialog.show();
    }
    private void showEduDialog(){
        EduDialog eduDialog = new EduDialog(this);
        eduDialog.show();
    }
    private void showCertDialog(){
        CertDialog certDialog = new CertDialog(this);
        certDialog.show();
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                saveImageToDatabase(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImageToDatabase(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageData = outputStream.toByteArray();

        new Thread(() -> {
            if(UserDatabase.getDatabase(this).userDao().getDataAll().isEmpty()){
                User user=new User();
                user.image=imageData;
                UserDatabase.getDatabase(this).userDao().setInsertData(user);
            }else{
                User user=UserDatabase.getDatabase(this).userDao().getDataAll().get(0);
                user.image=imageData;
                UserDatabase.getDatabase(this).userDao().setUpdateData(user);
            }
        }).start();
    }
    public static Drawable byteArrayToDrawable(Context context, byte[] imageData) {
        // byte[]를 Bitmap으로 변환
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

        // Bitmap을 Drawable로 변환
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}