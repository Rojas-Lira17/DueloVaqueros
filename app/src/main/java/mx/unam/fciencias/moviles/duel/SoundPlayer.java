package mx.unam.fciencias.moviles.duel;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class SoundPlayer extends JobIntentService {

    public static final String ACTION_FIRE = "mx.unam.fciencias.moviles.duel.action.FIRE";
    private static final byte JOB_IO = 0;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }
}

