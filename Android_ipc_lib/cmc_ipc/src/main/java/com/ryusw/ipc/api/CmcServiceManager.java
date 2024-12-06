package com.ryusw.ipc.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.ryusw.ipc.callback.CmcResponseCallback;
import com.ryusw.ipc.callback.CmcServiceConnectionCallback;
import com.ryusw.ipc.constant.CmcBundleKey;
import com.ryusw.ipc.constant.CmcResultCode;
import com.ryusw.ipc.constant.CmcResultMsg;
import com.ryusw.ipc.setting.Config;


public class CmcServiceManager {
    private static final String CmcServiceClassName = "com.cmc.android.service.CmcCalcService";
    private static final String CLASSNAME = "CmcServiceManager";

    private final Handler responseHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle responseBundle = msg.getData();

            int respCode = responseBundle.getInt(CmcBundleKey.KEY_RESULT_CODE);
            String respMsg = responseBundle.getString(CmcBundleKey.KEY_RESULT_MSG);
            String result = responseBundle.getString(CmcBundleKey.KEY_RESULT_DATA);

            responseToApp(respCode, respMsg, result);
        }
    };

    private Context mContext = null;
    private Messenger mService = null;
    private final Messenger mRespMessenger = new Messenger(responseHandler);
    private ServiceConnection mConnection;
    private boolean mIsBind = false;
    private CmcResponseCallback mCmcResponseCallback;
    private CmcServiceConnectionCallback mCmcServiceConnectionCallback;

    public CmcServiceManager(Context mContext) {
        this.mContext = mContext;
    }

    public void setResponseCallback(CmcResponseCallback cmcResponseCallback) {
        this.mCmcResponseCallback = cmcResponseCallback;
    }

    public void setConnectionCallback(CmcServiceConnectionCallback cmcServiceConnectionCallback) {
        this.mCmcServiceConnectionCallback = cmcServiceConnectionCallback;
    }

    private void responseToApp(int respCode, String respMsg, String result) {
        if (mCmcResponseCallback == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(CmcBundleKey.KEY_RESULT_CODE, respCode);
        bundle.putString(CmcBundleKey.KEY_RESULT_MSG, respMsg);
        bundle.putString(CmcBundleKey.KEY_RESULT_DATA, result);
        if (respCode == CmcResultCode.RESULT_SUCCESS) {
            mCmcResponseCallback.onSuccess(bundle);
        } else {
            mCmcResponseCallback.onFailure(bundle);
        }
    }

    private synchronized void doConnection(final IServiceConnection iServiceConnection) {
        if (mIsBind) {
            iServiceConnection.onConnection();
            return;
        }
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = new Messenger(service);
                mIsBind = true;
                if (iServiceConnection != null) {
                    iServiceConnection.onConnection();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
                mIsBind = false;
            }
        };

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(Config.FLAVOR_APPCLIATION_ID, CmcServiceClassName);
        intent.setComponent(componentName);

        int bindFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            bindFlag = Context.BIND_ALLOW_ACTIVITY_STARTS | Context.BIND_AUTO_CREATE;
        } else {
            bindFlag = Context.BIND_AUTO_CREATE;
        }
        boolean connection = mContext.bindService(intent, mConnection, bindFlag);
        if (!connection && iServiceConnection != null) {
            iServiceConnection.onFailure(CmcResultCode.ERROR_BIND_FAIL, CmcResultMsg.ERROR_FAIL_BIND_SERVICE);
        }
    }

    public void disConnection() {
        if (mIsBind && mConnection != null) {
            mContext.unbindService(mConnection);
            mIsBind = false;
        }
        mConnection = null;
    }

    public void sendMessage(@NonNull Message msg) {
        doConnection(new IServiceConnection() {
            @Override
            public void onConnection() {
                try {
                    msg.replyTo = mRespMessenger;
                    mService.send(msg);

                    mCmcServiceConnectionCallback.onSuccess();
                } catch (RemoteException e) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(CmcBundleKey.KEY_RESULT_CODE, CmcResultCode.ERROR_BIND_FAIL);
                    bundle.putString(CmcBundleKey.KEY_RESULT_MSG, CmcResultMsg.ERROR_FAIL_BIND_SERVICE);
                    mCmcServiceConnectionCallback.onFailure(bundle);
                }
                catch (Exception exception) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(CmcBundleKey.KEY_RESULT_CODE, CmcResultCode.ERROR_UNKNOWN);
                    bundle.putString(CmcBundleKey.KEY_RESULT_MSG, CmcResultMsg.ERROR_UNKNOWN);
                    mCmcServiceConnectionCallback.onFailure(bundle);
                }
            }

            @Override
            public void onFailure(int errorCode, String errorMsg) {
                Bundle bundle = new Bundle();
                bundle.putInt(CmcBundleKey.KEY_RESULT_CODE, errorCode);
                bundle.putString(CmcBundleKey.KEY_RESULT_MSG, errorMsg);
                mCmcServiceConnectionCallback.onFailure(bundle);
            }
        });
    }

    interface IServiceConnection {
        void onConnection();

        void onFailure(int errorCode, String errorMsg);
    }
}
