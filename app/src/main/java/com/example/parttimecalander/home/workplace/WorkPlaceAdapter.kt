package com.example.parttimecalander.home.workplace

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parttimecalander.Database.WorkPlace
import com.example.parttimecalander.R
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkPlaceAdapter(private val workPlaces: MutableList<WorkPlace>): RecyclerView.Adapter<WorkPlaceAdapter.WorkPlaceViewHolder>() {
    // ViewHolder 정의
    inner class WorkPlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val indefiniteDate = LocalDate.of(9999, 12, 31)
        //title 구성
        val colorView: View = itemView.findViewById(R.id.workplace_color)
        val titleTextView: TextView = itemView.findViewById(R.id.workplace_title)
        val startDateTextView: TextView = itemView.findViewById(R.id.workplace_start)
        val endDateTextView: TextView = itemView.findViewById(R.id.workplace_end)
        
        private val detailsLayout: View = itemView.findViewById(R.id.container_industry)
        val industryTextView: TextView = itemView.findViewById(R.id.content_industry)
        val moneyTextView: TextView = itemView.findViewById(R.id.content_money)
        val juhyuTextView: TextView = itemView.findViewById(R.id.juhyu_money)
        val workdayBox: LinearLayout = itemView.findViewById(R.id.box_workday)
        //TODO : 리니어레이아웃에 "출근요일: 출근시간 - 퇴근시간"인 텍스트 뷰 동적으로 만들어서 표시하기

        fun bind(workPlace: WorkPlace) {
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val decimalFormatter = DecimalFormat("###,###")
            val usualPay = workPlace.usualPay

            //titleLayout setting
            colorView.setBackgroundColor(Color.parseColor(workPlace.ColorHex))
            titleTextView.text = workPlace.placeName
            startDateTextView.text = workPlace.startDate.format(formatter)
            endDateTextView.text = if (workPlace.endDate.isEqual(indefiniteDate)) {
                "미정"
            } else {
                workPlace.endDate.format(formatter) // 일반 날짜 포맷
            }

            //detailLayout setting
            detailsLayout.setBackgroundColor(Color.parseColor(workPlace.ColorHex))
            industryTextView.text = workPlace.type
            moneyTextView.text = decimalFormatter.format(usualPay)
            juhyuTextView.text = if(workPlace.isJuhyu) "주휴수당 있음" else "주휴수당 없음"
            //TODO : 리니어레이아웃에 "출근요일: 출근시간 - 퇴근시간"인 텍스트 뷰 동적으로 만들어서 표시하기


            detailsLayout.visibility = if (workPlace.isExpanded) View.VISIBLE else View.GONE
            // 클릭 시 아이템 확장 및 축소
            itemView.setOnClickListener {
                workPlace.isExpanded = !workPlace.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkPlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workplace, parent, false)
        return WorkPlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkPlaceViewHolder, position: Int) {
        holder.bind(workPlaces[position])
    }

    override fun getItemCount(): Int = workPlaces.size

}