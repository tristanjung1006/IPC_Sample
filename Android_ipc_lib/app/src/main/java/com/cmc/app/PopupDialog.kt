package com.cmc.app

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.cmc.app.databinding.DialogPopupBinding

class PopupDialog(
    private val content: String
) : DialogFragment() {

    private var _binding: DialogPopupBinding? = null
    private val binding by lazy { requireNotNull(_binding) }
    private val windowManager by lazy { context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPopupBinding.inflate(inflater, container, false)

        dialog?.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setWrapDialog()
    }

    private fun setWrapDialog() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            tvContent.text = content
            btnConfirm.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun show(manager: FragmentManager, tag: String?) {
        manager.beginTransaction()
            .add(this, tag)
            .commitAllowingStateLoss()
    }

    companion object {
        private var instance: PopupDialog? = null
        fun showDialog(content: String, fragmentManager: FragmentManager) {
            instance = PopupDialog(content)
            instance?.show(fragmentManager, PopupDialog::class.java.simpleName)
        }

        fun dismissDialog() {
            if(instance != null && instance?.isAdded == true){
                instance?.dismissAllowingStateLoss()
            }
            instance = null
        }
    }
}