package com.example.openssldemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.openssldemo.R
import com.example.openssldemo.database.data.App
import com.example.openssldemo.database.data.ImageBitmapString
import java.text.SimpleDateFormat
import java.util.Locale

class AppAdapter(private val appList: List<App>) : RecyclerView.Adapter<AppAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appLogo : ImageView = view.findViewById(R.id.app_logo)
        val appName : TextView = view.findViewById(R.id.app_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        return ItemViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = appList[position]
        holder.apply {
            appLogo.setImageBitmap(ImageBitmapString.stringToBitmap(item.logo))
            appName.text = item.name

        }
    }


}