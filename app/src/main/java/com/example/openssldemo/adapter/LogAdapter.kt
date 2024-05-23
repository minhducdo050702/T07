package com.example.openssldemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.openssldemo.R
import com.example.openssldemo.database.data.Log
import com.example.openssldemo.database.data.LogWithAppAndData
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.SimpleFormatter
import com.example.openssldemo.database.data.ImageBitmapString


class LogAdapter(private val logsList: List<LogWithAppAndData>,
    private val context : Context) : RecyclerView.Adapter< LogAdapter.ItemViewHolder>() {


    class ItemViewHolder(private val view: View) : ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val itemName : TextView = view.findViewById(R.id.item_name)
        val itemDate : TextView = view.findViewById(R.id.item_date)
        val itemAction : TextView = view.findViewById(R.id.item_action)
        val itemStatus :TextView = view.findViewById(R.id.item_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layer = LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent, false)
        return ItemViewHolder(layer)
    }

    override fun getItemCount(): Int {
        return logsList.size
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = logsList[position]
        val formatter = SimpleDateFormat("E MMM dd HH:mm:ss", Locale.getDefault())
        holder.apply {

            imageView.setImageBitmap(ImageBitmapString.stringToBitmap(item.app.logo))
            itemName.text = item.app.name
            itemDate.text = formatter.format(item.log.logDate)
            itemAction.text = context.resources.getString(R.string.action, item.log.action)
            itemStatus.text = context.resources.getString(R.string.status, item.log.status)
            if(item.log.status == "Successful") {
                itemStatus.setTextColor(context.getColor(R.color.success))
            }else {
                itemStatus.setTextColor(context.getColor(R.color.fail))
            }
        }
    }



}