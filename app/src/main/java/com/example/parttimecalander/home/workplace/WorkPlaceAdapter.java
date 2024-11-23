package com.example.parttimecalander.home.workplace;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class WorkPlaceAdapter extends RecyclerView.Adapter<WorkPlaceAdapter.ViewHolder> {
    private final List<WorkPlace> workPlaces;
    private Context mContext;

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
            holder.startDateTextView.setText(workPlaces.get(position).startDate.replace("00:00:00",""));
        }
        if(workPlaces.get(position).endDate==null || workPlaces.get(position).endDate.trim().isEmpty()){
            holder.endDateTextView.setText("0000-00-00");
        }else{
            holder.endDateTextView.setText(workPlaces.get(position).endDate.replace("00:00:00",""));
        }

        holder.industryTextView.setText(workPlaces.get(position).type);
        holder.moneyTextView.setText(decimalFormatter.format(usualPay));
        holder.juhyuTextView.setText(workPlaces.get(position).isJuhyu ? "주휴수당 있음" : "주휴수당 없음");
        holder.detailsLayout.setVisibility(workPlaces.get(position).isExpanded ? View.VISIBLE : View.GONE);

        Log.d("qqq", String.valueOf(workPlaces.get(position).day.charAt(1) == '1'));
        String[] array={"일","월","화","수","목","금","토"};
        String answer="";
        String[] start_times=workPlaces.get(position).startTime.split(",");
        String[] end_times=workPlaces.get(position).endTime.split(",");
        int sssss=0;
        int eeeee=0;
        for(int i=0;i<7;i++){
            if(workPlaces.get(position).day.charAt(i) == '1'){
                answer=answer.concat(array[i]+" ");
                answer=answer.concat(start_times[sssss++]);
                answer=answer.concat("~");
                answer=answer.concat(end_times[eeeee++]);
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
