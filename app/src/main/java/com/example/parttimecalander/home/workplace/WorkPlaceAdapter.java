package com.example.parttimecalander.home.workplace;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.PartTimeDatabase;
import com.example.parttimecalander.Database.data.WorkPlace;
import com.example.parttimecalander.R;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkPlaceAdapter extends RecyclerView.Adapter<WorkPlaceAdapter.ViewHolder> {
    private final List<WorkPlace> workPlaces;
    private Context mContext;
    private WorkDailyDao workDailyDao;
    private WorkPlaceDao workPlaceDao;

    public WorkPlaceAdapter(Context mContext, List<WorkPlace> workPlaces) {
        this.workPlaces = workPlaces;
        this.mContext = mContext;
    }

    // ViewHolder 정의
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LocalDate indefiniteDate = LocalDate.of(9999, 12, 31);
        private View colorView;
        private TextView titleTextView;
        private TextView startDateTextView;
        private TextView endDateTextView;
        private View detailsLayout;
        private TextView industryTextView;
        private TextView moneyTextView;
        private TextView juhyuTextView;
        private TextView workdayBox;
        private ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.workplace_color);
            titleTextView = itemView.findViewById(R.id.workplace_title);
            startDateTextView = itemView.findViewById(R.id.workplace_start);
            endDateTextView = itemView.findViewById(R.id.workplace_end);
            detailsLayout = itemView.findViewById(R.id.container_industry);
            industryTextView = itemView.findViewById(R.id.content_industry);
            moneyTextView = itemView.findViewById(R.id.content_money);
            juhyuTextView = itemView.findViewById(R.id.juhyu_money);
            workdayBox = itemView.findViewById(R.id.content_sample_money);
            constraintLayout=itemView.findViewById(R.id.container_industry);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workplace, parent, false);

        PartTimeDatabase partTimeDatabase = PartTimeDatabase.getDatabase(parent.getContext());
        workDailyDao = partTimeDatabase.workDailyDao();
        workPlaceDao = partTimeDatabase.workPlaceDao();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecimalFormat decimalFormatter = new DecimalFormat("###,###");
        int usualPay = workPlaces.get(position).usualPay;
        holder.titleTextView.setText(workPlaces.get(position).placeName);
        if(workPlaces.get(position).startDate==null || workPlaces.get(position).startDate.trim().isEmpty()){
            holder.startDateTextView.setText("0000-00-00");
        }else{
            LocalDateTime startDate = LocalDateTime.parse(workPlaces.get(position).startDate);
            LocalDate localDate = startDate.toLocalDate();
            holder.startDateTextView.setText(localDate.toString());
        }
        if(workPlaces.get(position).endDate==null || workPlaces.get(position).endDate.trim().isEmpty()){
            holder.endDateTextView.setText("0000-00-00");
        }else{
            LocalDateTime endDate = LocalDateTime.parse(workPlaces.get(position).endDate);
            LocalDate localDate = endDate.toLocalDate();
            holder.endDateTextView.setText(localDate.toString());
        }

        holder.industryTextView.setText(workPlaces.get(position).type);
        holder.moneyTextView.setText(decimalFormatter.format(usualPay));
        holder.juhyuTextView.setText(workPlaces.get(position).isJuhyu ? "주휴수당 있음" : "주휴수당 없음");
        holder.detailsLayout.setVisibility(workPlaces.get(position).isExpanded ? View.VISIBLE : View.GONE);

        Log.d("qqq", String.valueOf(workPlaces.get(position).day.charAt(1) == '1'));
        String[] array={"일","월","화","수","목","금","토"};
        String answer="";
        List<String> start_times=workPlaces.get(position).startTime;
        List<String> end_times=workPlaces.get(position).endTime;

        for(int i=0;i<7;i++){
            if(workPlaces.get(position).day.charAt(i) == '1'){
                answer=answer.concat(array[i]+" ");
                answer=answer.concat(start_times.get(i));
                answer=answer.concat("~");
                answer=answer.concat(end_times.get(i));
                answer=answer.concat("\n");
            }
        }
        if (!answer.isEmpty() && answer.endsWith("\n")) {
            answer = answer.substring(0, answer.length() - 1); // 마지막 문자(\n) 제거
        }
        holder.workdayBox.setText(answer);
        holder.itemView.setOnClickListener(v -> {
                    workPlaces.get(position).isExpanded=!workPlaces.get(position).isExpanded;
        notifyItemChanged(holder.getAdapterPosition());});
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 꾹 누르면 다이얼로그 표시
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("작업 선택");
                builder.setItems(new CharSequence[]{"수정", "삭제"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WorkPlace selectedWorkPlace = workPlaces.get(position);
                        switch (which) {
                            case 0: // 수정
                                Intent intent = new Intent(mContext, WorkPlaceRegisterActivity.class);
                                intent.putExtra("isRegister", false);
                                intent.putExtra("workPlace", selectedWorkPlace);
                                mContext.startActivity(intent);
                                break;

                            case 1: // 삭제
                                selectedWorkPlace.erase = true;
                                String today = LocalDate.now(ZoneId.of("Asia/Seoul")).toString();
                                ExecutorService executor = Executors.newSingleThreadExecutor();
                                executor.execute(() -> {
                                    // 백그라운드 작업
                                    workPlaceDao.setUpdateData(selectedWorkPlace);
                                    workDailyDao.deleteSchedulesAfterDateForPlace(today,selectedWorkPlace.ID);

                                    // UI 스레드에서 어댑터 설정
                                    ((Activity) mContext).runOnUiThread(() -> {
                                        workPlaces.remove(position);
                                        notifyItemRemoved(position);
                                        Toast.makeText(mContext, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                                    });
                                });


                                break;
                        }
                    }
                });

                builder.show();
                return true;
            }
        });
 
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.background_round); // 원래 oval 리소스
        if (drawable != null) {
            // 새로운 색상으로 변경
            drawable.setTint(Color.parseColor(workPlaces.get(position).ColorHex));
            holder.colorView.setBackground(drawable); // colorView에 새 배경 설정
        }
        drawable = ContextCompat.getDrawable(mContext, R.drawable.background_white_border_10);
        if (drawable != null) {
            // 새로운 색상으로 변경
            drawable.setTint(Color.parseColor(workPlaces.get(position).ColorHex));
            holder.detailsLayout.setBackground(drawable); // colorView에 새 배경 설정
        }
    }

    @Override
    public int getItemCount() {
        return workPlaces.size();
    }
}
