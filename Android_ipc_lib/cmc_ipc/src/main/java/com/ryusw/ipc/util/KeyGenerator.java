package com.ryusw.ipc.util;

public interface KeyGenerator {
    /**
     * 20자리의 RandomKey를 생성한다.
     */
    public static final int KEY_SIZE = 10;

    /**
     *  20자리의 Number RandomKey를 생성한다.
     */
    public static final int NUMBER_KEY_SIZE = 10;


    /**
     * 20자리의 순차적인 Number RandomKey를 생성한다.
     */
    public static final int NUMBER_SEQ_KEY_SIZE = 10;



    public String nextKey();
    public String nextNumberKey();
}
