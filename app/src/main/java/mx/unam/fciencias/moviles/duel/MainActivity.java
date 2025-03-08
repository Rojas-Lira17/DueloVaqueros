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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MainActivity que implementa SensorEventListener para detectar pasos y activar una imagen.
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView gunView; // Imagen del arma
    private Button start_button; // Botón de inicio
    private SensorManager sensorManager; // Administrador de sensores
    private Sensor stepDetectorSensor; // Sensor de podómetro
    private ExecutorService singleThreadProducer; // Hilo para la cuenta regresiva
    private DrawTimer asyncCounter; // Temporizador de cuenta regresiva

    private byte steps; // Contador de pasos
    public static final byte SECONDS_TO_COUNT = 3; // Segundos para la cuenta regresiva

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        gunView = findViewById(R.id.gun_iv);

        // Inicialización del sensor de pasos
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        if (stepDetectorSensor == null) sensorManager = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onPause() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        super.onPause();
    }

    /**
     * Configura la pantalla para modo inmersivo cuando cambia el enfoque de la ventana.
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    /**
     * Inicia la cuenta regresiva al ocultar el botón de inicio y verificar el sensor de pasos.
     */
    public void finalCountdown(View startButton) {
        startButton.setVisibility(View.INVISIBLE);
        checkStepsensor();
    }

    /**
     * Verifica si el sensor de pasos está disponible y lo registra.
     */
    private void checkStepsensor() {
        if (sensorManager == null) {
            startTimer();
            return;
        }
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Inicializa la interfaz y solicita permisos si es necesario.
     */
    private void init() {
        start_button.setVisibility(View.VISIBLE);
        gunView.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!checkActivityRecognitionPermission()) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }
        }
    }

    /**
     * Verifica si el permiso de reconocimiento de actividad ha sido concedido.
     */
    @TargetApi(29)
    private boolean checkActivityRecognitionPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Inicia la cuenta regresiva con un temporizador en un hilo separado.
     */
    private void startTimer() {
        if (singleThreadProducer == null) {
            singleThreadProducer = Executors.newSingleThreadExecutor();
        }
        asyncCounter = new DrawTimer(gunView, SECONDS_TO_COUNT);
        singleThreadProducer.execute(asyncCounter);
    }

    /**
     * Método invocado cuando el sensor detecta un cambio (paso registrado).
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        steps++;
        if (steps >= 3) {
            sensorManager.unregisterListener(this);
            gunView.setVisibility(View.VISIBLE);
            steps = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se requiere implementación en este caso
    }
}