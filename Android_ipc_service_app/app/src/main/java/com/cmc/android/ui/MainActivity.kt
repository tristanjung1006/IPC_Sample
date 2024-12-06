package com.cmc.android.ui

import android.os.Bundle
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.cmc.android.CmcBundleKey
import com.cmc.android.CmcMessageType
import com.cmc.android.CmcResultCode
import com.cmc.android.CmcResultMsg
import com.cmc.android.databinding.ActivityMainBinding
import com.cmc.android.service.CmcCalcServiceManager
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding by lazy { requireNotNull(_binding) }
    private var serviceManager: CmcCalcServiceManager? = null
    private var jobId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        doAppToApp()
    }

    private fun doAppToApp() {
        val bundle = intent.extras ?: return
        bindService()
        jobId = bundle.getString(CmcBundleKey.KEY_JOB_ID)
        val firstNumber = bundle.getString(CmcBundleKey.KEY_FIRST_NUMBER)?.toIntOrNull()!!
        val secondNumber = bundle.getString(CmcBundleKey.KEY_SECOND_NUMBER)?.toIntOrNull()!!
        val operator = bundle.getInt(CmcBundleKey.KEY_OPERATOR)

        val result = kotlin.runCatching {
            when (operator) {
                1 -> {
                    firstNumber + secondNumber
                }

                2 -> {
                    firstNumber - secondNumber
                }
                else -> {
                    0
                }
            }
        }.getOrNull()
        val resultData = if(result == null){
            Triple(CmcResultCode.ERROR_UNSUPPORTED_NUMBER, CmcResultMsg.ERROR_INVALID_PARAMS, null)
        } else {
            Triple(CmcResultCode.RESULT_SUCCESS, CmcResultMsg.SUCCESS, result.toString())
        }

        responseAppToApp(
            resultCode = resultData.first,
            resultMsg = resultData.second,
            resultData = resultData.third
        )
    }

    private fun responseAppToApp(
        resultCode: Int,
        resultMsg: String,
        resultData: String?
    ) {
        if (serviceManager == null) return

        val replyMsg = Message.obtain(null, CmcMessageType.RESP, 0, 0).apply {
            data = Bundle().apply {
                putInt(CmcBundleKey.KEY_RESULT_CODE, resultCode)
                putString(CmcBundleKey.KEY_RESULT_MSG, resultMsg)
                putString(CmcBundleKey.KEY_RESULT_DATA, resultData)
                putString(CmcBundleKey.KEY_JOB_ID, jobId)
            }
        }
        serviceManager?.sendMessage(replyMsg)
        finishAndRemoveTask()
    }

    private fun bindService() {
        if (serviceManager != null) return

        serviceManager = CmcCalcServiceManager(this).also {
            it.connectService()
        }
    }

    private fun unBindingService() {
        if (serviceManager == null) return

        serviceManager?.disConnect()
        serviceManager = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        unBindingService()
    }
}