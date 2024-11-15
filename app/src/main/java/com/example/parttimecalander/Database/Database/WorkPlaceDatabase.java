package com.example.parttimecalander.Database.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.WorkPlace;

@Database(entities = {WorkPlace.class},version=1)
public abstract class WorkPlaceDatabase extends RoomDatabase {
    public abstract WorkPlaceDao workPlaceDao();
}
