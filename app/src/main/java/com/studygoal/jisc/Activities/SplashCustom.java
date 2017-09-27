package com.studygoal.jisc.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.R;

public class SplashCustom extends AppCompatActivity {

    // Note: Currently as a DEMO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NetworkManager.getInstance().init(getApplicationContext());
        DataManager.getInstance().currActivity = this;

        if(getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            DataManager.getInstance().isLandscape = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DataManager.getInstance().isLandscape = false;
        }

        setContentView(R.layout.activity_splash);

        try {
            VideoView videoHolder = (VideoView) findViewById(R.id.video_view);
            float videoProportion = 1.5f;
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            float screenProportion = (float) screenHeight / (float) screenWidth;
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoHolder.getLayoutParams();

            if (videoProportion < screenProportion) {
                lp.height = screenHeight;
                lp.width = (int) ((float) screenHeight / videoProportion);
            } else {
                lp.width = screenWidth;
                lp.height = (int) ((float) screenWidth * videoProportion);
            }

            Uri video = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.splash);
            if(DataManager.getInstance().isLandscape) {
                video = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.splash_screen_tablet);
            }

            videoHolder.setLayoutParams(lp);
            videoHolder.setVideoURI(video);

            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    jump();
                }

            });
            videoHolder.start();
        } catch (Exception e) {
            e.printStackTrace();
            jump();
        }
    }

    public void jump() {
        Intent intent = new Intent(SplashCustom.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

}