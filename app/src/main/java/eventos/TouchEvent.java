package eventos;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import technow.com.vision.R;

/**
 * Created by Tautvydas on 22/04/2016.
 */
public class TouchEvent implements View.OnTouchListener {
    private static int PULSADO = 0;
    private static int SOLTADO = 1;
    private static int SCROLL = 3;
    public static int posicionSeleccionada;
    private RecyclerView recyclerView;

    private GestureDetector gestureDetector;

    public TouchEvent(GestureDetector gestureDetector,RecyclerView recyclerView) {
        this.gestureDetector = gestureDetector;
        this.recyclerView=recyclerView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //le añadimos un evento en caso de que quiera eliminar una imagen mediante dialogo
        gestureDetector.onTouchEvent(event);
        if (event.getAction() == PULSADO) {
            //obtenemos la posicion del elemento seleccionado
            posicionSeleccionada=recyclerView.findContainingViewHolder(v).getAdapterPosition();
            v.setBackgroundResource(R.drawable.borde_encima);
        } else if (event.getAction() == SOLTADO) {
            v.setBackgroundResource(R.drawable.borde);
        } else if (event.getAction() == SCROLL) {
            v.setBackgroundResource(R.drawable.borde);
        }
        return true;
    }
}
