package com.example.parttimecalander.home.resume;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.data.WorkPlace;
import com.example.parttimecalander.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryAdapter  extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{
    private List<WorkPlace> itemList;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    // 생성자
    public HistoryAdapter(List<WorkPlace> itemList) {
        this.itemList = itemList;
    }

    // ViewHolder 클래스
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView2;
        public ImageView imageButton;
        public ConstraintLayout background;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.certName); //근무지명
            textView2=itemView.findViewById(R.id.certDuration); //근무경력
            imageButton=itemView.findViewById(R.id.delete_cert_button); //삭제버튼
            background = itemView.findViewById(R.id.background);
        }
    }

    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cert, parent, false);
        return new HistoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.MyViewHolder holder, int position) {
        String placename = itemList.get(position).placeName;
        String start = itemList.get(position).startDate;
        String end = itemList.get(position).endDate;
        String duration  = start.split(" ")[0] + " ~ " + end.split(" ")[0];
        holder.textView.setText(placename);
        holder.textView2.setText(duration);

        holder.imageButton.setVisibility(View.GONE);
        holder.background.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
