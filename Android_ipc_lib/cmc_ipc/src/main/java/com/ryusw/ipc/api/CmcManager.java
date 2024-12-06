package com.ryusw.ipc.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.ryusw.ipc.callback.CmcResponseCallback;
import com.ryusw.ipc.constant.CmcBundleKey;
import com.ryusw.ipc.constant.CmcMessageType;
import com.ryusw.ipc.constant.CmcResultCode;
import com.ryusw.ipc.constant.CmcResultMsg;
import com.ryusw.ipc.params.CmcCalcRequestParams;
import com.ryusw.ipc.setting.Config;
import com.ryusw.ipc.util.RandomKeyGenerator;

public class CmcManager {
    private static final String CLASSNAME = "CmcManager";
    private CmcServiceManager mCmcServiceManager;
    private Context mContext;

    public CmcManager(@NonNull Context context) {
        mContext = context;
        if (mCmcServiceManager == null) {
            mCmcServiceManager = new CmcServiceManager(context);
        }
    }

    public void release() {
        if (mCmcServiceManager == null) {
            return;
        }
        mCmcServiceManager.disConnection();
    }

    public void requestCmcCalc(@NonNull CmcCalcRequestParams params) {
        mCmcServiceManager.setConnectionCallback(params.getCmcServiceConnectionCallback());
        CmcResponseCallback responseCallback = params.getCmcResponseCallback();
        if (responseCallback != null) {
            mCmcServiceManager.setResponseCallback(responseCallback);
        }
        try {
            Bundle bundle = generateRequestBundle(params);
            Message msg = new Message();
            msg.what = CmcMessageType.REQ;
            msg.setData(bundle);
            mCmcServiceManager.sendMessage(msg);
        } catch (Exception exception) {
            params.getCmcServiceConnectionCallback().onFailure(generateErrorBundle());
        }
    }

    private Bundle generateErrorBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(CmcBundleKey.KEY_RESULT_CODE, CmcResultCode.ERROR_UNKNOWN);
        bundle.putString(CmcBundleKey.KEY_RESULT_MSG, CmcResultMsg.ERROR_UNKNOWN);
        return bundle;
    }

    private Bundle generateRequestBundle(CmcCalcRequestParams params) {
        Bundle bundle = new Bundle();
        bundle.putString(CmcBundleKey.KEY_JOB_ID, new RandomKeyGenerator().nextNumberKey());
        bundle.putInt(CmcBundleKey.KEY_TASK_ID, getTaskId());
        bundle.putInt(CmcBundleKey.KEY_OPERATOR, params.getOperator());
        bundle.putString(CmcBundleKey.KEY_FIRST_NUMBER, params.getFirstNumber());
        bundle.putString(CmcBundleKey.KEY_SECOND_NUMBER, params.getSecondNumber());
        return bundle;
    }

    private int getTaskId() {
        return ((Activity) mContext).getTaskId();
    }

    public static boolean isInstalled(@NonNull Context context) {
        return getApplicationInfo(context) != null;
    }

    @Nullable
    private static ApplicationInfo getApplicationInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                applicationInfo = packageManager.getApplicationInfo(
                        Config.FLAVOR_APPCLIATION_ID,
                        PackageManager.ApplicationInfoFlags.of(0)
                );
            } else {
                applicationInfo = packageManager.getApplicationInfo(
                        Config.FLAVOR_APPCLIATION_ID,
                        PackageManager.GET_META_DATA
                );
            }
        } catch (Exception e) {
            return null;
        }
        return applicationInfo;
    }
}
