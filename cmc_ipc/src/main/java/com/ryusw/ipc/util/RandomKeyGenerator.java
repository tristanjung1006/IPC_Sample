package com.ryusw.ipc.util;

import java.security.SecureRandom;

public class RandomKeyGenerator implements KeyGenerator{

    @Override
    public String nextKey() {
        long time = System.currentTimeMillis();
        long s1 = new SecureRandom().nextLong();
        long s2 = new SecureRandom().nextLong();
        long s3 = new SecureRandom().nextLong();

        String temp = Long.toHexString(((time & s1) | s2) ^ s3).toUpperCase();

        for ( int i=temp.length(); i< KEY_SIZE; i++ )
            temp += new SecureRandom().nextInt(10);

        return temp;
    }

    @Override
    public String nextNumberKey() {

        long time = System.currentTimeMillis();
        long s1 = new SecureRandom().nextLong();
        long s2 = new SecureRandom().nextLong();
        long s3 = new SecureRandom().nextLong();

        String temp = Long.toString(Math.abs(((time & s1) | s2) ^ s3));

        for ( int i=temp.length(); i< KEY_SIZE; i++ )
            temp += new SecureRandom().nextInt(10);

        return temp;
    }
}
