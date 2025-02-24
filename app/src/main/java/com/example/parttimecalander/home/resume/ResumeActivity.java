package com.example.parttimecalander.home.resume;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.PartTimeDatabase;
import com.example.parttimecalander.Database.data.User;
import com.example.parttimecalander.Database.data.WorkPlace;
import com.example.parttimecalander.R;
import com.example.parttimecalander.databinding.ActivityResumeBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;


public class ResumeActivity extends AppCompatActivity {
    private ActivityResumeBinding binding;
    PartTimeDatabase partTimeDatabase;
    User user;
    List<WorkPlace> workPlaces;
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
        partTimeDatabase = PartTimeDatabase.getDatabase(this);
        UserDao userDao = partTimeDatabase.userDao();
        WorkPlaceDao workPlaceDao = partTimeDatabase.workPlaceDao();
        // LiveData 관찰
        userDao.getDataChange().observe(this, users -> {
            if (users != null && !users.isEmpty()) {
                user = users.get(0);
                updateUI();
            }
        });
        //근무지 읽어와서 자동으로 저장
        Executors.newSingleThreadExecutor().execute(() -> {
            workPlaces = workPlaceDao.getDataAll();
        });
    }
    private void updateUI(){
        binding.back.setOnClickListener(v->onBackPressed());
        if(user != null){
            binding.name.setText(user.name);
            binding.birth.setText(user.birthYear+"."+user.birthMonth+"."+user.birthDay);
            binding.phone.setText(user.phone);
            binding.email.setText(user.email);
            binding.address.setText(user.address);
            if(user.image!=null){
                binding.profileImg.setBackground(byteArrayToDrawable(this,user.image));
            }
            //학력 리사이클러뷰
            if(user.schoolList!=null){
                Log.d("qqqq",user.schoolList);
                List<String> itemlist;
                if(user.schoolList.contains("\n")){
                    itemlist= Arrays.asList(user.schoolList.split("\n"));

                }else{
                    itemlist=new ArrayList<>();
                }
                SchoolEduAdapter adapter=new SchoolEduAdapter(itemlist);
                binding.schoolEduRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                binding.schoolEduRecyclerView.setAdapter(adapter);
            }
            //자격증 리사이클러뷰
            if(user.certList!=null){
                List<String> itemlist;
                if(user.certList.contains("\n")){
                    itemlist= Arrays.asList(user.certList.split("\n"));

                }else{
                    itemlist=new ArrayList<>();
                }
                CertAdapter adapter=new CertAdapter(itemlist);
                binding.certRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                binding.certRecyclerView.setAdapter(adapter);
            }
            //근무지 리사이클러뷰
            if(workPlaces != null){
                HistoryAdapter adapter = new HistoryAdapter(workPlaces);
                binding.workplaceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                binding.workplaceRecyclerView.setAdapter(adapter);
            }
            
            //자기소개서 에딧텍스트
            if(user.selfIntroduce!=null){
                binding.selfIntroductionEdit.setText(user.selfIntroduce);
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







        binding.registerButton.setOnClickListener(v->saveintroduce());
    }
    public void saveintroduce(){
        new Thread(() -> {
            String myintroduce=binding.selfIntroductionEdit.getText().toString();
            UserDao userDao=partTimeDatabase.userDao();
            User user1;
            if(userDao.getDataAll().size()==0){
                user1=new User();
                user1.selfIntroduce=myintroduce;
                userDao.setInsertData(user1);
            }else{
                user1=userDao.getDataAll().get(0);
                user1.selfIntroduce=myintroduce;
                userDao.setUpdateData(user1);
            }
            runOnUiThread(() ->
                    Toast.makeText(ResumeActivity.this, "자소서가 저장되었습니다", Toast.LENGTH_SHORT).show()
            );
        }).start();


    }
    private void convertToPdf() {
        // 버튼을 invisible로 만들어 숨기기
        binding.pdfConvert.setVisibility(View.INVISIBLE);
        binding.registerCert.setVisibility(View.INVISIBLE);
        binding.registerEdu.setVisibility(View.INVISIBLE);
        binding.registerButton.setVisibility(View.INVISIBLE);
        binding.editPerson.setVisibility(View.INVISIBLE);
        ScrollView scrollView=(ScrollView)findViewById(R.id.scrollviewpdf);
        saveScrollViewAsPdf(scrollView,this,"자소서");
    }


    public void saveScrollViewAsPdf(ScrollView scrollView, Context context, String fileName) {
        // Step 1: ScrollView를 비트맵으로 변환
        Bitmap bitmap = getBitmapFromScrollView(scrollView);

        if (bitmap != null) {
            // Step 2: 비트맵을 PDF로 변환

            File pdfFile = getUniqueFile(fileName);

            PdfDocument pdfDocument = new PdfDocument();
            try {
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                Canvas canvas = page.getCanvas();
                canvas.drawBitmap(bitmap, 0, 0, null);

                pdfDocument.finishPage(page);

                // Step 3: PDF를 파일로 저장
                try (FileOutputStream out = new FileOutputStream(pdfFile)) {
                    pdfDocument.writeTo(out);
                }

                Toast.makeText(context,"PDF가 저장되었습니다.",Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // 명시적으로 PdfDocument 닫기
                pdfDocument.close();
            }
        }
        binding.pdfConvert.setVisibility(View.VISIBLE);
        binding.registerCert.setVisibility(View.VISIBLE);
        binding.registerEdu.setVisibility(View.VISIBLE);
        binding.registerButton.setVisibility(View.VISIBLE);
        binding.editPerson.setVisibility(View.VISIBLE);
    }
    private static File getUniqueFile(String baseFileName) {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(downloadsDir, baseFileName + ".pdf");
        int counter = 1;

        // 중복된 이름이 존재할 경우 이름 변경
        while (pdfFile.exists()) {
            pdfFile = new File(downloadsDir, baseFileName + "(" + counter + ").pdf");
            counter++;
        }
        return pdfFile;
    }
    private static Bitmap getBitmapFromScrollView(ScrollView scrollView) {
        int width = scrollView.getChildAt(0).getWidth();
        int height = scrollView.getChildAt(0).getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        scrollView.getChildAt(0).draw(canvas);

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
                // EXIF 정보를 사용하여 회전된 이미지를 처리
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                bitmap = rotateImageIfRequired(bitmap, imageUri);

                // 이미지 크기 조정 (최대 크기 제한을 예시로 1024px로 설정)
                Bitmap resizedBitmap = resizeImage(bitmap, 1024);

                // 이미지 저장
                saveImageToDatabase(resizedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // EXIF 정보를 사용하여 회전된 이미지를 올바르게 회전시키는 메소드
    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = getContentResolver().openInputStream(selectedImage);
        ExifInterface ei = new ExifInterface(input);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        Matrix matrix = new Matrix();
        // 회전 처리
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            matrix.postRotate(90);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            matrix.postRotate(180);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            matrix.postRotate(270);
        }

        // 회전된 이미지를 반환
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }
    // 이미지 크기를 조정하는 메소드
    private Bitmap resizeImage(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 크기가 maxSize보다 클 경우, 비율에 맞게 크기를 줄임
        float aspectRatio = (float) width / height;
        if (width > height) {
            width = maxSize;
            height = (int) (width / aspectRatio);
        } else {
            height = maxSize;
            width = (int) (height * aspectRatio);
        }

        // 크기 조정된 Bitmap 반환
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }
    private void saveImageToDatabase(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageData = outputStream.toByteArray();

        new Thread(() -> {
            if(PartTimeDatabase.getDatabase(this).userDao().getDataAll().isEmpty()){
                User user=new User();
                user.image=imageData;
                PartTimeDatabase.getDatabase(this).userDao().setInsertData(user);
            }else{
                User user=PartTimeDatabase.getDatabase(this).userDao().getDataAll().get(0);
                user.image=imageData;
                PartTimeDatabase.getDatabase(this).userDao().setUpdateData(user);
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