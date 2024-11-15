package com.example.parttimecalander.home.workplace;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class WorkPlaceAdapter extends RecyclerView.Adapter<WorkPlaceAdapter.WorkPlaceViewHolder> {
    private final List<WorkPlace> workPlaces;

    public WorkPlaceAdapter(List<WorkPlace> workPlaces) {
        this.workPlaces = workPlaces;
    }

    // ViewHolder 정의
    public class WorkPlaceViewHolder extends RecyclerView.ViewHolder {
        private final LocalDate indefiniteDate = LocalDate.of(9999, 12, 31);

        // title 구성
        private final View colorView;
        private final TextView titleTextView;
        private final TextView startDateTextView;
        private final TextView endDateTextView;

        private final View detailsLayout;
        private final TextView industryTextView;
        private final TextView moneyTextView;
        private final TextView juhyuTextView;
        private final LinearLayout workdayBox;

        public WorkPlaceViewHolder(View itemView) {
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
        }

        public void bind(WorkPlace workPlace) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            DecimalFormat decimalFormatter = new DecimalFormat("###,###");
            int usualPay = workPlace.getUsualPay();

            // titleLayout setting
            colorView.setBackgroundColor(Color.parseColor(workPlace.getColorHex()));
            titleTextView.setText(workPlace.getPlaceName());
            startDateTextView.setText(workPlace.getStartDate());
            endDateTextView.setText(Objects.equals(workPlace.getEndDate(), indefiniteDate.toString()) ? "미정" : formatter.toString());

            // detailLayout setting
            detailsLayout.setBackgroundColor(Color.parseColor(workPlace.getColorHex()));
            industryTextView.setText(workPlace.getType());
            moneyTextView.setText(decimalFormatter.format(usualPay));
            juhyuTextView.setText(workPlace.isJuhyu() ? "주휴수당 있음" : "주휴수당 없음");

            // TODO: 리니어레이아웃에 "출근요일: 출근시간 - 퇴근시간"인 텍스트 뷰 동적으로 만들어서 표시하기

            detailsLayout.setVisibility(workPlace.isExpanded() ? View.VISIBLE : View.GONE);

            // 클릭 시 아이템 확장 및 축소
            itemView.setOnClickListener(v -> {
                workPlace.setExpanded(!workPlace.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }

    @NonNull
    @Override
    public WorkPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workplace, parent, false);
        return new WorkPlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkPlaceViewHolder holder, int position) {
        holder.bind(workPlaces.get(position));
    }

    @Override
    public int getItemCount() {
        return workPlaces.size();
    }
}
