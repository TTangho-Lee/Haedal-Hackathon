package com.example.parttimecalander.Database;

import androidx.room.TypeConverter;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converters {

    @TypeConverter
    public String fromStringList(List<String> times) {
        if (times == null) return null;
        return times.stream()
                .map(time -> time == null ? "NULL" : time)
                .collect(Collectors.joining(","));
    }

    @TypeConverter
    public List<String> toStringList(String data) {
        if (data == null || data.isEmpty()) return null;
        return Arrays.stream(data.split(","))
                .map(time -> time.equals("NULL") ? null : time)
                .collect(Collectors.toList());
    }
}