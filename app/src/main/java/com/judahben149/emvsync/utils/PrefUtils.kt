package com.judahben149.emvsync.utils

import android.content.SharedPreferences


fun SharedPreferences.saveString(key: String, value: String) {
    val editor = this.edit()
    editor.putString(key, value)
    editor.apply()
}

fun SharedPreferences.saveInt(key: String, value: Int) {
    val editor = this.edit()
    editor.putInt(key, value)
    editor.apply()
}

fun SharedPreferences.saveBoolean(key: String, value: Boolean) {
    val editor = this.edit()
    editor.putBoolean(key, value)
    editor.apply()
}

fun SharedPreferences.fetchString(key: String): String? {
    return this.getString(key, "")
}

fun SharedPreferences.fetchInt(key: String): Int {
    return this.getInt(key, 0)
}

fun SharedPreferences.fetchBoolean(key: String, defaultValue: Boolean): Boolean {
    return this.getBoolean(key, defaultValue)
}

