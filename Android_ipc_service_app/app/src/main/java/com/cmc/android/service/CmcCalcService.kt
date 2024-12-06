package com.cmc.android.service

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import com.cmc.android.context.CalcContext
import com.cmc.android.CmcBundleKey
import com.cmc.android.CmcMessageType
import com.cmc.android.ui.MainActivity

class CmcCalcService : Service() {

    private val calcMap = mutableMapOf<String, CalcContext>()
    private val requestHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                CmcMessageType.REQ -> {
                    doAppToApp(msg)
                }
                CmcMessageType.RESP -> {
                    responseApp(msg)
                }
            }
        }
    }
    private val messenger = Messenger(requestHandler)

    private fun doAppToApp(message : Message) {
        val bundle = message.data

        val jobId = bundle.getString(CmcBundleKey.KEY_JOB_ID)!!
        val taskId = bundle.getInt(CmcBundleKey.KEY_TASK_ID)
        val firstNumber = bundle.getString(CmcBundleKey.KEY_FIRST_NUMBER)!!
        val secondNumber = bundle.getString(CmcBundleKey.KEY_SECOND_NUMBER)!!
        val operator = bundle.getInt(CmcBundleKey.KEY_OPERATOR)

        calcMap[jobId] = CalcContext(firstNumber, secondNumber, operator, taskId, message.replyTo)

        startActivity(
            firstNumber = firstNumber,
            secondNumber = secondNumber,
            operator = operator
        )
    }

    private fun responseApp(message : Message) {
        val bundle = message.data
        val jobId = bundle.getString(CmcBundleKey.KEY_JOB_ID) ?: return
        val jobInfo = calcMap[jobId] ?: return
        kotlin.runCatching {
            val responseMessage = Message.obtain(null, CmcMessageType.RESP).apply {
                data = message.data
            }
            jobInfo.responseMessenger.send(responseMessage)
        }.onSuccess {
            calcMap.remove(jobId)
            moveTaskFront(jobInfo.taskId)
        }.onFailure {
            return
        }
    }

    private fun startActivity(
        firstNumber: String,
        secondNumber: String,
        operator: Int,
    ) {
        Intent(this, MainActivity::class.java).apply {
            putExtra(CmcBundleKey.KEY_FIRST_NUMBER, firstNumber)
            putExtra(CmcBundleKey.KEY_SECOND_NUMBER, secondNumber)
            putExtra(CmcBundleKey.KEY_OPERATOR, operator)

            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }.run { startActivity(this) }
    }

    private fun moveTaskFront(taskId : Int){
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        am.moveTaskToFront(taskId, 0)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return messenger.binder
    }

    companion object {
        const val SERVICE_FULL_NAME = "com.cmc.android.service.CmcCalcService"
    }
}