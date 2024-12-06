package com.cmc.app

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmc.app.databinding.ActivityMainBinding
import com.ryusw.ipc.api.CmcManager
import com.ryusw.ipc.callback.CmcResponseCallback
import com.ryusw.ipc.callback.CmcServiceConnectionCallback
import com.ryusw.ipc.constant.CmcBundleKey
import com.ryusw.ipc.context.CmcCalcType
import com.ryusw.ipc.params.CmcCalcRequestParams

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding by lazy { requireNotNull(_binding) }
    private var operator = CmcCalcType.PLUS

    private val cmcResponseCallback = object : CmcResponseCallback {
        override fun onSuccess(bundle: Bundle) {
            val result = bundle.getString(CmcBundleKey.KEY_RESULT_DATA)
            PopupDialog.showDialog(
                content = "계산결과 = $result",
                fragmentManager = supportFragmentManager
            )
        }

        override fun onFailure(bundle: Bundle) {
            val resultCode = bundle.getInt(CmcBundleKey.KEY_RESULT_CODE)
            val resultMsg = bundle.getString(CmcBundleKey.KEY_RESULT_MSG)
            PopupDialog.showDialog(
                content = "code = $resultCode, msg = $resultMsg",
                fragmentManager = supportFragmentManager
            )
        }

    }
    private val cmcServiceConnectionCallback = object : CmcServiceConnectionCallback {
        override fun onSuccess() {
            showToast("연결 성공")
        }

        override fun onFailure(bundle: Bundle) {
            val resultCode = bundle.getInt(CmcBundleKey.KEY_RESULT_CODE)
            val resultMsg = bundle.getString(CmcBundleKey.KEY_RESULT_MSG)
            PopupDialog.showDialog(
                content = "code = $resultCode, msg = $resultMsg",
                fragmentManager = supportFragmentManager
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM){
            addPadding()
        }
        initView()
    }

    private fun initView() {
        with(binding) {
            btnStart.setOnClickListener {
                startAppToApp()
            }
            groupOperator.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.btn_plus -> {
                        operator = CmcCalcType.PLUS
                    }

                    R.id.btn_minus -> {
                        operator = CmcCalcType.MINUS
                    }
                }
            }
        }
    }

    private fun addPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun startAppToApp() {
        if (CmcManager.isInstalled(this)) {
            val cmcManager = CmcManager(this)
            cmcManager.requestCmcCalc(
                CmcCalcRequestParams.Builder()
                    .setOperator(operator)
                    .setFirstNumber(binding.editFirst.text.toString())
                    .setSecondNumber(binding.editFirst.text.toString())
                    .setCmcResponseCallback(cmcResponseCallback)
                    .setCmcServiceConnectionCallback(cmcServiceConnectionCallback)
                    .build()
            )
        } else {
            PopupDialog.showDialog(
                content = "앱이 설치되지 않았습니다.",
                fragmentManager = supportFragmentManager
            )
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

