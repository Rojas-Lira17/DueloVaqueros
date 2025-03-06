package mx.unam.fciencias.moviles.duel;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView gunView;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;

    private byte steps;
    private ExecutorService singleThreadProducer;
    private DrawTimer asyncCounter;
    public static final byte SECONDS_TO_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        gunView = findViewById(R.id.gun_iv);
        setContentView(R.layout.activity_main);
        gunView = findViewById(R.id.gun_iv); //Es para buscar in id especifico
        start_button = findViewById(R.id.start_button);
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

    //Inicia el boton de inicio, vamos a establecer cuando nuestro metodo va a ser utilizado
    private void init(){
        start_button.setVisibility(View.VISIBLE);
        gunView.setVisibility(View.INVISIBLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if(!checkActivityRecognitionPermission()){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACTIVITY_RECOGNITION},0);
            }
        }
    }
    //Checa si tienes los permisos adecuados
    private boolean checkActivityRecognitionPermission(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
    }

    public void onSensorChanged(SensorEvent sensorEvent){
        steps++;

        if (steps >=3){
            sensorManager.unre
        }
    }

    private  void checkStepSensor(){

    }

}