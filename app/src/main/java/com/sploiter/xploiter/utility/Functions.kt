package com.sploiter.xploiter.utility

import android.content.Context
import android.content.Intent

fun startactivity(context: Context, ktclass: Class<*>, username: String? = null){
    val intent = Intent(context, ktclass)
    username?.let{ intent.putExtra("username", it)}
    context.startActivity(intent)
}