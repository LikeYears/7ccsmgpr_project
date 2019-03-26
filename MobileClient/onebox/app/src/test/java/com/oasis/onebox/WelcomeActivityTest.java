package com.oasis.onebox;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static org.junit.Assert.*;

public class WelcomeActivityTest {

    private static final String TAG = "WelcomeActivity";
    //valid user
    String vun = "123";
    String vup = "111";
    //invalid user
    String iun = "1233";
    String iup = null;
    @Test
    public void onCreate() {
        assertNotNull(TAG);
    }

    @Test
    public void getPublicKey(){
    OkHttpClient oHC = new OkHttpClient.Builder()
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .build();
    assertNotNull(oHC);
    }

}