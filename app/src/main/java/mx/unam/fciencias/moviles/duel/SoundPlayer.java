package mx.unam.fciencias.moviles.duel;

import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

/**
 * Clase SoundPlayer que extiende JobIntentService para manejar la reproducción de sonidos en segundo plano.
 * Se encarga de procesar solicitudes de sonido de manera asíncrona sin bloquear la interfaz de usuario.
 */
public class SoundPlayer extends JobIntentService {

    /**
     * Acción para reproducir el sonido de disparo.
     */
    public static final String ACTION_FIRE = "mx.unam.fciencias.moviles.duel.action.FIRE";

    /**
     * Identificador del trabajo en la cola de JobIntentService.
     */
    private static final byte JOB_ID = 0;

    /**
     * Maneja los intent recibidos y ejecuta la acción correspondiente.
     * @param intent Intent que contiene la acción a ejecutar.
     */
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            Log.d(SoundPlayer.class.getSimpleName(), "Intent sin acción específica.");
            return;
        }

        switch (action) {
            case ACTION_FIRE:
                playFireSound();
                break;
            default:
                Log.d(SoundPlayer.class.getSimpleName(), "Acción no reconocida: " + action);
        }
    }

    /**
     * Reproduce el sonido de disparo y libera los recursos del MediaPlayer al finalizar.
     */
    private void playFireSound() {
        MediaPlayer soundPlayer = MediaPlayer.create(this, R.raw.fire);
        if (soundPlayer == null) {
            Log.e(SoundPlayer.class.getSimpleName(), "No se pudo crear MediaPlayer.");
            return;
        }

        soundPlayer.setOnCompletionListener(mp -> mp.release());
        soundPlayer.start();
    }
}