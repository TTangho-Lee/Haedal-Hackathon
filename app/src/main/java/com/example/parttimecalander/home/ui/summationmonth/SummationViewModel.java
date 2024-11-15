package com.example.parttimecalander.home.ui.summationmonth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//ViewModel을 통해 두 프래그먼트에서 데이터를 공유한다.
//액티비티에서 ViewModel을 초기화하고 프래그먼트에서 ViewModel을 참조한다

//넘기고자 하는 데이터: 해당 월 및 주의 시급들
//각 프래그먼트에 맞춰서 데이터를 셀렉션해서 프래그먼트를 구성한다
public class SummationViewModel extends ViewModel {

    private final MutableLiveData<String> _data = new MutableLiveData<>();
    public LiveData<String> getData() {
        return _data;
    }

    public void updateData(String newData) {
        _data.setValue(newData);
    }
}
