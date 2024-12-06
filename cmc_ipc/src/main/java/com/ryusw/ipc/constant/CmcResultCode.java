package com.ryusw.ipc.constant;

public class CmcResultCode {
    private static final int CMC_BASE_CODE                       = 200000;

    public static final int RESULT_SUCCESS                      = CMC_BASE_CODE + 1;
    public static final int ERROR_BIND_FAIL                     = CMC_BASE_CODE + 2;
    public static final int ERROR_UNSUPPORTED_NUMBER            = CMC_BASE_CODE + 3;
    public static final int ERROR_UNKNOWN                       = CMC_BASE_CODE + 4;
    public static final int ERROR_INVALID_PARAMS                = CMC_BASE_CODE + 5;
}
