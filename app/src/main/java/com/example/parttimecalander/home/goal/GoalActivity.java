package com.example.parttimecalander.home.goal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Database.PartTimeDatabase;
import com.example.parttimecalander.Database.data.User;
import com.example.parttimecalander.R;
import com.example.parttimecalander.databinding.ActivityGoalBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Executors;


public class GoalActivity extends AppCompatActivity {

    private ActivityGoalBinding binding;

    private static final int PICK_IMAGE_REQUEST = 1;
    User user;
    Bitmap bitmap;
    byte[] imageData;
    String newNickname;
    String newAmount;

    long amountValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding 초기화
        binding = ActivityGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataFromDatabase();
        binding.back.setOnClickListener(v->onBackPressed());
        // editGoal 클릭 이벤트
        binding.editGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditGoalDialog();
            }
        });
        binding.goalImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGoal();
            }
        });
        Log.d("abcde","체크포인트");
    }

    private void loadDataFromDatabase(){
        PartTimeDatabase userDatabase=PartTimeDatabase.getDatabase(this);
        UserDao userDao=userDatabase.userDao();
        Executors.newSingleThreadScheduledExecutor().execute(()->{
            if(userDao.getDataAll().size() == 1){
                user = userDao.getDataAll().get(0);
            }
            else{
                user = new User();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            });
        });

    }

    private void updateUI(){
        Log.d("abcde","aefwefasefsaef");
        if(user != null){
            String formattedAmount = NumberFormat.getNumberInstance(Locale.getDefault()).format(user.goal);
            Log.d("abcde",formattedAmount);
            String formattedAmount2 = NumberFormat.getNumberInstance(Locale.getDefault()).format(user.goal - user.goalSaveMoney);
            if (user.goalImage != null) {
                binding.goalImg.setBackground(byteArrayToDrawable(this, user.goalImage));
            } else {
                binding.goalImg.setBackgroundResource(R.drawable.pencil_edit_button_svgrepo_com); // 기본 이미지 설정
            }
            binding.titleGoal.setText(user.goalName);
            binding.goalPrice.setText(formattedAmount + '원');
            binding.contentPrice.setText(formattedAmount2 + '원');
        }
    }
    // 현제 여기 하는중 저장버튼 눌렀을떄 이미지 말고 돈도 저장해주자
    private void saveGoal(){
        saveImageToDatabase();
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
                // 이미지 Bitmap 생성 및 회전 보정
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Bitmap rotatedBitmap = rotateImageIfRequired(this, originalBitmap, imageUri);

                // 최대 크기 제한 (1024 유지)
                int maxSize = 1024;
                int width = rotatedBitmap.getWidth();
                int height = rotatedBitmap.getHeight();
                float scale = Math.min((float) maxSize / width, (float) maxSize / height);

                // 크기 조정
                int targetWidth = Math.round(scale * width);
                int targetHeight = Math.round(scale * height);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, targetWidth, targetHeight, true);

                // Bitmap을 ByteArray로 변환
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                imageData = outputStream.toByteArray();

                // 화면에 표시
                binding.goalImg.setBackground(byteArrayToDrawable(this, imageData));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface exif = new ExifInterface(input);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private void saveImageToDatabase() {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        byte[] imageData = outputStream.toByteArray();

        new Thread(() -> {
            if(imageData != null) {
                user.goalImage = imageData;
            }
            if((int) amountValue != 0) {
                user.goal = (int) amountValue;
            }
            if(newNickname != null){
            user.goalName = newNickname;
            }
            if(PartTimeDatabase.getDatabase(this).userDao().getDataAll().isEmpty()){
                PartTimeDatabase.getDatabase(this).userDao().setInsertData(user);
            }else{
                PartTimeDatabase.getDatabase(this).userDao().setUpdateData(user);
            }
            runOnUiThread(() ->
                    Toast.makeText(GoalActivity.this, "저장되었습니다", Toast.LENGTH_SHORT).show()
            );
        }).start();
    }

    public static Drawable byteArrayToDrawable(Context context, byte[] imageData) {
        // byte[]를 Bitmap으로 변환
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

        // Bitmap을 Drawable로 변환
        return new BitmapDrawable(context.getResources(), bitmap);
    }
    private void showEditGoalDialog() {
        // 다이얼로그를 위한 커스텀 뷰 설정
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_goal, null);
        EditText editNickname = dialogView.findViewById(R.id.edit_nickname);
        EditText editAmount = dialogView.findViewById(R.id.edit_amount);

        // 다이얼로그 생성
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("목표 설정")
                .setView(dialogView)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        newNickname = editNickname.getText().toString().trim();
                        newAmount = editAmount.getText().toString().trim();

                        // 입력값 검증
                        if (!TextUtils.isEmpty(newNickname)) {
                            binding.titleGoal.setText(newNickname);
                        }
                        try {
                            // 숫자 형식으로 변환
                            amountValue = Long.parseLong(newAmount);
                            String formattedAmount = NumberFormat.getNumberInstance(Locale.getDefault()).format(amountValue);

                            binding.goalPrice.setText(formattedAmount + "원");
                        } catch (NumberFormatException e) {
                            binding.goalPrice.setText("Invalid amount");
                        }
                        //Todo 여기다가 목표금액 바꿔주는거 써주자 데베에서 유저 확인

                    }
                })
                .setNegativeButton("취소", null)
                .create();

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

