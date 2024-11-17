package com.example.parttimecalander.calander;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {
    private final int color;
    private final HashSet<CalendarDay> dates;
    public EventDecorator(int color, Collection<CalendarDay> dates){
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day){
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view){
        view.addSpan(new DotSpan(10, Color.RED));

        //TODO: SPAN 커스텀해서 가지고 있는 일정 나타내기. 일단 일정 있으면 DOT span으로 표시하게 해둠.
        //view.addSpan(new RectangleSpan(50.0f, 50.0f, Color.RED,"TEST STRING"));
    }
}
