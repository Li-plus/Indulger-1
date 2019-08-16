package com.inftyloop.indulger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import com.qmuiteam.qmui.arch.QMUILatestVisit;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zx1239856
 * A class that can hold splash screen
 */
public class LauncherActivity extends Activity {
    @BindView(R.id.main_splash)
    ImageView mainSplash;
    @BindView(R.id.main_splash_text)
    ImageView mainSplashText;
    @BindView(R.id.splash_container)
    RelativeLayout splashContainer;

    private void startAnimation() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        anim.reset();
        splashContainer.clearAnimation();
        mainSplashText.startAnimation(anim);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
        } else {
            setContentView(R.layout.splash_screen);
            ButterKnife.bind(this);
            QMUIStatusBarHelper.translucent(this);
            startAnimation();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent = QMUILatestVisit.intentOfLatestVisit(LauncherActivity.this);
                    if (intent == null)
                        intent = new Intent(LauncherActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1700);
        }
    }
}
