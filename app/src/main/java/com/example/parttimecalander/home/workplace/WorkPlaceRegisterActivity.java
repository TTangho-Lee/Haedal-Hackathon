package com.example.parttimecalander.home.workplace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.User;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;

import java.util.List;
import java.util.concurrent.Executors;

public class WorkPlaceRegisterActivity extends AppCompatActivity {
    EditText workplace_name,workplace_pay;
    Spinner workplace_color,workplace_type,workplace_start,workplace_end;
    RadioGroup workplace_juhyu;
    RadioButton workplace_yes,workplace_no;
    Button register_button;
    private WorkPlaceDatabase placeDatabase;
    private WorkPlaceDao placeDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workplace_register);
        workplace_name=(EditText)findViewById(R.id.content_workplace_name);
        workplace_color=(Spinner)findViewById(R.id.content_color);
        workplace_type=(Spinner)findViewById(R.id.content_type);
        workplace_pay=(EditText)findViewById(R.id.content_salary);
        workplace_juhyu=(RadioGroup)findViewById(R.id.content_juhyu);
        workplace_yes=(RadioButton)findViewById(R.id.radioButton_yes);
        workplace_no=(RadioButton)findViewById(R.id.radioButton_no);
        workplace_start=(Spinner)findViewById(R.id.content_workstart);
        workplace_end=(Spinner)findViewById(R.id.content_workend);
        register_button=(Button)findViewById(R.id.register_button);
        placeDatabase=WorkPlaceDatabase.getDatabase(this);
        placeDao=placeDatabase.workPlaceDao();

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    WorkPlace new_workplace=new WorkPlace();
                    new_workplace.placeName=workplace_name.getText().toString();
                    new_workplace.ColorHex=workplace_color.getSelectedItem().toString();
                    new_workplace.type=workplace_type.getSelectedItem().toString();
                    new_workplace.usualPay=Integer.getInteger(workplace_pay.getText().toString());
                    if(workplace_yes.isSelected()){
                        new_workplace.isJuhyu=true;
                    }
                    else if(workplace_no.isSelected()){
                        new_workplace.isJuhyu=false;
                    }
                    new_workplace.startDate=workplace_start.getSelectedItem().toString();
                    new_workplace.endDate=workplace_end.getSelectedItem().toString();
                    placeDao.setInsertData(new_workplace);
                });
                Intent intent=new Intent(WorkPlaceRegisterActivity.this, WorkPlaceActivity.class);
                startActivity(intent);

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}
