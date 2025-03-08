package com.example.parttimecalander.home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.R;

import java.util.List;

public class homeRecyclerviewAdapter extends RecyclerView.Adapter<homeRecyclerviewAdapter.MyViewHolder> {
    private List<String> dataList;

    public homeRecyclerviewAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_layout.xml 파일을 inflate
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_parttime, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // 데이터 바인딩
        String data = dataList.get(position);
        String[] parts = data.split("///");
        holder.wp_title.setText(parts[0]);
        String k = parts[1].substring(0, 10) + " ~ " + parts[1].substring(17, 27);
        holder.wp_start_date.setText(k);
        holder.colorIndicator.setBackgroundColor(Color.parseColor(parts[2]));

        // 애니메이션 실행
        holder.startMarqueeAnimation();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder 클래스 정의
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView wp_title;
        HorizontalScrollView wp_start_scrollview;
        TextView wp_start_date;
        View colorIndicator;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            wp_title = itemView.findViewById(R.id.workplace_title);
            wp_start_scrollview = itemView.findViewById(R.id.workplace_start_scrollview); // HorizontalScrollView 참조
            wp_start_date = wp_start_scrollview.findViewById(R.id.workplace_start); // HorizontalScrollView 내의 TextView 참조
            colorIndicator = itemView.findViewById(R.id.workplace_color);

            // 터치 이벤트 비활성화
            wp_start_scrollview.setOnTouchListener((v, event) -> true); // 터치 이벤트를 막음
        }

        public void startMarqueeAnimation() {
            wp_start_date.post(() -> {
                float textWidth = wp_start_date.getPaint().measureText(wp_start_date.getText().toString());
                float parentWidth = wp_start_scrollview.getWidth();

                // 텍스트가 부모보다 클 때만 애니메이션 실행
                if (textWidth > parentWidth) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(
                            wp_start_date, "translationX",
                            parentWidth, -textWidth // 오른쪽 → 왼쪽
                    );

                    animator.setDuration(5000); // 5초 동안 이동
                    animator.setRepeatCount(ValueAnimator.INFINITE); // 무한 반복
                    animator.setInterpolator(new LinearInterpolator()); // 일정한 속도로 이동
                    animator.start();
                }
            });
        }
    }
}
