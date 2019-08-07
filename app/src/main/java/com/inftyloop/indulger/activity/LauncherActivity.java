package com.inftyloop.indulger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.qmuiteam.qmui.arch.QMUILatestVisit;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zx1239856
 * A class that can hold splash screen
 */
public class LauncherActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
        } else {
            QMUIStatusBarHelper.translucent(this);
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
            }, 0);
        }
    }
}
