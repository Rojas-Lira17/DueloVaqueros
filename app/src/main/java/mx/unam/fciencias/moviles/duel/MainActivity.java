package mx.unam.fciencias.moviles.duel;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView gunView;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;

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
    public  void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){

        }
    }
    public void finalCountdown(View startButton){
        startButton.setVisibility(View.INVISIBLE);

    }
}