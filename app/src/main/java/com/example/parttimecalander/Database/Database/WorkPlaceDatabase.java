package com.example.parttimecalander.Database.Database;

import androidx.room.Database;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.Database.WorkPlace;

@Database(entities = {WorkPlace.class},version=1)
public abstract class WorkPlaceDatabase {
    public abstract WorkPlaceDao workPlaceDao();
}
