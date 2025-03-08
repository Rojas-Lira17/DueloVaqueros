package mx.unam.fciencias.moviles.duel;

import android.view.View;
import android.widget.ImageView;

/**
 * Clase DrawTimer que implementa Runnable para manejar una cuenta regresiva en un hilo separado.
 */
public class DrawTimer implements Runnable {

    private ImageView gunView; // Imagen del arma
    private final byte COUNT_TO; // Tiempo de cuenta regresiva en segundos

    /**
     * Constructor de DrawTimer.
     * @param gunView ImageView que se hará visible tras la cuenta regresiva.
     * @param countTo Número de segundos a contar antes de mostrar la imagen.
     */
    public DrawTimer(ImageView gunView, byte countTo) {
        this.gunView = gunView;
        COUNT_TO = countTo;
    }

    /**
     * Método ejecutado en un hilo separado, cuenta los segundos y luego muestra la imagen.
     */
    @Override
    public void run() {
        byte counter = 0;
        // Ciclo mientras no se alcance el límite
        while (counter < COUNT_TO) {
            try {
                Thread.sleep(1000); // Pausa de 1 segundo
            } catch (InterruptedException e) {
                // Manejo de la interrupción si es necesario
            }
            counter++;
        }
        postVisibilityToUI();
    }

    /**
     * Método para actualizar la interfaz de usuario y hacer visible la imagen.
     */
    private void postVisibilityToUI() {
        gunView.post(new Runnable() {
            @Override
            public void run() {
                gunView.setVisibility(View.VISIBLE);
            }
        });
    }
}