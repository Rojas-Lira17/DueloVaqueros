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

    // Definición de las vistas y variables de la clase.
    private ImageView gunView;  // Vista del arma.
    private Button start_button;  // Botón para iniciar la cuenta atrás.
    private SensorManager sensorManager;  // Gestor de sensores.
    private Sensor stepDetectorSensor;  // Sensor de detección de pasos.
    private byte step;  // Contador de pasos.
    private ExecutorService singleThreadProducer;  // Servicio de ejecución en un solo hilo.
    private DrawTimer asyncCounter;  // Temporizador para la cuenta atrás.
    public static final byte SECONDS_TO_COUNT = 3;  // Número de segundos para la cuenta atrás.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Establece el layout de la actividad.

        // Inicialización de las vistas.
        gunView = findViewById(R.id.gun_iv);  // Referencia a la imagen del arma.
        start_button = findViewById(R.id.start_button);  // Referencia al botón de inicio.

        // Inicialización del gestor de sensores.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);  // Obtener el sensor de pasos.
        }
        if (stepDetectorSensor == null) {
            sensorManager = null;  // Si no existe el sensor, lo dejamos como null.
        }
    }

    @Override
    protected void onPause() {
        killCounter();  // Detiene cualquier contador en ejecución cuando la actividad entra en pausa.
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();  // Inicializa los elementos cuando la actividad se reanuda.
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);  // Establece la orientación de la pantalla en modo paisaje.
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |  // Habilita el modo inmersivo.
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    private void init() {
        gunView.setVisibility(View.INVISIBLE);  // Hace invisible el arma al inicio.
        start_button.setVisibility(View.VISIBLE);  // Hace visible el botón de inicio.

        // Verifica si el dispositivo tiene el permiso de "ACTIVITY_RECOGNITION" para acceder a los sensores de actividad.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!checkActivityRecognitionPermission()) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, 0);  // Solicita el permiso si no está concedido.
            }
        }
    }

    private boolean checkActivityRecognitionPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
        // Verifica si el permiso de "ACTIVITY_RECOGNITION" ha sido concedido.
    }

    @TargetApi(29)
    public void finalCountdown(View startButton) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // Evita que la pantalla se apague durante el conteo.

        startButton.setVisibility(View.INVISIBLE);  // Hace invisible el botón de inicio.
        checkStepSensor();  // Comienza a verificar los pasos.
    }

    private void checkStepSensor() {
        if (sensorManager == null) {
            startTimer();  // Si no hay sensor, inicia el temporizador.
            return;
        }
        sensorManager.registerListener(this, stepDetectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL);  // Registra el listener para el sensor de pasos.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        step++;  // Incrementa el contador de pasos cada vez que se detecta un paso.
        if (step >= 3) {
            sensorManager.unregisterListener(this);  // Desregistra el listener del sensor después de 3 pasos.
            gunView.setVisibility(View.VISIBLE);  // Hace visible el arma cuando se han detectado los 3 pasos.
            step = 0;  // Resetea el contador de pasos.
        }
    }

    private void startTimer() {
        if (singleThreadProducer == null) {
            singleThreadProducer = Executors.newSingleThreadExecutor();  // Crea un ejecutor de un solo hilo.
        }
        asyncCounter = new DrawTimer(gunView, SECONDS_TO_COUNT);  // Inicializa el temporizador con el tiempo especificado.
        singleThreadProducer.execute(asyncCounter);  // Ejecuta el temporizador en el hilo.
    }

    public void fire(View gun) {
        // Envia una acción de disparo al servicio SoundPlayer para reproducir un sonido de disparo.
        JobIntentService.enqueueWork(this, SoundPlayer.class, 0,
                new Intent(SoundPlayer.ACTION_FIRE));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // Libera el bloqueo de pantalla.

        // Retorna a la pantalla inicial después de 3 segundos.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();  // Vuelve a inicializar la interfaz.
            }
        }, 3000);  // Retrasa la acción por 3 segundos.
    }

    private void killCounter() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);  // Desregistra el listener del sensor cuando ya no se necesita.
        } else if (singleThreadProducer != null) {
            singleThreadProducer.shutdown();  // Detiene el hilo si es necesario.
            singleThreadProducer = null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Este método no se utiliza, pero es parte de la interfaz SensorEventListener.
    }
}
