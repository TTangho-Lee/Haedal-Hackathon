package com.example.parttimecalander.home.workplace

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parttimecalander.Database.WorkPlace

class WorkPlaceAdapter(private val workPlaces: MutableList<WorkPlace>): RecyclerView.Adapter<WorkPlaceAdapter.WorkPlaceViewHolder>() {
    // ViewHolder 정의
    inner class WorkPlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val detailsLayout: View = itemView.findViewById(R.id.detailsLayout)

        fun bind(workPlace: WorkPlace) {
            titleTextView.text = workPlace.placeName
            detailsLayout.visibility = if (workPlace.isExpanded) View.VISIBLE else View.GONE

            // 클릭 시 아이템 확장 및 축소
            itemView.setOnClickListener {
                workPlace.isExpanded = !workPlace.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
}