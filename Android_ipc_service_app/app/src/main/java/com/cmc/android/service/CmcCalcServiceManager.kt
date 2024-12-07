package com.cmc.android.service

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CmcCalcServiceManager(context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: CmcCalcServiceManager? = null
        fun getInstance(context: Context): CmcCalcServiceManager {
            if (INSTANCE == null) {
                INSTANCE = CmcCalcServiceManager(context)
            }
            return INSTANCE!!
        }

        fun release() {
            INSTANCE = null
        }
    }

    private var mContext: Context? = null
    private var mConnection: ServiceConnection? = null
    private var mService: Messenger? = null
    private var mIsBound = false

    init {
        this.mContext = context
    }

    fun connectService() {
        if (mIsBound) {
            return
        }
        mConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mService = Messenger(service)
                mIsBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mService = null
                mIsBound = false
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            val bindState = async {
                Intent().apply {
                    component =
                        ComponentName(mContext!!.packageName, CmcCalcService.SERVICE_FULL_NAME)
                }.let { mContext?.bindService(it, mConnection!!, Context.BIND_AUTO_CREATE) }
            }
            bindState.await()
        }
    }

    fun disConnect() {
        if (mIsBound) {
            mContext?.unbindService(mConnection!!)
            mIsBound = false
        }
        release()
        mConnection = null
    }

    fun sendMessage(msg: Message) {
        if (!mIsBound) return
        mService?.send(msg)
    }
}