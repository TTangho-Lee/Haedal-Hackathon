package com.example.parttimecalander.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.parttimecalander.home.ui.summationmonth.SummationMonthFragment

import com.example.parttimecalander.R
import com.example.parttimecalander.home.ui.summationmonth.SummationViewModel
import com.example.parttimecalander.home.ui.summationmonth.SummationWeekFragment

class SummationActivity : AppCompatActivity() {

    private lateinit var summationViewModel: SummationViewModel
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summation)

        summationViewModel = ViewModelProvider(this).get(summationViewModel::class.java)

        viewPager = findViewById(R.id.viewPager)
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
    }
}

//ViewPager 사용해서 month, week fragment를 양옆으로 swipe
//클래스 이름, 생성자 매개변수(이름)
class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity){ //fragmentStateAdapter 상속
    override fun getItemCount(): Int = 2 //두 개의 프래그먼트

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> SummationMonthFragment()
            else -> SummationWeekFragment()
        }
    }
}