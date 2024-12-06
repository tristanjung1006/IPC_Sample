package com.ryusw.ipc.callback;

import android.os.Bundle;

import androidx.annotation.NonNull;

public interface CmcResponseCallback {
    @NonNull
    void onSuccess(@NonNull Bundle bundle);
    @NonNull
    void onFailure(@NonNull Bundle bundle);
}
