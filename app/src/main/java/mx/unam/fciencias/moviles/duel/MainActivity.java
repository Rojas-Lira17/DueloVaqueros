package mx.unam.fciencias.moviles.duel;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView gunView;
    private Button start_button;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private byte step;
    private ExecutorService singleThreadProducer;
    private DrawTimer asyncCounter;
    public static final byte SECONDS_TO_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gunView = findViewById(R.id.gun_iv);
        start_button = findViewById(R.id.start_button);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        if (stepDetectorSensor == null) {
            sensorManager = null;
        }
    }

    @Override
    protected void onPause() {
        killCounter();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    private void init() {
        gunView.setVisibility(View.INVISIBLE);
        start_button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!checkActivityRecognitionPermission()) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }
        }
    }

    private boolean checkActivityRecognitionPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(29)
    public void finalCountdown(View startButton) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startButton.setVisibility(View.INVISIBLE);
        checkStepSensor();
    }

    private void checkStepSensor() {
        if (sensorManager == null) {
            startTimer();
            return;
        }
        sensorManager.registerListener(this, stepDetectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        step++;
        if (step >= 3) {
            sensorManager.unregisterListener(this);
            gunView.setVisibility(View.VISIBLE);
            step = 0;
        }
    }

    private void startTimer() {
        if (singleThreadProducer == null) {
            singleThreadProducer = Executors.newSingleThreadExecutor();
        }
        asyncCounter = new DrawTimer(gunView, SECONDS_TO_COUNT);
        singleThreadProducer.execute(asyncCounter);
    }

    public void fire(View gun) {
        JobIntentService.enqueueWork(this, SoundPlayer.class, 0,
                new Intent(SoundPlayer.ACTION_FIRE));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 3000);
    }

    private void killCounter() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        } else if (singleThreadProducer != null) {
            singleThreadProducer.shutdown();
            singleThreadProducer = null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

