package com.example.parttimecalander.home.workplace;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

    public WorkPlaceAdapter(List<WorkPlace> workPlaces) {
        this.workPlaces = workPlaces;
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
        private LinearLayout workdayBox;
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
            workdayBox = itemView.findViewById(R.id.box_workday);
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
        // TODO: 리니어레이아웃에"출근요일: 출근시간- 퇴근시간"인 텍스트 뷰 동적으로 만들어서 표시하기
        holder.detailsLayout.setVisibility(workPlaces.get(position).isExpanded ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
                    workPlaces.get(position).isExpanded=!workPlaces.get(position).isExpanded;
        notifyItemChanged(holder.getAdapterPosition());});
        holder.colorView.setBackgroundColor(Color.parseColor(workPlaces.get(position).ColorHex));
        holder.constraintLayout.setBackgroundColor(Color.parseColor(workPlaces.get(position).ColorHex));
    }

    @Override
    public int getItemCount() {
        return workPlaces.size();
    }
}
