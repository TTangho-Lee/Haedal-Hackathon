package com.example.parttimecalander.home.ui.summationmonth;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

public class SummationMonthAdapter extends RecyclerView.Adapter<SummationMonthAdapter.ViewHolder>{


    private List<RecyclerItem> items;
    private AppCompatActivity activity;

    public interface onItemClickListner{
        void onItemClick(RecyclerItem item);
    }
    // 생성자
    public SummationMonthAdapter(List<RecyclerItem> items, AppCompatActivity activity) {
        this.activity = activity;
        this.items = items;
    }

    // ViewHolder 정의
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView[] textviews=new TextView[6];
        View view;
        ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView.findViewById(R.id.workplace_color);
            constraintLayout=itemView.findViewById(R.id.container_industry);
            textviews[0]=itemView.findViewById(R.id.workplace_title);
            textviews[1]=itemView.findViewById(R.id.content_money);
            textviews[2]=itemView.findViewById(R.id.content_hour);
            textviews[3]=itemView.findViewById(R.id.juhyu_money);
            textviews[4]=itemView.findViewById(R.id.content_total_money);
            textviews[5]=itemView.findViewById(R.id.content_total_hour);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workplace_summation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("###,###");
        RecyclerItem item = items.get(position);
        holder.textviews[0].setText(item.name);
        holder.view.setBackgroundColor(Color.parseColor(item.ColorHex));
        holder.constraintLayout.setBackgroundColor(Color.parseColor(item.ColorHex));
        double normal_hour=0;
        double over_hour=0;
        for(int i=0;i<6;i++){
            double second=0;
            for(int j=0;j<7;j++){
                second+=item.worked_time[i][j];
            }
            second/=3600;
            if(second>=15&&item.juhyu){
                normal_hour+=15;
                over_hour+=second-15;
            }
            else{
                normal_hour+=second;
            }
        }

        holder.textviews[1].setText(String.format(df.format((int)normal_hour*item.pay+over_hour*item.pay)) + "원");
        holder.textviews[2].setText(String.format("(%.1f시간)", normal_hour+over_hour));
        holder.textviews[3].setText(String.format(df.format((int)over_hour*item.pay*0.5)) + "원");
        holder.textviews[4].setText(String.format(df.format((int)normal_hour*item.pay+over_hour*item.pay*1.5)) + "원");
        holder.textviews[5].setText(String.format("(%.1f시간)", normal_hour+over_hour));
        holder.itemView.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", item);

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            SummationWeekFragment fragment=new SummationWeekFragment();
            fragment.setArguments(bundle);
            // 프래그먼트를 컨테이너에 교체
            fragmentTransaction.replace(R.id.summation_month, fragment);
            fragmentTransaction.addToBackStack(null); // 뒤로가기 버튼으로 이전 상태로 돌아가기
            fragmentTransaction.commit(); // 변경사항 적용
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
