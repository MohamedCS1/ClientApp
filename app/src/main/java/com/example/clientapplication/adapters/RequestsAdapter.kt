package com.example.clientapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapplication.R
import com.example.clientapplication.pojo.RequestMessage

class RequestsAdapter: RecyclerView.Adapter<RequestsAdapter.RequestViewHolder>() {

    var arrayRequests = arrayListOf<RequestMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        return RequestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.request_card ,parent ,false))
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.name.text = arrayRequests[position].eventName
        holder.date.text = arrayRequests[position].date
        holder.numberOfDays.text = arrayRequests[position].numberDays
        holder.brief.text = arrayRequests[position].brief
        holder.dwgLink.text = arrayRequests[position].dwgLink
        holder.threeDDesignLink.text = arrayRequests[position].threeDDesignLink
        holder.uid.text = arrayRequests[position].uid
    }

    override fun getItemCount(): Int {
        return arrayRequests.size
    }

    inner class RequestViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.textViewName)
        val date = itemView.findViewById<TextView>(R.id.textViewDate)
        val numberOfDays = itemView.findViewById<TextView>(R.id.textViewNumberOfDays)
        val brief = itemView.findViewById<TextView>(R.id.textViewBrief)
        val dwgLink = itemView.findViewById<TextView>(R.id.textViewDWGLinks)
        val threeDDesignLink = itemView.findViewById<TextView>(R.id.textViewThreeDDesignLink)
        val uid = itemView.findViewById<TextView>(R.id.textViewUID)
    }

    fun setList(arrayOfRequests:ArrayList<RequestMessage>)
    {
        this.arrayRequests = arrayOfRequests
        notifyDataSetChanged()
    }
}