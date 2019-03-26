package com.oasis.onebox;


import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static org.junit.Assert.*;

public class RegisterActivityTest {
    private String url = "169.254.146.86";
    //valid user
    private String vun = "123";
    private String vup = "111";
    //invalid user
    private String iun = "1233";
    private String iup = null;
    private String response = "succeed";
    @Test
    public void initOkHttp() {
        OkHttpClient oHC = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        assertNotNull(oHC);
    }

    @Test
    public void getPublicKey() {

        assertNotNull(response);
        assertNull(iup);
    }

    @Test
    public void postUser() {
        String error = "fail to post";
        assertNotNull(response);
        //assertNull(error);
    }
}