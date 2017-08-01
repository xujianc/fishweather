package com.example.xujia.fishweather;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xujia on 2017/8/1.
 */

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏标题栏以及状态栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**
         * 标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题
         */
        // requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.welcome);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        ImageView welcome_img = (ImageView) findViewById(R.id.welcome_img);
        Glide.with(this).load(R.drawable.welcome).into(welcome_img);

        //背景透明度变化3秒内从0.3变到1.0
        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        aa.setDuration(3000);
        linearLayout.startAnimation(aa);

        //创建Timer对象
        Timer timer = new Timer();
        //创建TimerTask对象
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        //使用timer.schedule（）方法调用timerTask，定时3秒后执行run
        timer.schedule(timerTask, 4000);
    }
}
