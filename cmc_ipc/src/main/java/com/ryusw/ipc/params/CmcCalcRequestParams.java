package com.ryusw.ipc.params;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ryusw.ipc.callback.CmcResponseCallback;
import com.ryusw.ipc.callback.CmcServiceConnectionCallback;
import com.ryusw.ipc.constant.CmcResultMsg;
import com.ryusw.ipc.context.CmcCalcType;

import java.security.InvalidParameterException;

public class CmcCalcRequestParams {
    @NonNull
    private String firstNumber = "";
    @NonNull
    private String secondNumber = "";
    private @CmcCalcType.CalcType int operator = CmcCalcType.NONE;
    @NonNull
    private CmcServiceConnectionCallback cmcServiceConnectionCallback;
    @Nullable
    private CmcResponseCallback cmcResponseCallback = null;

    private CmcCalcRequestParams(
            @NonNull String firstNumber,
            @NonNull String secondNumber,
            @CmcCalcType.CalcType int operator,
            @NonNull CmcServiceConnectionCallback cmcServiceConnectionCallback,
            @Nullable CmcResponseCallback cmcResponseCallback
    ) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
        this.operator = operator;
        this.cmcServiceConnectionCallback = cmcServiceConnectionCallback;
        this.cmcResponseCallback = cmcResponseCallback;
    }

    @NonNull
    public String getFirstNumber() {
        return firstNumber;
    }

    @NonNull
    public String getSecondNumber() {
        return secondNumber;
    }

    public int getOperator() {
        return operator;
    }

    @NonNull
    public CmcServiceConnectionCallback getCmcServiceConnectionCallback() {
        return cmcServiceConnectionCallback;
    }

    @Nullable
    public CmcResponseCallback getCmcResponseCallback() {
        return cmcResponseCallback;
    }

    public static final class Builder {
        @NonNull
        private String firstNumber = "";
        @NonNull
        private String secondNumber = "";
        private @CmcCalcType.CalcType int operator = CmcCalcType.NONE;
        @Nullable
        private CmcServiceConnectionCallback cmcServiceConnectionCallback;
        @Nullable
        private CmcResponseCallback cmcResponseCallback = null;

        public Builder setFirstNumber(@NonNull String firstNumber) {
            this.firstNumber = firstNumber;
            return this;
        }

        public Builder setSecondNumber(@NonNull String secondNumber) {
            this.secondNumber = secondNumber;
            return this;
        }

        public Builder setOperator(int operator) {
            this.operator = operator;
            return this;
        }

        public Builder setCmcServiceConnectionCallback(@NonNull CmcServiceConnectionCallback cmcServiceConnectionCallback) {
            this.cmcServiceConnectionCallback = cmcServiceConnectionCallback;
            return this;
        }

        public Builder setCmcResponseCallback(@Nullable CmcResponseCallback cmcResponseCallback) {
            this.cmcResponseCallback = cmcResponseCallback;
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "firstNumber='" + firstNumber + '\'' +
                    ", secondNumber='" + secondNumber + '\'' +
                    ", operator=" + operator +
                    '}';
        }

        public CmcCalcRequestParams build() throws InvalidParameterException {
            if (firstNumber.isEmpty()) {
                throw new InvalidParameterException(CmcResultMsg.ERROR_FIRST_NUMBER_EMPTY);
            }
            if (secondNumber.isEmpty()) {
                throw new InvalidParameterException(CmcResultMsg.ERROR_SECOND_NUMBER_EMPTY);
            }
            if (operator == CmcCalcType.NONE) {
                throw new InvalidParameterException(CmcResultMsg.ERROR_OPERATOR_NONE);
            }
            if (cmcServiceConnectionCallback == null) {
                throw new NullPointerException(CmcResultMsg.ERROR_CONNECTION_CALLBACK_NULL);
            }
            CmcCalcRequestParams params = new CmcCalcRequestParams(
                    firstNumber,
                    secondNumber,
                    operator,
                    cmcServiceConnectionCallback,
                    cmcResponseCallback
            );

            return params;
        }
    }
}
