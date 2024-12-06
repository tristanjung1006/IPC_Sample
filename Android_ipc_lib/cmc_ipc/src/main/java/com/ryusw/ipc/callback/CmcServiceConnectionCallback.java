package com.ryusw.ipc.callback;

import android.os.Bundle;

import androidx.annotation.NonNull;

public interface CmcServiceConnectionCallback {
    @NonNull
    void onSuccess();
    @NonNull
    void onFailure(@NonNull Bundle bundle);
}
