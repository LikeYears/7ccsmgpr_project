package com.oasis.onebox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.transition.TransitionInflater;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.oasis.onebox.tool.RSA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private String IP_ADDRESS = "169.254.146.86";
    private String url = "http://"+IP_ADDRESS+":8080/api/register";

    private static final String TAG = "RegisterActivity";

    public String pubKey;

    private FloatingActionButton fab;
    private CardView cvAdd;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etRePassword;
    private Button btNext;

    public class MyCallback extends StringCallback
    {

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            Log.e(TAG, "onError:"+e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            Log.e(TAG, "onResponse：complete");
//            switch (id)
//            {
//                case 100:
//                    Toast.makeText(WelcomeActivity.this, "http", Toast.LENGTH_SHORT).show();
//                    break;
//                case 101:
//                    Toast.makeText(WelcomeActivity.this, "https", Toast.LENGTH_SHORT).show();
//                    break;
//            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        showEnterAnimation();
        initView();
        setListener();
        initOkHttp();
        getPublicKey();
    }

    public void initOkHttp()
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public void getPublicKey()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils
                        .get()
                        .url(url)
                        .build()
                        .execute(new StringCallback()
                        {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                toastString("fail to connect the server, please try it again",Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onResponse(String response, int id) {
//                        Toast.makeText(WelcomeActivity.this,response.toString(),Toast.LENGTH_LONG).show();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    pubKey = jsonObject.getString("result");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.e(TAG,pubKey);
                            }

                        });
            }
        }).start();

    }

    public void postUser()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RSA rsa = new RSA();
                OkHttpUtils
                        .post()
                        .url(url)
                        .addParams("username", rsa.encryptByPubKey(pubKey,etUsername.getText().toString()))
                        .addParams("password", rsa.encryptByPubKey(pubKey,etPassword.getText().toString()))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e(TAG,e.getMessage());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e(TAG,response.toString());
//                                Toast.makeText(RegisterActivity.this,"Register Success!",Toast.LENGTH_LONG).show();
                                toastString("Register Success!",Toast.LENGTH_LONG);
                                fabClick();
                            }
                        });
            }
        }).start();

    }

    private void fabClick()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fab.performClick();
            }
        });
    }

    private void toastString(final String toastContent, final int longOrShort)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this,toastContent,longOrShort).show();

            }
        });
    }

    private void initView(){
        fab = findViewById(R.id.fab);
        cvAdd = findViewById(R.id.cv_add);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etRePassword = findViewById(R.id.et_repeatpassword);
        btNext = findViewById(R.id.bt_next);
    }

    private void setListener(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String rePassword = etRePassword.getText().toString();
                if (password.equals(rePassword))
                {
                    postUser();
                }
                else
                {
//                    Toast.makeText(RegisterActivity.this,"Please make sure your password is correct",Toast.LENGTH_SHORT).show();
                    toastString("Please make sure your password is correct",Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void showEnterAnimation(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Transition transition = TransitionInflater.from(getApplicationContext()).inflateTransition(R.transition.fabtransition);
                getWindow().setSharedElementEnterTransition(transition);

                transition.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {
                        cvAdd.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        transition.removeListener(this);
                        animateRevealShow();
                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(Transition transition) {

                    }
                });
            }
        });

    }

    public void animateRevealShow() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
                mAnimator.setDuration(500);
                mAnimator.setInterpolator(new AccelerateInterpolator());
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        cvAdd.setVisibility(View.VISIBLE);
                        super.onAnimationStart(animation);
                    }
                });
                mAnimator.start();
            }
        });

    }

    public void animateRevealClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
                mAnimator.setDuration(500);
                mAnimator.setInterpolator(new AccelerateInterpolator());
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cvAdd.setVisibility(View.INVISIBLE);
                        super.onAnimationEnd(animation);
                        fab.setImageResource(R.drawable.plus);
                        RegisterActivity.super.onBackPressed();
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }
                });
                mAnimator.start();
            }
        });

    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
}
