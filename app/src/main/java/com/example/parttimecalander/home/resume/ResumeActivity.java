package com.example.parttimecalander.home.resume;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
        binding.pdfConvert.setOnClickListener(v->convertToPdf());

        binding.profileImg.setOnClickListener(v->openGallery());
        //개인정보 수정
        binding.containerPerson.setOnClickListener(v -> showInfoDialog());
        //학력 추가
        binding.registerEdu.setOnClickListener(v -> showEduDialog());
        //자격증 추가
        binding.registerCert.setOnClickListener(v->showCertDialog());
    }
    private void convertToPdf() {
        // 버튼을 invisible로 만들어 숨기기
        binding.pdfConvert.setVisibility(View.INVISIBLE);
        binding.registerCert.setVisibility(View.INVISIBLE);
        binding.registerEdu.setVisibility(View.INVISIBLE);
        binding.registerButton.setVisibility(View.INVISIBLE);
        binding.editPerson.setVisibility(View.INVISIBLE);
        // 화면 캡처 후 PDF로 저장
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            savePdfToDownloadsAndroidQ();
        } else {
            savePdfToDownloadsLegacy();
        }
    }

    private void savePdfToDownloadsAndroidQ() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "captured_screen.pdf");  // 파일 이름
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);  // 다운로드 폴더

        // URI 가져오기
        Uri contentUri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
        if (contentUri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(contentUri)) {
                if (outputStream != null) {
                    // 비트맵을 PDF로 저장
                    saveBitmapAsPdf(outputStream);
                    Toast.makeText(this, "PDF가 다운로드 폴더에 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "PDF 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        // 캡처 후 버튼을 visible로 되돌리기
        binding.pdfConvert.setVisibility(View.VISIBLE);
        binding.registerCert.setVisibility(View.VISIBLE);
        binding.registerEdu.setVisibility(View.VISIBLE);
        binding.registerButton.setVisibility(View.VISIBLE);
        binding.editPerson.setVisibility(View.VISIBLE);
    }

    private void savePdfToDownloadsLegacy() {
        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsFolder, "captured_screen.pdf");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            // 비트맵을 PDF로 저장
            saveBitmapAsPdf(fos);
            Toast.makeText(this, "PDF가 다운로드 폴더에 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }

        // 캡처 후 버튼을 visible로 되돌리기
        binding.pdfConvert.setVisibility(View.VISIBLE);
    }

    private void saveBitmapAsPdf(OutputStream outputStream) {
        try {
            // PDF 문서 생성
            android.graphics.pdf.PdfDocument document = new android.graphics.pdf.PdfDocument();
            android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(1080, 1920, 1).create();
            android.graphics.pdf.PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Bitmap bitmap = getActivityBitmap(); // 액티비티 전체 캡처 비트맵
            canvas.drawBitmap(bitmap, 0, 0, null);

            document.finishPage(page);
            document.writeTo(outputStream);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 액티비티 전체를 캡처하는 메소드
    private Bitmap getActivityBitmap() {
        // 액티비티의 전체 화면을 캡처하는 비트맵을 생성
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true); // 캡처를 위한 DrawingCache 활성화
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache()); // 비트맵 생성
        rootView.setDrawingCacheEnabled(false); // DrawingCache 비활성화
        return bitmap;
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