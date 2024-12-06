package com.cmc.android.service

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
    private var mContext: Context? = null
    private var mConnection: ServiceConnection? = null
    private var mService: Messenger? = null
    private var mBind = false

    init {
        this.mContext = context
    }

    fun connectService() {
        if (mBind) {
            return
        }
        mConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mService = Messenger(service)
                mBind = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mService = null
                mBind = false
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
        if (mBind) {
            mContext?.unbindService(mConnection!!)
            mBind = false
        }
        mConnection = null
    }

    fun sendMessage(msg: Message) {
        if (!mBind) return
        mService?.send(msg)
    }
}