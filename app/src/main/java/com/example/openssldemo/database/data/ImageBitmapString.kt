package com.example.openssldemo.database.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class ImageBitmapString {


    companion object {
        fun bitmapToString(bitmap: Bitmap) : String {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val b = stream.toByteArray()
            val temp = Base64.encodeToString(b, Base64.DEFAULT)
            return temp
        }


        fun stringToBitmap(string: String) : Bitmap? {
            val encodeByte = Base64.decode(string, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            return bitmap
        }
    }

}