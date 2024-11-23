package com.example.parttimecalander.home.resume;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Database.UserDatabase;
import com.example.parttimecalander.Database.User;
import com.example.parttimecalander.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SchoolEduAdapter extends RecyclerView.Adapter<SchoolEduAdapter.MyViewHolder>{
    private List<String> itemList;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    // 생성자
    public SchoolEduAdapter(List<String> itemList) {
        this.itemList = itemList;
    }

    // ViewHolder 클래스
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView2;
        public ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.schoolName);
            textView2=itemView.findViewById(R.id.schoolDuration);
            imageView=itemView.findViewById(R.id.delete_button);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_school_edu, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String[] items = itemList.get(position).split("///");
        if(items.length==2){
            holder.textView.setText(items[0]);
            holder.textView2.setText(items[1]);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            UserDatabase userDatabase=UserDatabase.getDatabase(v.getContext());
                            UserDao userDao=userDatabase.userDao();
                            String current=userDao.getDataAll().get(0).schoolList;
                            current=current.replace(itemList.get(position)+"\n","");
                            User user=userDao.getDataAll().get(0);
                            user.schoolList=current;
                            userDao.setUpdateData(user);
                        }
                    });
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
