package com.cmc.android.context

import android.os.Messenger

data class CalcContext(
    val firstNumber: String,
    val secondNumber: String,
    val operator: Int,
    val taskId: Int,
    val responseMessenger : Messenger
)