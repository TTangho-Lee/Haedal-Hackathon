package com.example.parttimecalander.home.resume;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Database.PartTimeDatabase;
import com.example.parttimecalander.Database.data.User;
import com.example.parttimecalander.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CertAdapter extends RecyclerView.Adapter<CertAdapter.MyViewHolder>{
    private List<String> itemList;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    // 생성자
    public CertAdapter(List<String> itemList) {
        this.itemList = itemList;
    }

    // ViewHolder 클래스
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView2;
        public ImageView imageButton;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.certName);
            textView2=itemView.findViewById(R.id.certDuration);
            imageButton=itemView.findViewById(R.id.delete_cert_button);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cert, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String[] items = itemList.get(position).split("///");
        if(items.length==2){

            holder.textView.setText(items[0]);
            holder.textView2.setText(items[1]);

            holder.imageButton.setOnClickListener(v -> executorService.execute(() -> {
                PartTimeDatabase userDatabase=PartTimeDatabase.getDatabase(v.getContext());
                UserDao userDao=userDatabase.userDao();
                String current=userDao.getDataAll().get(0).certList;
                current=current.replace(itemList.get(position)+"\n","");
                User user=userDao.getDataAll().get(0);
                user.certList=current;
                userDao.setUpdateData(user);
            }));
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}