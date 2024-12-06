package com.cmc.android

object CmcResultCode {
    private const val CMC_BASE_CODE: Int = 200000
    const val RESULT_SUCCESS: Int = CMC_BASE_CODE + 1
    const val ERROR_BIND_FAIL: Int = CMC_BASE_CODE + 2
    const val ERROR_UNSUPPORTED_NUMBER: Int = CMC_BASE_CODE + 3
    const val ERROR_UNKNOWN: Int = CMC_BASE_CODE + 4
    const val ERROR_INVALID_PARAMS: Int = CMC_BASE_CODE + 5
}

object CmcMessageType {
    const val REQ: Int = 1
    const val RESP: Int = 2
}

object CmcResultMsg {
    const val SUCCESS: String = "success"
    const val ERROR_FIRST_NUMBER_EMPTY: String = "first number is empty or null"
    const val ERROR_SECOND_NUMBER_EMPTY: String = "second number is empty or null"
    const val ERROR_OPERATOR_NONE: String = "operator is empty or null"
    const val ERROR_CONNECTION_CALLBACK_NULL: String = "connection callback must be initialize"
    const val ERROR_FAIL_BIND_SERVICE: String = "fail bind service"
    const val ERROR_INVALID_PARAMS: String = "invalid params"
    const val ERROR_UNKNOWN: String = "unknown exception"
}

object CmcBundleKey {
    /* REQ KEY */
    const val KEY_FIRST_NUMBER: String = "key_first_number"
    const val KEY_SECOND_NUMBER: String = "key_second_number"
    const val KEY_OPERATOR: String = "key_operator"
    const val KEY_TASK_ID: String = "key_task_id"
    const val KEY_JOB_ID: String = "key_job_id"

    /* RESP KEY */

    const val KEY_RESULT_CODE: String = "key_result_code"
    const val KEY_RESULT_MSG: String = "key_result_msg"
    const val KEY_RESULT_DATA: String = "key_result_data"
}