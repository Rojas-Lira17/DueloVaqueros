package mx.unam.fciencias.moviles.duel;

import android.widget.ImageView;

public class DrawTimer implements Runnable {
    private ImageView gunView;
    private final byte COUNT_TO;

    public DrawTimer(ImageView gunView, byte countTo) {
        this.gunView = gunView;
        COUNT_TO = countTo;
    }

    @Override
    public void run() {

    }
}
