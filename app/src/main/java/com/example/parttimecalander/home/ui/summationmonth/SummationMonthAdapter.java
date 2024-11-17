package com.example.parttimecalander.home.ui.summationmonth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.R;

import java.io.Serializable;
import java.util.List;

public class SummationMonthAdapter extends RecyclerView.Adapter<SummationMonthAdapter.ViewHolder>{


    private List<RecyclerItem> items;
    private AppCompatActivity activity;

    // 생성자
    public SummationMonthAdapter(List<RecyclerItem> items, AppCompatActivity activity) {
        this.activity = activity;
        this.items = items;
    }

    // ViewHolder 정의
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView[] textviews=new TextView[6];

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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
        RecyclerItem item = items.get(position);
        holder.textviews[0].setText(item.name);
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
        holder.textviews[1].setText(String.format("%.1f원", normal_hour*item.pay));
        holder.textviews[2].setText(String.format("(%.1f시간)", normal_hour));
        holder.textviews[3].setText(String.format("%.1f원", over_hour*item.pay*1.5));
        holder.textviews[4].setText(String.format("%.1f원", normal_hour*item.pay+over_hour*item.pay*1.5));
        holder.textviews[5].setText(String.format("(%.1f시간)", normal_hour+over_hour));
        holder.itemView.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", item);

            // 새 Fragment로 이동
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            SummationWeekFragment detailFragment = new SummationWeekFragment();
            detailFragment.setArguments(bundle);  // Bundle을 Fragment에 전달
            transaction.replace(R.id.nav_host_fragment_content_main, detailFragment);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
