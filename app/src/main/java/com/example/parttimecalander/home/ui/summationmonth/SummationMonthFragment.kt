package com.example.parttimecalander.home.ui.summationmonth

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.parttimecalander.R

class SummationMonthFragment : Fragment() {

    companion object {
        fun newInstance() = SummationMonthFragment()
    }

    private val viewModel: SummationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_summation_month, container, false)
    }

}