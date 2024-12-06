package com.ryusw.ipc.context;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CmcCalcType {
    public static final int NONE        = 0;
    public static final int PLUS        = 1;
    public static final int MINUS       = 2;

    @IntDef({NONE, PLUS, MINUS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CalcType {}

}
