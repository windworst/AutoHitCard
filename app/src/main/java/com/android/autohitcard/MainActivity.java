package com.android.autohitcard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final Intent sSettingsIntent =
            new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    private boolean todayHitCard = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accessibility_service);
        // Add a shortcut to the accessibility settings.
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (root()) {
                    startActivity(sSettingsIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Must root", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.buttonDingding).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDingDing();
            }
        });
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                if (8 == hour && 30 <= minute && minute <= 59) {
                    if (!todayHitCard) {
                        openDingDing();
                        todayHitCard = true;
                    }
                } else {
                    todayHitCard = false;
                }
                handler.postDelayed(this, 60 * 1000);
            }
        });
    }

    private void openDingDing() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.alibaba.android.rimet");
        if (null != intent) startActivity(intent);
    }

    private boolean root() {
        try {
            ProcessBuilder builder = new ProcessBuilder("sh");
            builder.redirectErrorStream(true);
            builder.directory(new File("/"));
            Process process = builder.start();
            process.getOutputStream().write("su\n".getBytes());
            process.getOutputStream().flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
