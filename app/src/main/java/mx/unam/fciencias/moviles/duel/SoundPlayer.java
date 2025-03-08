package mx.unam.fciencias.moviles.duel;

import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

// Esta clase extiende JobIntentService y se encarga de reproducir un sonido cuando se recibe una acción.
public class SoundPlayer extends JobIntentService {

    // Constante que define la acción de "disparo" (reproducir sonido).
    public static final String ACTION_FIRE = "mx.unam.fciencias.moviles.duel.action.FIRE";

    // ID único para el trabajo en segundo plano.
    private static final byte JOB_ID = 0;

    // Este método es llamado cuando se necesita realizar el trabajo en segundo plano.
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String action = intent.getAction();  // Obtiene la acción que se pasó con el Intent.

        // Controla qué hacer según la acción recibida.
        switch (action) {
            case ACTION_FIRE:
                // Si la acción es "FIRE", reproduce un sonido de disparo.
                MediaPlayer soundPlayer = MediaPlayer.create(this, R.raw.fire);  // Crea un MediaPlayer para reproducir el archivo "fire" (sonido de disparo).

                // Establece un listener para liberar el MediaPlayer cuando termine de reproducir el sonido.
                soundPlayer.setOnCompletionListener(
                        new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();  // Libera los recursos del MediaPlayer una vez que termine de reproducir el sonido.
                            }
                        }
                );
                soundPlayer.start();  // Inicia la reproducción del sonido.
                break;

            default:
                // Si la acción no es reconocida, se registra un mensaje de depuración.
                Log.d(SoundPlayer.class.getSimpleName(), "Unrecognized action: " + action);
        }
    }
}