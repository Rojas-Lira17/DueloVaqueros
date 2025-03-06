package mx.unam.fciencias.moviles.duel;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.JobIntentService;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;

import kotlinx.coroutines.Job;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView gunView;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private byte steps;
    private ExecutorService singleThreadProducer;
    private DrawTimer asyncCounter;
    public static final byte SECONDS_TO_COUNT = 3;



    private Button start_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        gunView = findViewById(R.id.gun_iv);
        start_button = findViewById(R.id.start_button);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        if (stepDetectorSensor == null) sensorManager = null;

    }

    protected void onResume() {
        super.onResume();
        init();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    public void finalCountdown(View start_button) {
        start_button.setVisibility(View.INVISIBLE);
        checkStepSensor();
    }

    private void init() {
        start_button.setVisibility(View.VISIBLE);
        gunView.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!checkActivityRecognitionPermission()) {
                ActivityCompat.requestPermission(this,
                        new String[] {Manifest.permission.ACTIVITY_RECOGNITION, 0});
            }
        }
    }

    @TargetApi(29)
    private boolean checkActivityRecognitionPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        steps++;
        if(steps >= 3) {
            sensorManager.unregisterListener(this);
            gunView.setVisibility(View.VISIBLE);
            steps = 0;
        }
    }

    private void checkStepSensor() {
        if(sensorManager == null) {
            startTimer();
            return;
        }
        sensorManager.registerListener(this, stepDetectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void startTimer() {

    }

    private void fire(View gun) {
        JobIntentService.enqueueWork(this,

                SoundPlayer.class, 0, new Intent(SoundPlayer.ACTION_FIRE));
    }


}