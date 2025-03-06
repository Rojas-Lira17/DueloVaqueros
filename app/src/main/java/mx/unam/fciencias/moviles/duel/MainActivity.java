package mx.unam.fciencias.moviles.duel;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView gunView;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private byte steps;

    private ExecutorService singleThreadProducer;

    private DrawTimer asyncCounter;
    private Button start_button;
    public static final byte SECONDS_TO_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        gunView = findViewById(R.id.gun_iv);
        start_button = findViewById(R.id.start_button);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        if (stepDetectorSensor == null) sensorManager = null;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        private void init(){
            start_button.setVisibility(View.VISIBLE);
           gunView.setVisibility(View.INVISIBLE);
           if(Build.VERSION.SDK_INIT>=Build.VERSION_CODES.Q) {
               if (!checkActivityRecognitionPermission()) {
                   ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
               }
             }
           }
           private boolean checkActivityRecognitionPermission(){
            return ContextCompat.checkSelfPermission(this,Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
        }

    }
    @Override
    protected  void onResume(){
        super.onResume();
        init();
    }
    private void checkStepSensor(){
        //verificamos si tenemos referencias al sensor
        if(sensorManager ==  null){
            startTimer();//contador de segundos
            return();
        }
    }
}