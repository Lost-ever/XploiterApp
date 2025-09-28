package com.sploiter.xploiter.utility

import android.content.Context
import com.google.gson.Gson
import androidx.core.content.edit

fun saveUser(context: Context, user: User){
    val sharedPreferences = context.getSharedPreferences("Users", Context.MODE_PRIVATE)
    sharedPreferences.edit {
        val gson = Gson()

        val json = gson.toJson(user)
        putString("user_data", json)
    }
}

fun readUser(context: Context):User?{
    val sharedPreferences = context.getSharedPreferences("Users", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("user_data", null)

    return if (json != null){
        gson.fromJson(json, User::class.java)
    } else {
        null
    }
}

fun saveData(context: Context, data: Data){
    val sharedPreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE)
    sharedPreferences.edit {
        val json = Gson().toJson(data)
        putString(data.dataName, json)
    }
}

fun readData(context: Context, dataName: String): Data? {
    val sharedPreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString(dataName, null)

    return if (json != null){
        gson.fromJson(json, Data::class.java)
    } else {
        null
    }
}
data class User(val username: String, val password: String? = null)
data class Data(val dataName: String, val gson: Gson)