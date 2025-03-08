package mx.unam.fciencias.moviles.duel;

import android.annotation.TargetApi;
import android.content.Context;
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
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView gunView;
    private Button start_button;
    private Sensor StepDetectorSensor;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor; // referencia al podometro.
    private ExecutorService singleThreadProducer;
    private DrawTimer asyncCounter;


    private byte steps;

    public static final byte SECONDS_TO_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        gunView = findViewById(R.id.gun_iv);
        setContentView(R.layout.activity_main);
        gunView = findViewById(R.id.gun_iv);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null){
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        if ( stepDetectorSensor == null) sensorManager = null;
    }

    @Override
    protected void onResume(){
        super.onResume();
        init();
    }

    @Override
    protected void onPause(){
        if (sensorManager != null){
            sensorManager.unregisterListener(this);
        }

        super.onPause();
    }




    public  void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN

            );
        }
    }
    public void finalCountdown(View startButton){
        startButton.setVisibility(View.INVISIBLE);
        checkStepsensor();
    }

    private void checkStepsensor(){
        if(sensorManager== null){
            startTimer();
            return;
        }

        sensorManager.registerListener(this, stepDetectorSensor, sensorManager.SENSOR_DELAY_NORMAL);
    }



    private void init(){
        start_button.setVisibility(View.VISIBLE);
        gunView.setVisibility(gunView.INVISIBLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if(!checkActivityRecognitionPermission()){
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }
        }
    }

    @TargetApi(29)
    private boolean checkActivityRecognitionPermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
    }


    private void startTimer(){
        if (singleThreadProducer == null){
            singleThreadProducer = Executors.newSingleThreadExecutor();
        }
        asyncCounter = new DrawTimer(gunView, SECONDS_TO_COUNT);
        singleThreadProducer.execute(asyncCounter);
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        steps++;
        if(steps >= 3) {
            sensorManager.unregisterListener(this);
            gunView.setVisibility(View.VISIBLE);
            steps = 0;
        }
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy){

    }








}

