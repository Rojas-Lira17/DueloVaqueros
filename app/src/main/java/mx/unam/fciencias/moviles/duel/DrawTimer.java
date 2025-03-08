package mx.unam.fciencias.moviles.duel;

import android.view.View;
import android.widget.ImageView;

public class DrawTimer implements Runnable{

    private ImageView gunView;
    private final byte COUNT_TO;

    public DrawTimer(ImageView gunView, byte countTo){
        this.gunView = gunView;
        COUNT_TO = countTo;
    }

    @Override
    public void run(){
        byte counter = 0;
        // ciclo mientras no se alcance el limite
        while (counter < COUNT_TO){
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){

            }
            counter++;
        }
        postVisibilityToUI();
    }

    private void postVisibilityToUI(){
        gunView.post(new Runnable(){
            @Override
            public void run(){
                gunView.setVisibility(View.VISIBLE);
            }
        });
    }
}
