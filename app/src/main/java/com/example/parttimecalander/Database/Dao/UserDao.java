package com.example.parttimecalander.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.parttimecalander.Database.User;

import java.util.List;


@Dao
public interface UserDao {
    @Insert
    void setInsertData(User data);
    @Update
    void setUpdateData(User data);
    @Delete
    void setDeleteData(User data);
    @Query("SELECT * FROM user")
    List<User> getDataAll();
}
